package com.englishflow.community.service;

import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DataInitializationService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public void initializeCategories() {
        if (categoryRepository.count() > 0) {
            return; // Already initialized
        }
        
        // Général
        Category general = createCategory("Général", "Discussions générales et annonces", "fa-home", "primary");
        createSubCategory(general, "Présentation des étudiants", "Présentez-vous, parlez de votre niveau et de vos objectifs");
        createSubCategory(general, "Annonces de l'école", "Nouvelles importantes, changements d'horaires, rappels");
        createSubCategory(general, "Questions générales", "Pour tout ce qui ne rentre pas dans une autre catégorie");
        
        // Discussions linguistiques
        Category linguistic = createCategory("Discussions linguistiques", "Grammaire, vocabulaire, prononciation et expression", "fa-book-open", "accent-navy");
        createSubCategory(linguistic, "Grammaire & Vocabulaire", "Posez des questions sur la grammaire, apprenez de nouveaux mots");
        createSubCategory(linguistic, "Prononciation & Accent", "Conseils, astuces, enregistrements pour corriger la prononciation");
        createSubCategory(linguistic, "Expression écrite", "Partagez des textes, des essais pour recevoir des retours");
        createSubCategory(linguistic, "Expression orale", "Organisez des sessions de discussion avec d'autres étudiants");
        
        // Clubs
        Category clubs = createCategory("Clubs", "Rejoignez nos clubs thématiques", "fa-users", "accent-orange");
        createSubCategory(clubs, "Club Lecture", "Discussion sur des livres en anglais, recommandations");
        createSubCategory(clubs, "Club Cinéma / Séries", "Parlez de films et séries en VO, analysez dialogues et vocabulaire");
        createSubCategory(clubs, "Club Conversation", "Groupes de conversation informelle pour pratiquer l'anglais");
        createSubCategory(clubs, "Club Culture & Voyages", "Découvrez la culture des pays anglophones, partagez vos expériences");
        
        // Événements
        Category events = createCategory("Événements", "Ateliers, compétitions et rencontres", "fa-calendar-alt", "accent-red");
        createSubCategory(events, "Ateliers et conférences", "Discussions sur les ateliers de l'école, partages de notes");
        createSubCategory(events, "Compétitions & Challenges", "Concours de vocabulaire, quiz, jeux linguistiques");
        createSubCategory(events, "Sorties & Rencontres", "Planification des événements, visites culturelles, cafés linguistiques");
        
        // Ressources et Aide
        Category resources = createCategory("Ressources et Aide", "Partagez et trouvez des ressources", "fa-lightbulb", "secondary");
        createSubCategory(resources, "Partage de ressources", "Liens, vidéos, livres, podcasts en anglais");
        createSubCategory(resources, "Aide entre étudiants", "Tutorat, révisions collectives, questions-réponses");
        
        categoryRepository.saveAll(Arrays.asList(general, linguistic, clubs, events, resources));
    }
    
    private Category createCategory(String name, String description, String icon, String color) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setColor(color);
        return category;
    }
    
    private void createSubCategory(Category category, String name, String description) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setDescription(description);
        subCategory.setCategory(category);
        category.getSubCategories().add(subCategory);
    }
}
