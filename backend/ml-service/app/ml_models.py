"""
Module d'entraînement et d'optimisation des modèles ML
"""
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, GridSearchCV, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    classification_report, confusion_matrix, accuracy_score,
    silhouette_score, davies_bouldin_score
)
import joblib
import json
from datetime import datetime


class StudentClusteringModel:
    """Modèle de clustering pour segmenter les étudiants"""
    
    def __init__(self):
        self.model = None
        self.scaler = StandardScaler()
        self.best_k = None
        
    def find_optimal_k(self, X, k_range=range(2, 11)):
        """Trouve le nombre optimal de clusters"""
        scores = []
        for k in k_range:
            kmeans = KMeans(n_clusters=k, random_state=42, n_init=10)
            labels = kmeans.fit_predict(X)
            score = silhouette_score(X, labels)
            scores.append({'k': k, 'silhouette': score})
        
        best = max(scores, key=lambda x: x['silhouette'])
        self.best_k = best['k']
        return scores
    
    def train(self, X, n_clusters=None):
        """Entraîne le modèle de clustering"""
        X_scaled = self.scaler.fit_transform(X)
        
        if n_clusters is None:
            self.find_optimal_k(X_scaled)
            n_clusters = self.best_k
        
        self.model = KMeans(n_clusters=n_clusters, random_state=42, n_init=10)
        labels = self.model.fit_predict(X_scaled)
        
        # Métriques
        silhouette = silhouette_score(X_scaled, labels)
        davies_bouldin = davies_bouldin_score(X_scaled, labels)
        
        return {
            'n_clusters': n_clusters,
            'silhouette_score': silhouette,
            'davies_bouldin_score': davies_bouldin,
            'labels': labels
        }
    
    def predict(self, X):
        """Prédit le cluster pour de nouvelles données"""
        X_scaled = self.scaler.transform(X)
        return self.model.predict(X_scaled)
    
    def save(self, path='models/clustering_model.pkl'):
        """Sauvegarde le modèle"""
        joblib.dump({'model': self.model, 'scaler': self.scaler}, path)
    
    def load(self, path='models/clustering_model.pkl'):
        """Charge le modèle"""
        data = joblib.load(path)
        self.model = data['model']
        self.scaler = data['scaler']


