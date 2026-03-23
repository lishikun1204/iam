import type { CustomRoute, ElegantConstRoute, ElegantRoute } from '@elegant-router/types';
import { generatedRoutes } from '../elegant/routes';
import { layouts, views } from '../elegant/imports';
import { transformElegantRoutesToVueRoutes } from '../elegant/transform';

/**
 * custom routes
 *
 * @link https://github.com/soybeanjs/elegant-router?tab=readme-ov-file#custom-route
 */
const customRoutes: CustomRoute[] = [
  {
    name: 'rbac',
    path: '/rbac',
    component: 'layout.base',
    meta: {
      title: 'rbac',
      i18nKey: 'route.rbac',
      icon: 'mdi:account-key',
      order: 2
    },
    children: [
      {
        name: 'rbac_users',
        path: 'users',
        component: 'view.rbac_users',
        meta: {
          title: 'rbac_users',
          i18nKey: 'route.rbac_users'
        }
      },
      {
        name: 'rbac_roles',
        path: 'roles',
        component: 'view.rbac_roles',
        meta: {
          title: 'rbac_roles',
          i18nKey: 'route.rbac_roles'
        }
      },
      {
        name: 'rbac_permissions',
        path: 'permissions',
        component: 'view.rbac_permissions',
        meta: {
          title: 'rbac_permissions',
          i18nKey: 'route.rbac_permissions'
        }
      },
      {
        name: 'rbac_depts',
        path: 'depts',
        component: 'view.rbac_depts',
        meta: {
          title: 'rbac_depts',
          i18nKey: 'route.rbac_depts'
        }
      }
    ]
  }
];

/** create routes when the auth route mode is static */
export function createStaticRoutes() {
  const constantRoutes: ElegantRoute[] = [];

  const authRoutes: ElegantRoute[] = [];

  const mergedRoutes = new Map<string, ElegantRoute>();

  [...generatedRoutes, ...customRoutes].forEach(item => {
    if (item.name === 'oauth-callback') {
      return;
    }
    mergedRoutes.set(item.name, item);
  });

  [...mergedRoutes.values()].forEach(item => {
    if (item.meta?.constant) {
      constantRoutes.push(item);
    } else {
      authRoutes.push(item);
    }
  });

  return {
    constantRoutes,
    authRoutes
  };
}

/**
 * Get auth vue routes
 *
 * @param routes Elegant routes
 */
export function getAuthVueRoutes(routes: ElegantConstRoute[]) {
  return transformElegantRoutesToVueRoutes(routes, layouts, views);
}
