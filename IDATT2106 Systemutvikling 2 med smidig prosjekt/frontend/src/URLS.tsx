const AUTH = '/auth/';

export const AUTH_RELATIVE_ROUTES = {
  LOGIN: '',
  SIGNUP: 'sign-up/',
  FORGOT_PASSWORD: 'forgot-password/',
  RESET_PASSWORD: 'reset-password/',
};

export const AUTH_ROUTES = {
  LOGIN: AUTH + AUTH_RELATIVE_ROUTES.LOGIN,
  SIGNUP: AUTH + AUTH_RELATIVE_ROUTES.SIGNUP,
  FORGOT_PASSWORD: AUTH + AUTH_RELATIVE_ROUTES.FORGOT_PASSWORD,
  RESET_PASSWORD: AUTH + AUTH_RELATIVE_ROUTES.RESET_PASSWORD,
};

export default {
  LANDING: '/',
  ABOUT: '/about/',
  ACTIVITIES: '/activities/',
  ADMIN_ACTIVITIES: '/admin/activities/',
  PROFILE: '/profile/',
  USERS: '/users/',
  ...AUTH_ROUTES,
};