class StudentPredictionModel:
    """Modèle de prédiction du succès étudiant"""
    
    def __init__(self, model_type='random_forest'):
        self.model_type = model_type
        self.model = None
        self.scaler = StandardScaler()
        self.best_params = None
        
    def _get_model(self):
        """Retourne le modèle selon le type"""
        if self.model_type == 'random_forest':
            return RandomForestClassifier(random_state=42)
        elif self.model_type == 'gradient_boosting':
            return GradientBoostingClassifier(random_state=42)
        elif self.model_type == 'logistic_regression':
            return LogisticRegression(random_state=42, max_iter=1000)
        else:
            raise ValueError(f"Type de modèle inconnu: {self.model_type}")
    
    def optimize_hyperparameters(self, X_train, y_train):
        """Optimise les hyperparamètres avec GridSearchCV"""
        param_grids = {
            'random_forest': {
                'n_estimators': [100, 200],
                'max_depth': [10, 20, None],
                'min_samples_split': [2, 5],
                'min_samples_leaf': [1, 2]
            },
            'gradient_boosting': {
                'n_estimators': [100, 200],
                'learning_rate': [0.01, 0.1],
                'max_depth': [3, 5],
                'min_samples_split': [2, 5]
            },
            'logistic_regression': {
                'C': [0.1, 1.0, 10.0],
                'penalty': ['l2'],
                'solver': ['lbfgs']
            }
        }
        
        model = self._get_model()
        param_grid = param_grids[self.model_type]
        
        grid_search = GridSearchCV(
            model, param_grid, cv=5, scoring='accuracy', n_jobs=-1, verbose=1
        )
        grid_search.fit(X_train, y_train)
        
        self.best_params = grid_search.best_params_
        self.model = grid_search.best_estimator_
        
        return grid_search.best_params_, grid_search.best_score_
    
    def train(self, X_train, y_train, optimize=True):
        """Entraîne le modèle"""
        X_train_scaled = self.scaler.fit_transform(X_train)
        
        if optimize:
            best_params, best_score = self.optimize_hyperparameters(X_train_scaled, y_train)
            return {'best_params': best_params, 'cv_score': best_score}
        else:
            self.model = self._get_model()
            self.model.fit(X_train_scaled, y_train)
            cv_scores = cross_val_score(self.model, X_train_scaled, y_train, cv=5)
            return {'cv_score': cv_scores.mean()}
    
    def evaluate(self, X_test, y_test):
        """Évalue le modèle"""
        X_test_scaled = self.scaler.transform(X_test)
        y_pred = self.model.predict(X_test_scaled)
        
        return {
            'accuracy': accuracy_score(y_test, y_pred),
            'classification_report': classification_report(y_test, y_pred, output_dict=True),
            'confusion_matrix': confusion_matrix(y_test, y_pred).tolist(),
            'predictions': y_pred
        }
    
    def predict(self, X):
        """Prédit pour de nouvelles données"""
        X_scaled = self.scaler.transform(X)
        return self.model.predict(X_scaled)
    
    def predict_proba(self, X):
        """Prédit les probabilités"""
        X_scaled = self.scaler.transform(X)
        return self.model.predict_proba(X_scaled)
    
    def get_feature_importance(self, feature_names):
        """Retourne l'importance des features"""
        if hasattr(self.model, 'feature_importances_'):
            importances = self.model.feature_importances_
            return dict(zip(feature_names, importances))
        return None
    
    def save(self, path='models/prediction_model.pkl'):
        """Sauvegarde le modèle"""
        joblib.dump({
            'model': self.model,
            'scaler': self.scaler,
            'model_type': self.model_type,
            'best_params': self.best_params
        }, path)
    
    def load(self, path='models/prediction_model.pkl'):
        """Charge le modèle"""
        data = joblib.load(path)
        self.model = data['model']
        self.scaler = data['scaler']
        self.model_type = data['model_type']
        self.best_params = data.get('best_params')


def compare_models(X_train, X_test, y_train, y_test):
    """Compare différents modèles de prédiction"""
    models = ['random_forest', 'gradient_boosting', 'logistic_regression']
    results = {}
    
    for model_type in models:
        print(f"\n{'='*50}")
        print(f"Entraînement: {model_type}")
        print('='*50)
        
        model = StudentPredictionModel(model_type=model_type)
        train_results = model.train(X_train, y_train, optimize=True)
        eval_results = model.evaluate(X_test, y_test)
        
        results[model_type] = {
            'train': train_results,
            'test': eval_results
        }
        
        print(f"CV Score: {train_results['cv_score']:.4f}")
        print(f"Test Accuracy: {eval_results['accuracy']:.4f}")
    
    return results


