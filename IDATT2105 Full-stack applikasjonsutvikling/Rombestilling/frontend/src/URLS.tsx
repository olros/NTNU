const AUTH = '/auth/';

export const AUTH_RELATIVE_ROUTES = {
  LOGIN: '',
  FORGOT_PASSWORD: 'forgot-password/',
  RESET_PASSWORD: 'reset-password/',
};

export const AUTH_ROUTES = {
  LOGIN: AUTH + AUTH_RELATIVE_ROUTES.LOGIN,
  FORGOT_PASSWORD: AUTH + AUTH_RELATIVE_ROUTES.FORGOT_PASSWORD,
  RESET_PASSWORD: AUTH + AUTH_RELATIVE_ROUTES.RESET_PASSWORD,
};

export default {
  LANDING: '/',
  GROUPS: '/groups/',
  PROFILE: '/profile/',
  ROOMS: '/rooms/',
  USERS: '/users/',
  ...AUTH_ROUTES,
};
