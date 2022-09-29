import { UserRole } from './Enums';

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
  phoneNumber: string;
  image: string;
  expirationDate: string;
  roles: Array<{
    name: UserRole;
  }>;
};

export type UserList = Pick<User, 'id' | 'firstName' | 'surname' | 'email' | 'image' | 'phoneNumber'>;

export type UserCreate = Pick<User, 'email' | 'firstName' | 'surname' | 'phoneNumber'> & Partial<Pick<User, 'expirationDate'>>;

export type Section = {
  id: string;
  name: string;
  capacity: number;
  description: string;
  image: string;
  type: 'room' | 'section';
  parent: SectionChild;
  children: Array<SectionChild>;
};

export type SectionChild = Omit<SectionList, 'parent' | 'children'>;

export type SectionList = Omit<Section, 'description' | 'image'>;

export type SectionCreate = Omit<Section, 'id'> & {
  parentId?: string;
};

export type Statistics = {
  nrOfReservation: number;
  hoursOfReservation: number;
  daysWithReservation: number;
  userReservationCount: number;
};

export type Reservation = {
  id: string;
  section: Omit<SectionList, 'parent' | 'children'>;
  fromTime: string;
  toTime: string;
  text: string;
  nrOfPeople: number;
  entityId: string;
} & (
  | {
      type: 'user';
      user: UserList;
    }
  | {
      type: 'group';
      group: Group;
    }
);

export type ReservationCreate = Pick<Reservation, 'nrOfPeople' | 'text' | 'fromTime' | 'toTime' | 'entityId' | 'type'> & {
  userId: string;
};

export type Group = {
  id: string;
  name: string;
  isMember: boolean;
  creator: UserList;
};

export type GroupCreate = Pick<Group, 'name'>;

export type FileUploadResponse = {
  data: {
    display_url: string;
  };
};
