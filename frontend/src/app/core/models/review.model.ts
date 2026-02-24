export interface CourseReview {
  id?: number;
  courseId: number;
  userId: number;
  userName?: string;
  userAvatar?: string;
  rating: number;
  comment: string;
  helpful?: number;
  createdAt?: string | Date;
  updatedAt?: string | Date;
}

export interface ReviewStats {
  averageRating: number;
  totalReviews: number;
  ratingDistribution: {
    [key: number]: number;
  };
}
