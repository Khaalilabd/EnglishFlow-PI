import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface DashboardStats {
  users: {
    totalStudents: number;
    totalTutors: number;
    totalAcademics: number;
    activeUsers: number;
    newStudentsThisMonth: number;
    newTutorsThisMonth: number;
  };
  courses: {
    totalCourses: number;
    activeCourses: number;
    totalLessons: number;
    totalChapters: number;
    newCoursesThisMonth: number;
  };
  clubs: {
    totalClubs: number;
    activeClubs: number;
    totalMembers: number;
    newClubsThisWeek: number;
  };
  recruitment: {
    totalApplications: number;
    pendingApplications: number;
    acceptedApplications: number;
    rejectedApplications: number;
  };
  engagement: {
    activeSessionsToday: number;
    forumPostsThisWeek: number;
    complaintsOpen: number;
    eventsUpcoming: number;
  };
  revenue?: {
    totalRevenue: number;
    monthlyRevenue: number;
    pendingPayments: number;
    refundsProcessed: number;
  };
}

export interface ChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string;
    fill?: boolean;
  }[];
}

export interface TopPerformer {
  id: number;
  name: string;
  avatar?: string;
  metric: string;
  value: number;
  trend: 'up' | 'down' | 'stable';
}

export interface RecentActivity {
  id: number;
  userName: string;
  avatar?: string;
  action: string;
  time: string;
  type: 'enrollment' | 'course' | 'club' | 'complaint' | 'payment' | 'forum';
  metadata?: any;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardStatsService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    return forkJoin({
      users: this.getUserStats(),
      courses: this.getCourseStats(),
      clubs: this.getClubStats(),
      recruitment: this.getRecruitmentStats(),
      engagement: this.getEngagementStats(),
      revenue: this.getRevenueStats()
    }).pipe(
      catchError(error => {
        console.error('Error fetching dashboard stats:', error);
        return of(this.getMockStats());
      })
    );
  }

  private getUserStats(): Observable<any> {
    return forkJoin({
      students: this.http.get<any[]>(`${this.baseUrl}/api/users/students`).pipe(catchError(() => of([]))),
      tutors: this.http.get<any[]>(`${this.baseUrl}/api/users/tutors`).pipe(catchError(() => of([]))),
      academics: this.http.get<any[]>(`${this.baseUrl}/api/users/academic-affairs`).pipe(catchError(() => of([])))
    }).pipe(
      map(data => ({
        totalStudents: data.students.length,
        totalTutors: data.tutors.length,
        totalAcademics: data.academics.length,
        activeUsers: data.students.filter((s: any) => s.active).length + data.tutors.filter((t: any) => t.active).length,
        newStudentsThisMonth: this.countNewThisMonth(data.students),
        newTutorsThisMonth: this.countNewThisMonth(data.tutors)
      }))
    );
  }

  private getCourseStats(): Observable<any> {
    return this.http.get<any[]>(`${this.baseUrl}/api/courses`).pipe(
      map(courses => ({
        totalCourses: courses.length,
        activeCourses: courses.filter(c => c.active).length,
        totalLessons: courses.reduce((sum, c) => sum + (c.lessonsCount || 0), 0),
        totalChapters: courses.reduce((sum, c) => sum + (c.chaptersCount || 0), 0),
        newCoursesThisMonth: this.countNewThisMonth(courses)
      })),
      catchError(() => of({
        totalCourses: 0,
        activeCourses: 0,
        totalLessons: 0,
        totalChapters: 0,
        newCoursesThisMonth: 0
      }))
    );
  }

  private getClubStats(): Observable<any> {
    return this.http.get<any[]>(`${this.baseUrl}/api/clubs`).pipe(
      map(clubs => ({
        totalClubs: clubs.length,
        activeClubs: clubs.filter(c => c.active).length,
        totalMembers: clubs.reduce((sum, c) => sum + (c.membersCount || 0), 0),
        newClubsThisWeek: this.countNewThisWeek(clubs)
      })),
      catchError(() => of({
        totalClubs: 0,
        activeClubs: 0,
        totalMembers: 0,
        newClubsThisWeek: 0
      }))
    );
  }

  private getRecruitmentStats(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/recruitment/statistics`).pipe(
      map(stats => ({
        totalApplications: stats.total || 0,
        pendingApplications: stats.submitted + stats.underReview || 0,
        acceptedApplications: stats.accepted || 0,
        rejectedApplications: stats.rejected || 0
      })),
      catchError(() => of({
        totalApplications: 0,
        pendingApplications: 0,
        acceptedApplications: 0,
        rejectedApplications: 0
      }))
    );
  }

  private getEngagementStats(): Observable<any> {
    // Mock data for now - can be replaced with real endpoints
    return of({
      activeSessionsToday: Math.floor(Math.random() * 100) + 50,
      forumPostsThisWeek: Math.floor(Math.random() * 500) + 200,
      complaintsOpen: Math.floor(Math.random() * 20) + 5,
      eventsUpcoming: Math.floor(Math.random() * 15) + 3
    });
  }

  private getRevenueStats(): Observable<any> {
    // Mock data for now - can be replaced with real payment endpoints
    return of({
      totalRevenue: Math.floor(Math.random() * 100000) + 50000,
      monthlyRevenue: Math.floor(Math.random() * 20000) + 10000,
      pendingPayments: Math.floor(Math.random() * 50) + 10,
      refundsProcessed: Math.floor(Math.random() * 10) + 2
    });
  }

  getStudentGrowthChart(): Observable<ChartData> {
    // Generate last 6 months data
    const months = this.getLastMonths(6);
    const data = months.map(() => Math.floor(Math.random() * 100) + 50);
    
    return of({
      labels: months,
      datasets: [{
        label: 'New Students',
        data: data,
        backgroundColor: 'rgba(45, 87, 87, 0.1)',
        borderColor: '#2D5757',
        fill: true
      }]
    });
  }

  getRevenueChart(): Observable<ChartData> {
    const months = this.getLastMonths(6);
    const data = months.map(() => Math.floor(Math.random() * 10000) + 5000);
    
    return of({
      labels: months,
      datasets: [{
        label: 'Revenue ($)',
        data: data,
        backgroundColor: 'rgba(246, 189, 96, 0.2)',
        borderColor: '#F6BD60',
        fill: true
      }]
    });
  }

  getCourseDistributionChart(): Observable<ChartData> {
    return of({
      labels: ['Active', 'Draft', 'Archived'],
      datasets: [{
        label: 'Courses',
        data: [156, 23, 45],
        backgroundColor: ['#2D5757', '#F6BD60', '#C84630']
      }]
    });
  }

  getTopTutors(): Observable<TopPerformer[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/users/tutors`).pipe(
      map(tutors => tutors.slice(0, 5).map((tutor, index): TopPerformer => ({
        id: tutor.id,
        name: `${tutor.firstName} ${tutor.lastName}`,
        avatar: tutor.profilePhotoUrl,
        metric: 'Students',
        value: Math.floor(Math.random() * 50) + 10,
        trend: (index % 3 === 0 ? 'up' : index % 3 === 1 ? 'down' : 'stable')
      }))),
      catchError(() => of([] as TopPerformer[]))
    );
  }

  getRecentActivities(): Observable<RecentActivity[]> {
    // Mock data - can be replaced with real activity feed
    return of([
      { id: 1, userName: 'John Doe', avatar: 'https://i.pravatar.cc/40?img=1', action: 'Enrolled in Business English', time: '5 min ago', type: 'enrollment' },
      { id: 2, userName: 'Sarah Smith', avatar: 'https://i.pravatar.cc/40?img=2', action: 'Completed Grammar Basics', time: '12 min ago', type: 'course' },
      { id: 3, userName: 'Mike Johnson', avatar: 'https://i.pravatar.cc/40?img=3', action: 'Joined Conversation Club', time: '25 min ago', type: 'club' },
      { id: 4, userName: 'Emily Davis', avatar: 'https://i.pravatar.cc/40?img=4', action: 'Submitted a complaint', time: '1 hour ago', type: 'complaint' },
      { id: 5, userName: 'Alex Brown', avatar: 'https://i.pravatar.cc/40?img=5', action: 'Posted in forum', time: '2 hours ago', type: 'forum' },
      { id: 6, userName: 'Lisa Wilson', avatar: 'https://i.pravatar.cc/40?img=6', action: 'Made a payment', time: '3 hours ago', type: 'payment' }
    ]);
  }

  private countNewThisMonth(items: any[]): number {
    const now = new Date();
    const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    return items.filter(item => {
      const createdAt = new Date(item.createdAt);
      return createdAt >= firstDayOfMonth;
    }).length;
  }

  private countNewThisWeek(items: any[]): number {
    const now = new Date();
    const firstDayOfWeek = new Date(now.setDate(now.getDate() - now.getDay()));
    return items.filter(item => {
      const createdAt = new Date(item.createdAt);
      return createdAt >= firstDayOfWeek;
    }).length;
  }

  private getLastMonths(count: number): string[] {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const result = [];
    const now = new Date();
    
    for (let i = count - 1; i >= 0; i--) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      result.push(months[date.getMonth()]);
    }
    
    return result;
  }

  private getMockStats(): DashboardStats {
    return {
      users: {
        totalStudents: 1234,
        totalTutors: 89,
        totalAcademics: 12,
        activeUsers: 247,
        newStudentsThisMonth: 45,
        newTutorsThisMonth: 5
      },
      courses: {
        totalCourses: 156,
        activeCourses: 142,
        totalLessons: 890,
        totalChapters: 345,
        newCoursesThisMonth: 8
      },
      clubs: {
        totalClubs: 45,
        activeClubs: 38,
        totalMembers: 567,
        newClubsThisWeek: 3
      },
      recruitment: {
        totalApplications: 234,
        pendingApplications: 23,
        acceptedApplications: 156,
        rejectedApplications: 55
      },
      engagement: {
        activeSessionsToday: 89,
        forumPostsThisWeek: 456,
        complaintsOpen: 12,
        eventsUpcoming: 8
      },
      revenue: {
        totalRevenue: 85420,
        monthlyRevenue: 15230,
        pendingPayments: 23,
        refundsProcessed: 5
      }
    };
  }
}
