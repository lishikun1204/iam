import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginPage from '@/pages/LoginPage.vue'
import OAuthCallbackPage from '@/pages/OAuthCallbackPage.vue'
import ConsoleLayout from '@/pages/ConsoleLayout.vue'
import HomePage from '@/pages/HomePage.vue'
import UsersPage from '@/pages/rbac/UsersPage.vue'
import RolesPage from '@/pages/rbac/RolesPage.vue'
import PermissionsPage from '@/pages/rbac/PermissionsPage.vue'
import DeptsPage from '@/pages/rbac/DeptsPage.vue'

export type DynamicChildRoute = RouteRecordRaw & {
  meta?: {
    title?: string
    icon?: string
    requiredAuthority?: string
  }
}

const BASE_ROUTES: RouteRecordRaw[] = [
  { path: '/', redirect: '/console' },
  { path: '/login', name: 'login', component: LoginPage },
  { path: '/oauth/callback', name: 'oauthCallback', component: OAuthCallbackPage },
  {
    path: '/console',
    name: 'console',
    component: ConsoleLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'consoleHome', component: HomePage, meta: { title: '概览' } },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

export const router = createRouter({
  history: createWebHistory(),
  routes: BASE_ROUTES,
})

const DYNAMIC_ROUTE_NAMES = new Set<string>()

export function resetDynamicRoutes() {
  for (const name of DYNAMIC_ROUTE_NAMES) {
    router.removeRoute(name)
  }
  DYNAMIC_ROUTE_NAMES.clear()
}

export function applyDynamicRoutes(authorities: string[]) {
  const has = (code: string) => authorities.includes(code)

  const children: DynamicChildRoute[] = []
  if (has('sys:user:read')) {
    children.push({
      path: 'users',
      name: 'consoleUsers',
      component: UsersPage,
      meta: { title: '用户管理', requiredAuthority: 'sys:user:read' },
    })
  }
  if (has('sys:role:read')) {
    children.push({
      path: 'roles',
      name: 'consoleRoles',
      component: RolesPage,
      meta: { title: '角色管理', requiredAuthority: 'sys:role:read' },
    })
  }
  if (has('sys:perm:read')) {
    children.push({
      path: 'permissions',
      name: 'consolePermissions',
      component: PermissionsPage,
      meta: { title: '权限管理', requiredAuthority: 'sys:perm:read' },
    })
  }
  if (has('sys:dept:read')) {
    children.push({
      path: 'depts',
      name: 'consoleDepts',
      component: DeptsPage,
      meta: { title: '部门管理', requiredAuthority: 'sys:dept:read' },
    })
  }

  for (const r of children) {
    if (!r.name) continue
    if (router.hasRoute(r.name)) continue
    router.addRoute('console', r)
    DYNAMIC_ROUTE_NAMES.add(String(r.name))
  }
}

router.beforeEach(async (to) => {
  const store = useAuthStore()
  const requiresAuth = Boolean(to.meta && (to.meta as any).requiresAuth)

  if (requiresAuth && !store.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (store.isLoggedIn && store.authorities.length === 0) {
    try {
      const me = await store.fetchMe()
      applyDynamicRoutes(me.authorities)
    } catch {
      store.clear()
      return { path: '/login' }
    }
  }

  return true
})

export default router
