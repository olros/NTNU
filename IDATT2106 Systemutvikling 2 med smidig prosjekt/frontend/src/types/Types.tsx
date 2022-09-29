import { TrainingLevel } from 'types/Enums';

export type RequestResponse = {
  message: string;
};

export type LoginRequestResponse = {
  token: string;
  refreshToken: string;
};

export type RefreshTokenResponse = {
  token: string;
  refreshToken: string;
};

export type PaginationResponse<T> = {
  totalElements: number;
  totalPages: number;
  number: number;
  content: Array<T>;
  empty: boolean;
  last: boolean;
};

export type User = {
  id: string;
  firstName: string;
  surname: string;
  email: string;
  birthDate: string | null;
  level: TrainingLevel | null;
  image: string;
  currentUserIsFollowing: boolean;
  followingCount: number;
  followerCount: number;
};

export type UserList = Pick<User, 'id' | 'firstName' | 'surname' | 'email' | 'image' | 'currentUserIsFollowing'>;

export type UserCreate = Pick<User, 'email' | 'firstName' | 'surname'> & {
  password: string;
};

export type ActivityRequired = Partial<Activity> & Pick<Activity, 'title' | 'startDate' | 'endDate' | 'signupStart' | 'signupEnd'>;

export type Activity = {
  capacity: number;
  closed: boolean;
  creator: UserList | null;
  description: string;
  endDate: string;
  registered: number;
  equipment: Array<{
    name: string;
    amount: number;
  }>;
  geoLocation: LatLng | null;
  hasLiked: boolean;
  hosts: Array<UserList>;
  id: string;
  inviteOnly: boolean;
  level: TrainingLevel;
  likesCount: number;
  images: Array<{
    url: string;
  }>;
  startDate: string;
  signupStart: string;
  signupEnd: string;
  title: string;
};

export type ActivityList = Pick<Activity, 'id' | 'title' | 'closed' | 'startDate' | 'endDate' | 'level' | 'description' | 'images' | 'geoLocation'>;

export type Comment = {
  id: string;
  comment: string;
  user: UserList;
  createdAt: string;
};

export type Registration = {
  user: User;
};

export type FileUploadResponse = {
  data: {
    display_url: string;
  };
};

export type LatLng = {
  lat: number;
  lng: number;
};

export type Post = {
  id: string;
  creator: UserList;
  createdAt: string;
  image: string;
  content: string;
  likesCount: number;
  hasLiked: boolean;
  activity: ActivityList | null;
  commentsCount: number;
};

export type PostCreate = Pick<Post, 'content' | 'image'> & {
  activityId?: string;
};

export type Like = {
  hasLiked: boolean;
};
