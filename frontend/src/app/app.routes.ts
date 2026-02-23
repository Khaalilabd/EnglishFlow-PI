import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { AppLayoutComponent } from './shared/layout/app-layout/app-layout.component';
import { EcommerceComponent } from './pages/dashboard/ecommerce/ecommerce.component';
import { CalenderComponent } from './pages/calender/calender.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { FormElementsComponent } from './pages/forms/form-elements/form-elements.component';
import { BasicTablesComponent } from './pages/tables/basic-tables/basic-tables.component';
import { BlankComponent } from './pages/blank/blank.component';
import { InvoicesComponent } from './pages/invoices/invoices.component';
import { LineChartComponent } from './pages/charts/line-chart/line-chart.component';
import { BarChartComponent } from './pages/charts/bar-chart/bar-chart.component';
import { AlertsComponent } from './pages/ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './pages/ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './pages/ui-elements/badges/badges.component';
import { ButtonsComponent } from './pages/ui-elements/buttons/buttons.component';
import { ImagesComponent } from './pages/ui-elements/images/images.component';
import { VideosComponent } from './pages/ui-elements/videos/videos.component';
import { SignInComponent } from './pages/auth-pages/sign-in/sign-in.component';
import { SignUpComponent } from './pages/auth-pages/sign-up/sign-up.component';
import { NotFoundComponent } from './pages/other-page/not-found/not-found.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password.component';
import { ActivateComponent } from './auth/activate/activate.component';
import { OAuth2CallbackComponent } from './auth/oauth2-callback/oauth2-callback.component';
import { StudentLayoutComponent } from './shared/layout/student-layout/student-layout.component';

