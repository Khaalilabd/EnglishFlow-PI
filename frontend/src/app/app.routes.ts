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

export const routes: Routes = [
  // Page d'accueil EnglishFlow
  {
    path: '',
    component: HomeComponent
  },
  
  // Pages d'authentification (hors du layout dashboard)
  {
    path: 'login',
    component: LoginComponent,
    title: 'Login | EnglishFlow'
  },
  {
    path: 'register',
    component: RegisterComponent,
    title: 'Register | EnglishFlow'
  },
  {
    path: 'signin',
    component: SignInComponent,
    title: 'Sign In | EnglishFlow'
  },
  {
    path: 'signup',
    component: SignUpComponent,
    title: 'Sign Up | EnglishFlow'
  },
  
  // Dashboard avec toutes ses routes
  {
    path: 'dashboard',
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        component: EcommerceComponent,
        title: 'Dashboard | EnglishFlow'
      },
      {
        path: 'calendar',
        component: CalenderComponent,
        title: 'Calendar | EnglishFlow Dashboard'
      },
      {
        path: 'profile',
        component: ProfileComponent,
        title: 'Profile | EnglishFlow Dashboard'
      },
      {
        path: 'form-elements',
        component: FormElementsComponent,
        title: 'Form Elements | EnglishFlow Dashboard'
      },
      {
        path: 'basic-tables',
        component: BasicTablesComponent,
        title: 'Tables | EnglishFlow Dashboard'
      },
      {
        path: 'blank',
        component: BlankComponent,
        title: 'Blank Page | EnglishFlow Dashboard'
      },
      {
        path: 'invoice',
        component: InvoicesComponent,
        title: 'Invoice | EnglishFlow Dashboard'
      },
      {
        path: 'line-chart',
        component: LineChartComponent,
        title: 'Line Chart | EnglishFlow Dashboard'
      },
      {
        path: 'bar-chart',
        component: BarChartComponent,
        title: 'Bar Chart | EnglishFlow Dashboard'
      },
      {
        path: 'alerts',
        component: AlertsComponent,
        title: 'Alerts | EnglishFlow Dashboard'
      },
      {
        path: 'avatars',
        component: AvatarElementComponent,
        title: 'Avatars | EnglishFlow Dashboard'
      },
      {
        path: 'badge',
        component: BadgesComponent,
        title: 'Badges | EnglishFlow Dashboard'
      },
      {
        path: 'buttons',
        component: ButtonsComponent,
        title: 'Buttons | EnglishFlow Dashboard'
      },
      {
        path: 'images',
        component: ImagesComponent,
        title: 'Images | EnglishFlow Dashboard'
      },
      {
        path: 'videos',
        component: VideosComponent,
        title: 'Videos | EnglishFlow Dashboard'
      }
    ]
  },
  
  // Page 404 - DOIT ÊTRE EN DERNIER
  {
    path: '**',
    component: NotFoundComponent,
    title: 'Page Not Found | EnglishFlow'
  }
];
