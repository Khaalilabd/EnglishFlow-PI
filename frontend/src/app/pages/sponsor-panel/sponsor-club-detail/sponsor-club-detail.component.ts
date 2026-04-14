import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ClubService } from '../../../core/services/club.service';
import { MemberService } from '../../../core/services/member.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { AuthService } from '../../../core/services/auth.service';
import { ExpenseService } from '../../../core/services/expense.service';
import { EventService, Event as ClubEvent } from '../../../core/services/event.service';
import { Club } from '../../../core/models/club.model';
import { Sponsor } from '../../../core/models/sponsor.model';
import { Expense } from '../../../core/models/expense.model';

@Component({
  selector: 'app-sponsor-club-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gray-50">

      @if (loading) {
        <div class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-10 w-10 border-2 border-[#B37D05] border-t-transparent"></div>
        </div>
      }

      @if (!loading && club) {
        <div class="min-h-screen bg-gray-50 p-6">
          <div class="max-w-7xl mx-auto space-y-6">

            <!-- NAVBAR — same style as clubs-details -->
            <div class="bg-white rounded-2xl shadow-sm border border-gray-100 px-4 py-3">
              <div class="flex items-center gap-2">
                <button (click)="goBack()"
                  class="flex-shrink-0 p-2.5 text-[#B37D05] hover:bg-gray-100 rounded-xl transition-all group"
                  title="Back to Clubs">
                  <svg class="w-4 h-4 transition-transform group-hover:-translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
                  </svg>
                </button>
                <div class="w-px h-6 bg-gray-200 flex-shrink-0"></div>
                <nav class="flex items-center justify-center gap-1 flex-1 overflow-x-auto">
                  <button (click)="activeTab = 'overview'"
                    class="flex items-center gap-2.5 px-5 py-2.5 rounded-xl text-sm font-semibold whitespace-nowrap transition-all duration-200"
                    [ngClass]="activeTab === 'overview' ? 'bg-gradient-to-r from-[#B37D05] to-[#3D3D60] text-white shadow-md' : 'text-gray-500 hover:text-[#B37D05] hover:bg-gray-100'">
                    <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
                    </svg>
                    Overview
                  </button>
                  <button (click)="activeTab = 'transactions'"
                    class="flex items-center gap-2.5 px-5 py-2.5 rounded-xl text-sm font-semibold whitespace-nowrap transition-all duration-200"
                    [ngClass]="activeTab === 'transactions' ? 'bg-gradient-to-r from-[#F6BD60] to-[#C84630] text-white shadow-md' : 'text-gray-500 hover:text-[#C84630] hover:bg-gray-100'">
                    💰 My Transactions
                    @if (transactions.length > 0) {
                      <span class="px-1.5 py-0.5 rounded-full text-xs font-bold"
                        [ngClass]="activeTab === 'transactions' ? 'bg-white/20 text-white' : 'bg-orange-100 text-orange-600'">
                        {{ transactions.length }}
                      </span>
                    }
                  </button>

                  <button (click)="activeTab = 'events'"
                    class="flex items-center gap-2.5 px-5 py-2.5 rounded-xl text-sm font-semibold whitespace-nowrap transition-all duration-200"
                    [ngClass]="activeTab === 'events' ? 'bg-gradient-to-r from-purple-500 to-indigo-600 text-white shadow-md' : 'text-gray-500 hover:text-purple-600 hover:bg-gray-100'">
                    <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                    </svg>
                    Events
                    @if (clubEvents.length > 0) {
                      <span class="px-1.5 py-0.5 rounded-full text-xs font-bold"
                        [ngClass]="activeTab === 'events' ? 'bg-white/20 text-white' : 'bg-purple-100 text-purple-600'">
                        {{ clubEvents.length }}
                      </span>
                    }
                  </button>
                </nav>
              </div>
            </div>

            <!-- Sponsor badge -->
            @if (myRequest) {
              <div class="inline-flex items-center gap-2 px-3 py-1.5 bg-orange-50 border border-orange-200 rounded-full text-sm font-semibold text-orange-700">
                🤝 You are sponsoring this club
                <span class="font-black text-[#B37D05]">· {{ myRequest.contributionAmount }} DT</span>
              </div>
            }

            <!-- Overview Tab -->
            @if (activeTab === 'overview') {
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div class="lg:col-span-2 space-y-6">
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    @if (club.image) {
                      <img [src]="club.image" [alt]="club.name" class="w-full h-56 object-cover">
                    } @else {
                      <div class="h-56 bg-gradient-to-br from-[#B37D05] to-[#3D3D60] flex items-center justify-center text-7xl">
                        {{ getCategoryIcon(club.category) }}
                      </div>
                    }
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <span class="px-3 py-1 bg-[#F7EDE2] text-[#B37D05] text-xs font-bold rounded-full">
                          {{ getCategoryIcon(club.category) }} {{ club.category }}
                        </span>
                        <span class="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full">✅ Active</span>
                      </div>
                      <h1 class="text-2xl font-black text-gray-900 mb-2">{{ club.name }}</h1>
                      @if (club.description) {
                        <p class="text-gray-600 leading-relaxed">{{ club.description }}</p>
                      }
                    </div>
                  </div>
                  @if (club.objective) {
                    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                      <h3 class="text-lg font-bold text-gray-800 mb-3">🎯 Objective</h3>
                      <p class="text-gray-600 leading-relaxed">{{ club.objective }}</p>
                    </div>
                  }
                </div>
                <div class="space-y-4">
                  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 space-y-4">
                    <h3 class="text-lg font-bold text-gray-800">Club Info</h3>
                    <div class="space-y-3">
                      <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                        <span class="text-sm text-gray-500">Members</span>
                        <span class="font-bold text-gray-800">{{ club.currentMembersCount || 0 }} / {{ club.maxMembers }}</span>
                      </div>
                      <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                        <span class="text-sm text-gray-500">Entry Fee</span>
                        <span class="font-bold text-gray-800">{{ club.registrationFee || 0 }} DT</span>
                      </div>
                      <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                        <span class="text-sm text-gray-500">Category</span>
                        <span class="font-bold text-gray-800">{{ club.category }}</span>
                      </div>
                    </div>
                  </div>
                  @if (myRequest) {
                    <div class="bg-gradient-to-br from-[#B37D05] to-[#3D3D60] rounded-2xl p-6 text-white">
                      <h3 class="text-lg font-bold mb-3">🤝 My Sponsorship</h3>
                      <p class="text-3xl font-black text-[#F6BD60]">{{ myRequest.contributionAmount }} DT</p>
                      <p class="text-white/70 text-sm mt-1">Approved contribution</p>
                    </div>
                  }
                </div>
              </div>
            }

            <!-- Transactions Tab -->
            @if (activeTab === 'transactions') {              <div class="space-y-6">

                <!-- Sponsorship summary card -->
                @if (myRequest) {
                  <div class="bg-gradient-to-r from-[#B37D05] to-[#3D3D60] rounded-2xl p-5 text-white">
                    <div class="flex items-center justify-between">
                      <div>
                        <p class="text-white/70 text-sm mb-1">Your total sponsorship</p>
                        <p class="text-3xl font-black text-[#F6BD60]">{{ myRequest.contributionAmount }} DT</p>
                      </div>
                      <div class="text-right">
                        <p class="text-white/70 text-sm mb-1">Used by treasurer</p>
                        <p class="text-3xl font-black text-orange-300">{{ clubExpensesTotal }} DT</p>
                      </div>
                      <div class="text-right">
                        <p class="text-white/70 text-sm mb-1">Remaining</p>
                        <p class="text-3xl font-black" [ngClass]="remainingAmount >= 0 ? 'text-green-300' : 'text-red-300'">
                          {{ remainingAmount }} DT
                        </p>
                      </div>
                    </div>
                    <!-- Progress bar -->
                    <div class="mt-4">
                      <div class="w-full bg-white/20 rounded-full h-2">
                        <div class="h-2 rounded-full bg-[#F6BD60] transition-all duration-500"
                          [style.width.%]="myRequest.contributionAmount ? (clubExpensesTotal / myRequest.contributionAmount) * 100 : 0">
                        </div>
                      </div>
                    </div>
                  </div>
                }

                <!-- Club expenses list -->
                <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                  <h2 class="text-xl font-bold text-gray-800 mb-5">� How Your Sponsorship Was Used</h2>

                  @if (clubExpenses.length === 0) {
                    <div class="text-center py-12">
                      <div class="text-5xl mb-3">💸</div>
                      <p class="text-gray-600 font-semibold">No expenses recorded yet</p>
                      <p class="text-gray-400 text-sm mt-1">The treasurer hasn't recorded any expenses from your sponsorship yet.</p>
                    </div>
                  } @else {
                    <div class="space-y-3">
                      @for (exp of clubExpenses; track exp.id) {
                        <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-xl border border-gray-100 hover:bg-white hover:shadow-sm transition-all">
                          <!-- Date badge -->
                          <div class="flex-shrink-0 w-12 text-center">
                            <div class="bg-white rounded-xl border border-gray-200 px-2 py-1.5 shadow-sm">
                              <p class="text-xs font-bold text-[#B37D05] leading-none">{{ formatDate(exp.expenseDate).split(' ')[0] }}</p>
                              <p class="text-xs text-gray-400 leading-none mt-0.5">{{ formatDate(exp.expenseDate).split(' ')[1] }}</p>
                            </div>
                          </div>
                          <!-- Info -->
                          <div class="flex-1 min-w-0">
                            <p class="font-bold text-gray-800 truncate">{{ exp.designation }}</p>
                            @if (exp.notes && !exp.notes.includes('SPONSORSHIP_INCOME')) {
                              <p class="text-xs text-gray-400 mt-0.5 truncate">{{ exp.notes }}</p>
                            }
                          </div>
                          <!-- Amount -->
                          <div class="flex-shrink-0 text-right">
                            <p class="text-base font-black text-[#C84630]">{{ exp.amount.toFixed(2) }}</p>
                            <p class="text-xs text-gray-400 font-medium">DT</p>
                          </div>
                        </div>
                      }

                      <!-- Total -->
                      <div class="flex items-center justify-between p-4 bg-gradient-to-r from-[#C84630] to-[#a83820] rounded-xl text-white mt-2">
                        <span class="font-semibold">Total expenses from sponsorship</span>
                        <span class="text-2xl font-black">{{ clubExpensesTotal.toFixed(2) }} DT</span>
                      </div>
                    </div>
                  }
                </div>
              </div>
            }

            <!-- Events Tab -->
            @if (activeTab === 'events') {
              <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
                <h2 class="text-xl font-bold text-gray-800 mb-5">📅 Club Events</h2>

                @if (clubEvents.length === 0) {
                  <div class="text-center py-12">
                    <div class="text-5xl mb-3">📭</div>
                    <p class="text-gray-600 font-semibold">No events yet</p>
                    <p class="text-gray-400 text-sm mt-1">This club hasn't organized any events yet.</p>
                  </div>
                } @else {
                  <div class="space-y-4">
                    @for (event of clubEvents; track event.id) {
                      <div class="flex items-start gap-4 p-5 bg-gray-50 rounded-2xl border border-gray-100 hover:bg-white hover:shadow-md transition-all">

                        <!-- Event image or icon -->
                        <div class="flex-shrink-0 w-16 h-16 rounded-xl overflow-hidden bg-gradient-to-br from-purple-500 to-indigo-600 flex items-center justify-center">
                          @if (event.image) {
                            <img [src]="event.image" [alt]="event.title" class="w-full h-full object-cover">
                          } @else {
                            <span class="text-2xl">{{ getEventTypeIcon(event.type) }}</span>
                          }
                        </div>

                        <!-- Info -->
                        <div class="flex-1 min-w-0">
                          <!-- Title + badges -->
                          <div class="flex flex-wrap items-center gap-2 mb-2">
                            <p class="font-bold text-gray-800">{{ event.title }}</p>

                            <!-- Status badge -->
                            <span class="px-2 py-0.5 text-xs font-bold rounded-full"
                              [ngClass]="getEventStatusClass(event.status || '')">
                              {{ event.status }}
                            </span>

                            <!-- Format badge -->
                            @if (isOnline(event)) {
                              <span class="px-2 py-0.5 bg-blue-100 text-blue-700 text-xs font-bold rounded-full flex items-center gap-1">
                                🌐 En ligne
                              </span>
                            } @else {
                              <span class="px-2 py-0.5 bg-green-100 text-green-700 text-xs font-bold rounded-full flex items-center gap-1">
                                📍 Présentiel
                              </span>
                            }

                            <!-- Type badge -->
                            <span class="px-2 py-0.5 bg-purple-100 text-purple-700 text-xs font-bold rounded-full">
                              {{ event.type }}
                            </span>
                          </div>

                          <!-- Meta info -->
                          <div class="flex flex-wrap items-center gap-3 text-xs text-gray-500 mb-2">
                            <span class="flex items-center gap-1">
                              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                              </svg>
                              {{ event.startDate | date:'mediumDate' }}
                            </span>
                            <span class="flex items-center gap-1">
                              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
                              </svg>
                              {{ event.currentParticipants || 0 }} / {{ event.maxParticipants }} participants
                            </span>
                          </div>

                          <!-- Location (IN_PERSON only) -->
                          @if (!isOnline(event) && event.location) {
                            <div class="flex items-center gap-1.5 text-xs text-gray-600 bg-green-50 border border-green-100 rounded-lg px-3 py-1.5 mb-2 w-fit">
                              <svg class="w-3.5 h-3.5 text-green-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                              </svg>
                              <span class="font-medium text-green-700">{{ event.location }}</span>
                            </div>
                          }

                          <!-- Description -->
                          @if (event.description) {
                            <p class="text-xs text-gray-400 line-clamp-2">{{ event.description }}</p>
                          }
                        </div>

                        <!-- Live Session button (ONLINE only) — observer mode -->
                        @if (isOnline(event)) {
                          <div class="flex-shrink-0">
                            <button (click)="joinLiveSession(event.id!)"
                              class="flex items-center gap-2 px-4 py-2.5 bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-xs font-bold rounded-xl hover:opacity-90 transition-opacity shadow-md cursor-pointer">
                              <span class="w-2 h-2 bg-white rounded-full animate-pulse"></span>
                              🎥 Live Session
                            </button>
                          </div>
                        }

                      </div>
                    }
                  </div>
                }
              </div>
            }

          </div>
        </div>
      }
    </div>
  `
})
export class SponsorClubDetailComponent implements OnInit {
  club: Club | null = null;
  myRequest: Sponsor | null = null;
  transactions: Sponsor[] = [];
  clubExpenses: Expense[] = [];
  clubEvents: ClubEvent[] = [];
  loading = false;
  activeTab: 'overview' | 'transactions' | 'events' = 'overview';
  currentUserId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private memberService: MemberService,
    private sponsorService: SponsorService,
    private authService: AuthService,
    private expenseService: ExpenseService,
    private eventService: EventService
  ) {}

  ngOnInit() {
    this.currentUserId = this.authService.currentUserValue?.id || null;
    const clubId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData(clubId);
  }

  loadData(clubId: number) {
    this.loading = true;
    const userId = this.currentUserId;
    forkJoin({
      club: this.clubService.getClubById(clubId),
      sponsorships: userId ? this.sponsorService.getSponsorsByUser(userId) : of([]),
      expenses: this.expenseService.getExpensesByClub(clubId),
      events: this.eventService.getAllEvents().pipe(
        map((evts: ClubEvent[]) => evts.filter(e => e.clubId === clubId))
      )
    }).subscribe({
      next: ({ club, sponsorships, expenses, events }) => {
        this.club = club;
        this.transactions = (sponsorships as Sponsor[]).filter(s => s.clubId === clubId);
        this.myRequest = this.transactions.find(s => s.status === 'APPROVED') || null;
        this.clubExpenses = (expenses as Expense[]).filter(e =>
          e.source === 'SPONSORSHIP' &&
          !e.notes?.includes('SPONSORSHIP_INCOME') &&
          !e.designation?.includes('Sponsorship received from') &&
          !e.designation?.includes('Sponsorship income from')
        );
        this.clubEvents = events as ClubEvent[];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  get clubExpensesTotal(): number {
    return this.clubExpenses.reduce((sum, e) => sum + e.amount, 0);
  }

  get remainingAmount(): number {
    return (this.myRequest?.contributionAmount || 0) - this.clubExpensesTotal;
  }

  get transactionsTotal(): number {
    return this.transactions.reduce((sum, t) => sum + (t.contributionAmount || 0), 0);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  getEventTypeIcon(type: string): string {
    const m: Record<string, string> = { WORKSHOP: '🔧', SEMINAR: '🎤', SOCIAL: '🎉' };
    return m[type] || '📅';
  }

  getEventStatusClass(status: string): string {
    const m: Record<string, string> = {
      APPROVED: 'bg-green-100 text-green-700',
      PENDING:  'bg-yellow-100 text-yellow-700',
      REJECTED: 'bg-red-100 text-red-700'
    };
    return m[status] || 'bg-gray-100 text-gray-600';
  }

  isOnline(event: ClubEvent): boolean {
    return event.format === 'ONLINE';
  }

  joinLiveSession(eventId: number) {
    const clubId = this.route.snapshot.paramMap.get('id');
    this.router.navigate(['/live', eventId], {
      queryParams: { ghost: true, returnTo: `/sponsor-panel/clubs/${clubId}` }
    });
  }

  goBack() { this.router.navigate(['/sponsor-panel/clubs']); }

  getCategoryIcon(cat: string): string {
    const m: Record<string, string> = {
      CONVERSATION:'💬',BOOK:'📚',DRAMA:'🎭',WRITING:'✍️',GRAMMAR:'📝',
      VOCABULARY:'🔤',READING:'📖',LISTENING:'🎧',SPEAKING:'🎤',
      PRONUNCIATION:'🗣️',BUSINESS:'💼',ACADEMIC:'🎓'
    };
    return m[cat] || '🏛️';
  }
}
