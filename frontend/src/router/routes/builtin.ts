import type { RouteRecordRaw } from 'vue-router';
import type { CustomRoute } from '@elegant-router/types';
import { layouts, views } from '../elegant/imports';
import { getRoutePath } from '../elegant/transform';

export const ROOT_ROUTE: CustomRoute = {
  name: 'root',
  path: '/',
  redirect: getRoutePath(import.meta.env.VITE_ROUTE_HOME) || '/home',
  meta: {
    title: 'root',
    constant: true
  }
};

const NOT_FOUND_ROUTE: CustomRoute = {
  name: 'not-found',
  path: '/:pathMatch(.*)*',
  component: 'layout.blank$view.404',
  meta: {
    title: 'not-found',
    constant: true
  }
};

/** builtin routes, it must be constant and setup in vue-router */
const builtinRoutes: CustomRoute[] = [ROOT_ROUTE, NOT_FOUND_ROUTE];

/** create builtin vue routes */
export function createBuiltinVueRoutes() {
  const root: RouteRecordRaw = {
    name: 'root',
    path: '/',
    redirect: getRoutePath(import.meta.env.VITE_ROUTE_HOME) || '/home',
    meta: {
      title: 'root',
      constant: true
    }
  };

  const oauthCallback: RouteRecordRaw = {
    path: '/oauth/callback',
    component: layouts.blank,
    meta: {
      title: 'oauth-callback',
      i18nKey: 'route.oauth-callback',
      constant: true,
      hideInMenu: true
    },
    children: [
      {
        name: 'oauth-callback',
        path: '',
        component: views['oauth-callback'],
        meta: {
          title: 'oauth-callback',
          i18nKey: 'route.oauth-callback',
          constant: true,
          hideInMenu: true
        }
      }
    ]
  };

  const notFound: RouteRecordRaw = {
    path: '/:pathMatch(.*)*',
    component: layouts.blank,
    meta: {
      title: 'not-found',
      constant: true
    },
    children: [
      {
        name: 'not-found',
        path: '',
        component: views['404'],
        meta: {
          title: 'not-found',
          constant: true
        }
      }
    ]
  };

  return [root, oauthCallback, notFound];
}
