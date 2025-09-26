import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register', 
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/sites',
    name: 'Sites',
    component: () => import('../views/Sites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/sites/:id',
    name: 'SiteDetail',
    component: () => import('../views/SiteDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/content',
    name: 'Content',
    component: () => import('../views/Content.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/content/create',
    name: 'ContentCreate',
    component: () => import('../views/ContentCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/content/:id/edit',
    name: 'ContentEdit',
    component: () => import('../views/ContentEdit.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('../views/Categories.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/Users.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('../views/Test.vue')
  },
  {
    path: '/simple-login',
    name: 'SimpleLogin',
    component: () => import('../views/SimpleLogin.vue'),
    meta: { requiresAuth: false }
  }
]

export default routes
