import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Course {
  id: number;
  title: string;
  instructor: string;
  progress: number;
  icon: string;
  color: string;
  students: string[];
  lessons: number;
  duration: string;
}

interface Activity {
  id: number;
  studentName: string;
  avatar: string;
  date: string;
  grade: string;
  type: string;
}

interface UpcomingClass {
  id: number;
  time: string;
  title: string;
  instructor: string;
  date: string;
  icon: string;
  color: string;
}

interface Stat {
  title: string;
  value: string;
  icon: string;
  color: string;
  change: string;
  changeType: 'up' | 'down';
}

@Component({
  selector: 'app-student-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-panel.component.html',
  styleUrls: ['./student-panel.component.scss']
})
export class StudentPanelComponent implements OnInit {
  currentUser = {
    firstName: 'Shariar',
    lastName: 'Hossain',
    avatar: 'https://i.pravatar.cc/150?img=12'
  };

  stats: Stat[] = [
    {
      title: 'Courses Enrolled',
      value: '3',
      icon: '📚',
      color: 'from-blue-500 to-blue-600',
      change: '+2 this month',
      changeType: 'up'
    },
    {
      title: 'Hours Learned',
      value: '24.5',
      icon: '⏱️',
      color: 'from-green-500 to-green-600',
      change: '+5.2 this week',
      changeType: 'up'
    },
    {
      title: 'Assignments',
      value: '2',
      icon: '📝',
      color: 'from-amber-500 to-amber-600',
      change: 'Due soon',
      changeType: 'down'
    },
    {
      title: 'Average Grade',
      value: '85%',
      icon: '🎯',
      color: 'from-purple-500 to-purple-600',
      change: '+3% improvement',
      changeType: 'up'
    }
  ];

  currentMonth = 'JULY 2021';
  selectedDate = 5;

  courses: Course[] = [
    {
      id: 1,
      title: 'Grammar Basics',
      instructor: 'By Sarah Johnson',
      progress: 50,
      icon: '📚',
      color: 'bg-amber-100',
      students: ['https://i.pravatar.cc/40?img=1', 'https://i.pravatar.cc/40?img=2', 'https://i.pravatar.cc/40?img=3'],
      lessons: 12,
      duration: '8 weeks'
    },
    {
      id: 2,
      title: 'Conversation Practice',
      instructor: 'By Michael Brown',
      progress: 30,
      icon: '💬',
      color: 'bg-yellow-100',
      students: ['https://i.pravatar.cc/40?img=4', 'https://i.pravatar.cc/40?img=5', 'https://i.pravatar.cc/40?img=6'],
      lessons: 10,
      duration: '6 weeks'
    },
    {
      id: 3,
      title: 'Business English',
      instructor: 'By Emily Davis',
      progress: 80,
      icon: '💼',
      color: 'bg-green-100',
      students: ['https://i.pravatar.cc/40?img=7', 'https://i.pravatar.cc/40?img=8', 'https://i.pravatar.cc/40?img=9'],
      lessons: 15,
      duration: '10 weeks'
    }
  ];

  activities: Activity[] = [
    { id: 1, studentName: 'Arlene McCoy', avatar: 'https://i.pravatar.cc/40?img=10', date: '12/02/25', grade: 'Final Grade', type: 'Activity' },
    { id: 2, studentName: 'Marvin McKinney', avatar: 'https://i.pravatar.cc/40?img=11', date: '12/02/25', grade: 'Final Grade', type: 'Activity' },
    { id: 3, studentName: 'Devon Lane', avatar: 'https://i.pravatar.cc/40?img=12', date: '12/02/25', grade: 'Final Grade', type: 'Activity' },
    { id: 4, studentName: 'Theresa Webb', avatar: 'https://i.pravatar.cc/40?img=13', date: '12/02/25', grade: 'Final Grade', type: 'Activity' },
    { id: 5, studentName: 'Guy Hawkins', avatar: 'https://i.pravatar.cc/40?img=14', date: '12/02/25', grade: 'Final Grade', type: 'Activity' }
  ];

  upcomingClasses: UpcomingClass[] = [
    { id: 1, time: '10:00', title: 'Business English', instructor: 'July 09, 11:30pm', date: '', icon: '💼', color: 'bg-amber-100' },
    { id: 2, time: '11:00', title: 'Grammar Basics', instructor: 'July 09, 11:30pm', date: '', icon: '📚', color: 'bg-amber-100' },
    { id: 3, time: '13:00', title: 'Pronunciation', instructor: 'July 06, 11:30pm', date: '', icon: '🎤', color: 'bg-amber-100' },
    { id: 4, time: '15:00', title: 'Conversation', instructor: 'July 09, 11:30pm', date: '', icon: '💬', color: 'bg-amber-100' },
    { id: 5, time: '17:00', title: 'Vocabulary', instructor: 'July 09, 11:30pm', date: '', icon: '📖', color: 'bg-amber-100' }
  ];

  calendarDays: number[] = [];

  ngOnInit() {
    this.generateCalendar();
  }

  generateCalendar() {
    for (let i = 1; i <= 31; i++) {
      this.calendarDays.push(i);
    }
  }

  selectDate(day: number) {
    this.selectedDate = day;
  }
}