class CourseRecommendationModel:
    """Système de recommandation de cours basé sur le filtrage collaboratif"""
    
    def __init__(self):
        self.student_similarity = None
        self.course_stats = None
        self.interaction_matrix = None
        self.student_ids = None
        self.course_ids = None
        
    def train(self, df):
        """
        Entraîne le système de recommandation
        
        Args:
            df: DataFrame avec colonnes [id_student, code_module, interaction_score]
        """
        # Créer la matrice d'interaction étudiant-cours
        self.interaction_matrix = df.pivot_table(
            index='id_student',
            columns='code_module',
            values='interaction_score',
            fill_value=0
        )
        
        self.student_ids = self.interaction_matrix.index.tolist()
        self.course_ids = self.interaction_matrix.columns.tolist()
        
        # Calculer la similarité entre étudiants (cosine similarity)
        from sklearn.metrics.pairwise import cosine_similarity
        self.student_similarity = cosine_similarity(self.interaction_matrix)
        
        # Statistiques par cours
        self.course_stats = df.groupby('code_module').agg({
            'interaction_score': ['mean', 'count'],
            'final_result': lambda x: (x.isin(['Pass', 'Distinction'])).sum() / len(x) if len(x) > 0 else 0
        }).reset_index()
        
        self.course_stats.columns = ['code_module', 'avg_score', 'nb_students', 'success_rate']
        
        return {
            'n_students': len(self.student_ids),
            'n_courses': len(self.course_ids),
            'sparsity': 1 - (df.shape[0] / (len(self.student_ids) * len(self.course_ids)))
        }
    
    def recommend_for_student(self, student_id, n_recommendations=5, min_students=5):
        """
        Recommande des cours pour un étudiant
        
        Args:
            student_id: ID de l'étudiant
            n_recommendations: Nombre de cours à recommander
            min_students: Nombre minimum d'étudiants ayant suivi le cours
        
        Returns:
            Liste de recommandations avec scores
        """
        if student_id not in self.student_ids:
            # Étudiant inconnu : recommander les cours les plus populaires
            return self._recommend_popular_courses(n_recommendations, min_students)
        
        student_idx = self.student_ids.index(student_id)
        
        # Trouver les étudiants similaires
        similarities = self.student_similarity[student_idx]
        similar_students_idx = np.argsort(similarities)[::-1][1:21]  # Top 20 similaires (excluant lui-même)
        
        # Cours déjà suivis par l'étudiant
        student_courses = set(
            self.interaction_matrix.columns[self.interaction_matrix.iloc[student_idx] > 0]
        )
        
        # Calculer les scores de recommandation
        recommendations = {}
        for course in self.course_ids:
            if course in student_courses:
                continue  # Ne pas recommander un cours déjà suivi
            
            # Score basé sur les étudiants similaires
            course_idx = self.course_ids.index(course)
            weighted_score = 0
            total_weight = 0
            
            for sim_idx in similar_students_idx:
                sim_score = similarities[sim_idx]
                course_score = self.interaction_matrix.iloc[sim_idx, course_idx]
                
                if course_score > 0:
                    weighted_score += sim_score * course_score
                    total_weight += sim_score
            
            if total_weight > 0:
                recommendations[course] = weighted_score / total_weight
        
        # Si aucune recommandation basée sur similarité, utiliser les stats globales
        if not recommendations:
            # Recommander les cours non suivis avec le meilleur taux de réussite
            for course in self.course_ids:
                if course not in student_courses:
                    course_info = self.course_stats[self.course_stats['code_module'] == course].iloc[0]
                    # Score combiné : taux de réussite + popularité
                    recommendations[course] = (
                        course_info['success_rate'] * 0.6 + 
                        (course_info['avg_score'] / 5.0) * 0.4
                    )
        
        # Filtrer par popularité minimale et trier
        course_stats_dict = dict(zip(self.course_stats['code_module'], 
                                     self.course_stats['nb_students']))
        
        filtered_recs = {
            course: score 
            for course, score in recommendations.items()
            if course_stats_dict.get(course, 0) >= min_students
        }
        
        # Si toujours vide après filtrage, réduire le seuil
        if not filtered_recs and recommendations:
            filtered_recs = recommendations
        
        sorted_recs = sorted(filtered_recs.items(), key=lambda x: x[1], reverse=True)
        
        # Enrichir avec les statistiques
        results = []
        for course, score in sorted_recs[:n_recommendations]:
            course_info = self.course_stats[self.course_stats['code_module'] == course].iloc[0]
            results.append({
                'code_module': course,
                'recommendation_score': float(score),
                'avg_interaction': float(course_info['avg_score']),
                'nb_students': int(course_info['nb_students']),
                'success_rate': float(course_info['success_rate'])
            })
        
        return results
    
    def _recommend_popular_courses(self, n_recommendations, min_students):
        """Recommande les cours les plus populaires et réussis"""
        filtered = self.course_stats[self.course_stats['nb_students'] >= min_students].copy()
        
        # Score combiné : popularité + taux de réussite
        filtered['combined_score'] = (
            filtered['avg_score'] * 0.4 + 
            filtered['success_rate'] * 0.6
        )
        
        top_courses = filtered.nlargest(n_recommendations, 'combined_score')
        
        results = []
        for _, row in top_courses.iterrows():
            results.append({
                'code_module': row['code_module'],
                'recommendation_score': float(row['combined_score']),
                'avg_interaction': float(row['avg_score']),
                'nb_students': int(row['nb_students']),
                'success_rate': float(row['success_rate'])
            })
        
        return results
    
    def recommend_for_new_student(self, student_profile, n_recommendations=5):
        """
        Recommande des cours pour un nouvel étudiant basé sur son profil
        
        Args:
            student_profile: dict avec clés comme 'studied_credits', 'avg_score', etc.
            n_recommendations: Nombre de cours à recommander
        """
        # Pour un nouvel étudiant, recommander selon son niveau
        avg_score = student_profile.get('avg_score', 70)
        
        # Filtrer les cours selon le niveau de l'étudiant
        if avg_score >= 75:
            # Étudiant fort : cours avec bon taux de réussite
            filtered = self.course_stats[self.course_stats['success_rate'] >= 0.6].copy()
        elif avg_score >= 60:
            # Étudiant moyen : cours équilibrés
            filtered = self.course_stats[
                (self.course_stats['success_rate'] >= 0.5) & 
                (self.course_stats['success_rate'] <= 0.7)
            ].copy()
        else:
            # Étudiant faible : cours avec meilleur support
            filtered = self.course_stats[self.course_stats['avg_score'] >= 3.5].copy()
        
        if len(filtered) == 0:
            filtered = self.course_stats.copy()
        
        # Trier par score combiné
        filtered['combined_score'] = (
            filtered['avg_score'] * 0.3 + 
            filtered['success_rate'] * 0.7
        )
        
        top_courses = filtered.nlargest(n_recommendations, 'combined_score')
        
        results = []
        for _, row in top_courses.iterrows():
            results.append({
                'code_module': row['code_module'],
                'recommendation_score': float(row['combined_score']),
                'avg_interaction': float(row['avg_score']),
                'nb_students': int(row['nb_students']),
                'success_rate': float(row['success_rate']),
                'reason': self._get_recommendation_reason(avg_score, row)
            })
        
        return results
    
    def _get_recommendation_reason(self, student_score, course_row):
        """Génère une raison pour la recommandation"""
        if student_score >= 75:
            return f"Cours adapté à votre niveau élevé (taux de réussite: {course_row['success_rate']:.0%})"
        elif student_score >= 60:
            return f"Cours équilibré pour progresser (suivi par {course_row['nb_students']} étudiants)"
        else:
            return f"Cours avec bon support pédagogique (score d'interaction: {course_row['avg_score']:.1f}/5)"
    
    def get_course_details(self, course_code):
        """Retourne les détails d'un cours"""
        course_info = self.course_stats[self.course_stats['code_module'] == course_code]
        
        if len(course_info) == 0:
            return None
        
        row = course_info.iloc[0]
        return {
            'code_module': course_code,
            'avg_interaction': float(row['avg_score']),
            'nb_students': int(row['nb_students']),
            'success_rate': float(row['success_rate'])
        }
    
    def save(self, path='models/recommendation_model.pkl'):
        """Sauvegarde le modèle"""
        joblib.dump({
            'student_similarity': self.student_similarity,
            'course_stats': self.course_stats,
            'interaction_matrix': self.interaction_matrix,
            'student_ids': self.student_ids,
            'course_ids': self.course_ids
        }, path)
    
    def load(self, path='models/recommendation_model.pkl'):
        """Charge le modèle"""
        data = joblib.load(path)
        self.student_similarity = data['student_similarity']
        self.course_stats = data['course_stats']
        self.interaction_matrix = data['interaction_matrix']
        self.student_ids = data['student_ids']
        self.course_ids = data['course_ids']


if __name__ == "__main__":
    print("Module de modèles ML chargé avec succès")