export const routes: Routes = [
  // Page d'accueil Jungle in English
  {
    path: '',
    component: HomeComponent
  },
  
  // Diagnostic tool
  {
    path: 'diagnostic',
    loadComponent: () => import('./diagnostic.component').then(m => m.DiagnosticComponent),
    title: 'API Diagnostic | Jungle in English'
  },
  
  // Student Panel avec layout et sidebar
  {
    path: 'user-panel',
    component: StudentLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/student-panel/student-panel.component').then(m => m.StudentPanelComponent),
        title: 'My Dashboard | Jungle in English'
      },
      {
        path: 'courses',
        loadComponent: () => import('./pages/student-panel/courses/courses.component').then(m => m.CoursesComponent),
        title: 'My Courses | Jungle in English'
      },
      {
        path: 'course-catalog',
        loadComponent: () => import('./pages/student-panel/course-catalog/course-catalog.component').then(m => m.CourseCatalogComponent),
        title: 'Explore Courses | Jungle in English'
      },
      {
        path: 'course/:id',
        loadComponent: () => import('./pages/student-panel/course-view/course-view.component').then(m => m.CourseViewComponent),
        title: 'Course Details | Jungle in English'
      },
      {
        path: 'lesson/:id',
        loadComponent: () => import('./pages/student-panel/lesson-view/lesson-view.component').then(m => m.LessonViewComponent),
        title: 'Lesson | Jungle in English'
      },
      {
        path: 'schedule',
        loadComponent: () => import('./pages/student-panel/schedule/schedule.component').then(m => m.ScheduleComponent),
        title: 'My Schedule | Jungle in English'
      },
      {
        path: 'messages',
        loadComponent: () => import('./pages/student-panel/messages/messages.component').then(m => m.MessagesComponent),
        title: 'Messages | Jungle in English'
      },
      {
        path: 'assignments',
        loadComponent: () => import('./pages/student-panel/assignments/assignments.component').then(m => m.AssignmentsComponent),
        title: 'Assignments | Jungle in English'
      },
      {
        path: 'grades',
        loadComponent: () => import('./pages/student-panel/grades/grades.component').then(m => m.GradesComponent),
        title: 'My Grades | Jungle in English'
      },
      {
        path: 'quizzes',
        loadComponent: () => import('./pages/student-panel/quizzes/quizzes.component').then(m => m.QuizzesComponent),
        title: 'Quizzes | Jungle in English'
      },
      {
        path: 'ebooks',
        loadComponent: () => import('./pages/student-panel/ebooks/ebooks.component').then(m => m.EbooksComponent),
        title: 'Ebooks | Jungle in English'
      },
      {
        path: 'progress',
        loadComponent: () => import('./pages/student-panel/progress/progress.component').then(m => m.ProgressComponent),
        title: 'My Progress | Jungle in English'
      },
      {
        path: 'subscription',
        loadComponent: () => import('./pages/student-panel/subscription/subscription.component').then(m => m.SubscriptionComponent),
        title: 'My Subscription | Jungle in English'
      },
      {
        path: 'support',
        loadComponent: () => import('./pages/student-panel/support/support.component').then(m => m.SupportComponent),
        title: 'Help & Support | Jungle in English'
      },
      {
        path: 'complaints',
        loadComponent: () => import('./pages/student-panel/complaints/complaints.component').then(m => m.ComplaintsComponent),
        title: 'Complaints | Jungle in English'
      },
      {
        path: 'settings',
        loadComponent: () => import('./pages/student-panel/settings/settings.component').then(m => m.SettingsComponent),
        title: 'Settings | Jungle in English'
      }
    ]
  },
  
  // Pages d'authentification (hors du layout dashboard)
  {
    path: 'login',
    component: LoginComponent,
    title: 'Login | Jungle in English'
  },
  {
    path: 'register',
    component: RegisterComponent,
    title: 'Register | Jungle in English'
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    title: 'Forgot Password | Jungle in English'
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    title: 'Reset Password | Jungle in English'
  },
  {
    path: 'activate',
    component: ActivateComponent,
    title: 'Activate Account | Jungle in English'
  },
  {
    path: 'oauth2/callback',
    component: OAuth2CallbackComponent,
    title: 'Signing in... | Jungle in English'
  },
  {
    path: 'signin',
    component: SignInComponent,
    title: 'Sign In | Jungle in English'
  },
  {
    path: 'signup',
    component: SignUpComponent,
    title: 'Sign Up | Jungle in English'
  },
  
  // Dashboard avec toutes ses routes
  {
    path: 'dashboard',
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        component: EcommerceComponent,
        title: 'Dashboard | Jungle in English'
      },
      {
        path: 'calendar',
        component: CalenderComponent,
        title: 'Calendar | Jungle in English Dashboard'
      },
      {
        path: 'profile',
        component: ProfileComponent,
        title: 'Profile | Jungle in English Dashboard'
      },
      {
        path: 'users/students',
        loadComponent: () => import('./pages/users/students/students.component').then(m => m.StudentsComponent),
        title: 'Students | Jungle in English Dashboard'
      },
      {
        path: 'users/tutors',
        loadComponent: () => import('./pages/users/tutors/tutors.component').then(m => m.TutorsComponent),
        title: 'Tutors | Jungle in English Dashboard'
      },
      {
        path: 'users/tutors/create',
        loadComponent: () => import('./pages/users/create-tutor/create-tutor.component').then(m => m.CreateTutorComponent),
        title: 'Create Tutor | Jungle in English Dashboard'
      },
      {
        path: 'form-elements',
        component: FormElementsComponent,
        title: 'Form Elements | Jungle in English Dashboard'
      },
      {
        path: 'basic-tables',
        component: BasicTablesComponent,
        title: 'Tables | Jungle in English Dashboard'
      },
      {
        path: 'blank',
        component: BlankComponent,
        title: 'Blank Page | Jungle in English Dashboard'
      },
      {
        path: 'invoice',
        component: InvoicesComponent,
        title: 'Invoice | Jungle in English Dashboard'
      },
      {
        path: 'line-chart',
        component: LineChartComponent,
        title: 'Line Chart | Jungle in English Dashboard'
      },
      {
        path: 'bar-chart',
        component: BarChartComponent,
        title: 'Bar Chart | Jungle in English Dashboard'
      },
      {
        path: 'alerts',
        component: AlertsComponent,
        title: 'Alerts | Jungle in English Dashboard'
      },
      {
        path: 'avatars',
        component: AvatarElementComponent,
        title: 'Avatars | Jungle in English Dashboard'
      },
      {
        path: 'badge',
        component: BadgesComponent,
        title: 'Badges | Jungle in English Dashboard'
      },
      {
        path: 'buttons',
        component: ButtonsComponent,
        title: 'Buttons | Jungle in English Dashboard'
      },
      {
        path: 'images',
        component: ImagesComponent,
        title: 'Images | Jungle in English Dashboard'
      },
      {
        path: 'videos',
        component: VideosComponent,
        title: 'Videos | Jungle in English Dashboard'
      },
      {
        path: 'courses',
        loadComponent: () => import('./pages/courses/courses-list/courses-list.component').then(m => m.CoursesListComponent),
        title: 'Courses | Jungle in English Dashboard'
      },
      {
        path: 'courses/create',
        loadComponent: () => import('./pages/courses/courses-create/courses-create.component').then(m => m.CoursesCreateComponent),
        title: 'Create Course | Jungle in English Dashboard'
      },
      {
        path: 'courses/:id',
        loadComponent: () => import('./pages/courses/courses-details/courses-details.component').then(m => m.CoursesDetailsComponent),
        title: 'Course Details | Jungle in English Dashboard'
      },
      {
        path: 'courses/:id/edit',
        loadComponent: () => import('./pages/courses/courses-update/courses-update.component').then(m => m.CoursesUpdateComponent),
        title: 'Edit Course | Jungle in English Dashboard'
      },
      {
        path: 'chapters',
        loadComponent: () => import('./pages/courses/chapters-list/chapters-list.component').then(m => m.ChaptersListComponent),
        title: 'Chapters | Jungle in English Dashboard'
      },
      {
        path: 'chapters/create',
        loadComponent: () => import('./pages/courses/chapters-create/chapters-create.component').then(m => m.ChaptersCreateComponent),
        title: 'Create Chapter | Jungle in English Dashboard'
      },
      {
        path: 'chapters/update/:id',
        loadComponent: () => import('./pages/courses/chapters-update/chapters-update.component').then(m => m.ChaptersUpdateComponent),
        title: 'Edit Chapter | Jungle in English Dashboard'
      },
      {
        path: 'chapters/:id',
        loadComponent: () => import('./pages/courses/chapter-details/chapter-details.component').then(m => m.ChapterDetailsComponent),
        title: 'Chapter Details | Jungle in English Dashboard'
      },
      {
        path: 'lessons',
        loadComponent: () => import('./pages/courses/lessons-list/lessons-list.component').then(m => m.LessonsListComponent),
        title: 'Lessons | Jungle in English Dashboard'
      },
      {
        path: 'lessons/create',
        loadComponent: () => import('./pages/courses/lessons-create/lessons-create.component').then(m => m.LessonsCreateComponent),
        title: 'Create Lesson | Jungle in English Dashboard'
      },
      {
        path: 'lessons/update/:id',
        loadComponent: () => import('./pages/courses/lessons-update/lessons-update.component').then(m => m.LessonsUpdateComponent),
        title: 'Edit Lesson | Jungle in English Dashboard'
      }
    ]
  },
  
  // Page 404 - DOIT ÊTRE EN DERNIER
  {
    path: '**',
    component: NotFoundComponent,
    title: 'Page Not Found | Jungle in English'
  }
];
