import slugify from 'slugify';
import { subMinutes } from 'date-fns';
import { PaginationResponse, User } from 'types/Types';
import { UserRole } from 'types/Enums';

export const urlEncode = (text = '') => slugify(text, { lower: true, strict: true, locale: 'nb' });

export const isUserAdmin = (user?: User) => (user ? user.roles?.some((role) => role.name === UserRole.ADMIN) : false);

// Add leading zero to numbers below 10. Ex: 2 -> 02, 12 -> 12
const addLeadingZero = (number: number) => (number < 10 ? '0' + number : number);

export const formatTime = (date: Date) => {
  return `${addLeadingZero(date.getHours())}:${addLeadingZero(date.getMinutes())}`;
};

export const formatDate = (date: Date) => {
  const isDifferentYear = date.getFullYear() !== new Date().getFullYear();
  return `${getDay(date.getDay())} ${date.getDate()} ${getMonth(date.getMonth())} ${isDifferentYear ? date.getFullYear() : ''} - kl. ${formatTime(date)}`;
};

export const getTimeSince = (date: Date) => {
  const ms = new Date().getTime() - date.getTime();
  const sec = Number((ms / 1000).toFixed(0));
  const min = Number((ms / (1000 * 60)).toFixed(0));
  const hrs = Number((ms / (1000 * 60 * 60)).toFixed(0));
  const days = Number((ms / (1000 * 60 * 60 * 24)).toFixed(0));
  if (sec < 60) {
    return `${sec} sekunder siden`;
  } else if (min < 60) {
    return `${min} minutter siden`;
  } else if (hrs < 24) {
    return `${hrs} timer siden`;
  } else if (days < 7) {
    return `${days} dager siden`;
  } else {
    return formatDate(date);
  }
};

export const getDay = (day: number) => {
  switch (day) {
    case 0:
      return 'Søndag';
    case 1:
      return 'Mandag';
    case 2:
      return 'Tirsdag';
    case 3:
      return 'Onsdag';
    case 4:
      return 'Torsdag';
    case 5:
      return 'Fredag';
    case 6:
      return 'Lørdag';
    default:
      return day;
  }
};
export const getMonth = (month: number) => {
  switch (month) {
    case 0:
      return 'jan';
    case 1:
      return 'feb';
    case 2:
      return 'mars';
    case 3:
      return 'april';
    case 4:
      return 'mai';
    case 5:
      return 'juni';
    case 6:
      return 'juli';
    case 7:
      return 'aug';
    case 8:
      return 'sep';
    case 9:
      return 'okt';
    case 10:
      return 'nov';
    case 11:
      return 'des';
    default:
      return month;
  }
};

/**
 * Transforms a date to when UTC+0 will be at the same time.
 * Ex.: 15:00 in UTC+2 is transformed to 17:00 as UTC+0 at that time will be 15:00
 * @param date - The date to transform
 * @returns A new date
 */
export const dateAsUTC = (date: Date): Date => {
  return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes()));
};

/**
 * Transforms a date to UTC+0.
 * Ex.: 15:00 in UTC+2 is transformed to 13:00 as thats the equivalent time in UTC+0
 * @param date - The date to transform
 * @returns A new date
 */
export const dateToUTC = (date: Date): Date => {
  return subMinutes(date, -date.getTimezoneOffset());
};

export const getNextPaginationPage = (pagination: PaginationResponse<unknown>) => {
  return pagination.last ? null : pagination.number + 1;
};
