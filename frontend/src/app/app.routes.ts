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
import { TutorLayoutComponent } from './shared/layout/tutor-layout/tutor-layout.component';

export const routes: Routes = [
  // Page d'accueil Jungle in English
  {
    path: '',
    component: HomeComponent
  },
  
  // Page publique des clubs
  {
    path: 'clubs',
    loadComponent: () => import('./pages/public-clubs/public-clubs.component').then(m => m.PublicClubsComponent),
    title: 'Clubs | Jungle in English'
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
        path: 'clubs',
        loadComponent: () => import('./pages/student-panel/clubs/clubs.component').then(m => m.ClubsComponent),
        title: 'My Clubs | Jungle in English'
      },
      {
        path: 'club-requests',
        loadComponent: () => import('./pages/student-panel/club-requests/club-requests.component').then(m => m.ClubRequestsComponent),
        title: 'Club Requests | Jungle in English'
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
  
  // Tutor Panel avec layout et sidebar
  {
    path: 'tutor-panel',
    component: TutorLayoutComponent,
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/tutor-panel/tutor-panel.component').then(m => m.TutorPanelComponent),
        title: 'Tutor Dashboard | Jungle in English'
      },
      {
        path: 'courses',
        loadComponent: () => import('./pages/student-panel/courses/courses.component').then(m => m.CoursesComponent),
        title: 'My Courses | Jungle in English'
      },
      {
        path: 'quiz-management',
        loadComponent: () => import('./pages/tutor-panel/quiz-management/quiz-management.component').then(m => m.QuizManagementComponent),
        title: 'Quiz Management | Jungle in English'
      },
      {
        path: 'quiz-management/create',
        loadComponent: () => import('./pages/tutor-panel/quiz-create/quiz-create.component').then(m => m.QuizCreateComponent),
        title: 'Create Quiz | Jungle in English'
      },
      {
        path: 'quiz-management/edit/:id',
        loadComponent: () => import('./pages/tutor-panel/quiz-create/quiz-create.component').then(m => m.QuizCreateComponent),
        title: 'Edit Quiz | Jungle in English'
      },
      {
        path: 'ebooks',
        loadComponent: () => import('./pages/student-panel/ebooks/ebooks.component').then(m => m.EbooksComponent),
        title: 'Ebooks | Jungle in English'
      },
      {
        path: 'schedule',
        loadComponent: () => import('./pages/student-panel/schedule/schedule.component').then(m => m.ScheduleComponent),
        title: 'Schedule | Jungle in English'
      },
      {
        path: 'students',
        loadComponent: () => import('./pages/users/students/students.component').then(m => m.StudentsComponent),
        title: 'My Students | Jungle in English'
      },
      {
        path: 'assignments',
        loadComponent: () => import('./pages/student-panel/assignments/assignments.component').then(m => m.AssignmentsComponent),
        title: 'Assignments | Jungle in English'
      },
      {
        path: 'analytics',
        loadComponent: () => import('./pages/student-panel/progress/progress.component').then(m => m.ProgressComponent),
        title: 'Analytics | Jungle in English'
      },
      {
        path: 'messages',
        loadComponent: () => import('./pages/student-panel/messages/messages.component').then(m => m.MessagesComponent),
        title: 'Messages | Jungle in English'
      },
      {
        path: 'support',
        loadComponent: () => import('./pages/student-panel/support/support.component').then(m => m.SupportComponent),
        title: 'Help & Support | Jungle in English'
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
        path: 'settings',
        loadComponent: () => import('./pages/dashboard/settings/settings.component').then(m => m.SettingsComponent),
        title: 'Settings Profile | Jungle in English Dashboard'
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
        path: 'users/academic-affairs',
        loadComponent: () => import('./pages/users/academic-affairs/academic-affairs.component').then(m => m.AcademicAffairsComponent),
        title: 'Academic Affairs | Jungle in English Dashboard'
      },
      {
        path: 'statistics',
        loadComponent: () => import('./pages/dashboard/statistics/statistics.component').then(m => m.StatisticsComponent),
        title: 'Statistics | Jungle in English Dashboard'
      },
      {
        path: 'schedules',
        loadComponent: () => import('./pages/dashboard/schedules/schedules.component').then(m => m.SchedulesComponent),
        title: 'Schedules | Jungle in English Dashboard'
      },
      {
        path: 'schedules/manage',
        loadComponent: () => import('./pages/dashboard/schedules-manage/schedules-manage.component').then(m => m.SchedulesManageComponent),
        title: 'Manage Schedules | Jungle in English Dashboard'
      },
      {
        path: 'refunds',
        loadComponent: () => import('./pages/dashboard/refunds/refunds.component').then(m => m.RefundsComponent),
        title: 'Manage Refunds | Jungle in English Dashboard'
      },
      {
        path: 'payments',
        loadComponent: () => import('./pages/dashboard/payments/payments.component').then(m => m.PaymentsComponent),
        title: 'Manage Payments | Jungle in English Dashboard'
      },
      {
        path: 'subscriptions',
        loadComponent: () => import('./pages/dashboard/subscriptions/subscriptions.component').then(m => m.SubscriptionsComponent),
        title: 'Manage Subscriptions | Jungle in English Dashboard'
      },
      {
        path: 'events',
        loadComponent: () => import('./pages/dashboard/events/events.component').then(m => m.EventsComponent),
        title: 'Events | Jungle in English Dashboard'
      },
      {
        path: 'events/manage',
        loadComponent: () => import('./pages/dashboard/events-manage/events-manage.component').then(m => m.EventsManageComponent),
        title: 'Manage Events | Jungle in English Dashboard'
      },
      {
        path: 'complaints',
        loadComponent: () => import('./pages/dashboard/complaints/complaints.component').then(m => m.ComplaintsComponent),
        title: 'Manage Complaints | Jungle in English Dashboard'
      },
      {
        path: 'feedbacks',
        loadComponent: () => import('./pages/dashboard/feedbacks/feedbacks.component').then(m => m.FeedbacksComponent),
        title: 'Manage Feedbacks | Jungle in English Dashboard'
      },
      {
        path: 'clubs',
        loadComponent: () => import('./pages/clubs/clubs-list/clubs-list.component').then(m => m.ClubsListComponent),
        title: 'Clubs | Jungle in English Dashboard'
      },
      {
        path: 'clubs/manage',
        loadComponent: () => import('./pages/clubs/clubs-manage/clubs-manage.component').then(m => m.ClubsManageComponent),
        title: 'Manage Clubs | Jungle in English Dashboard'
      },
      {
        path: 'clubs/requests',
        loadComponent: () => import('./pages/clubs/club-requests-admin/club-requests-admin.component').then(m => m.ClubRequestsAdminComponent),
        title: 'Club Requests | Jungle in English Dashboard'
      },
      {
        path: 'clubs/create',
        loadComponent: () => import('./pages/clubs/club-create/club-create.component').then(m => m.ClubCreateComponent),
        title: 'Create Club | Jungle in English Dashboard'
      },
      {
        path: 'clubs/:id',
        loadComponent: () => import('./pages/clubs/club-detail/club-detail.component').then(m => m.ClubDetailComponent),
        title: 'Club Details | Jungle in English Dashboard'
      },
      {
        path: 'clubs/:id/edit',
        loadComponent: () => import('./pages/clubs/club-edit/club-edit.component').then(m => m.ClubEditComponent),
        title: 'Edit Club | Jungle in English Dashboard'
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
