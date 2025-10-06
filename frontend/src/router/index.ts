import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '仪表盘',
          icon: 'HomeFilled',
          showInMenu: true
        }
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: {
          title: '用户管理',
          icon: 'User',
          showInMenu: true,
          permissions: ['user:view']
        }
      },
      {
        path: '/sites',
        name: 'Sites',
        component: () => import('@/views/Sites.vue'),
        meta: {
          title: '站点管理',
          icon: 'OfficeBuilding',
          showInMenu: true,
          permissions: ['site:view']
        }
      },
      {
        path: '/contents',
        name: 'Contents',
        component: () => import('@/views/Contents.vue'),
        meta: {
          title: '内容管理',
          icon: 'Document',
          showInMenu: true,
          permissions: ['content:list']  // 修改为content:list，审批员有此权限
        }
      },
      {
        path: '/categories',
        name: 'Categories',
        component: () => import('@/views/Categories.vue'),
        meta: {
          title: '分类管理',
          icon: 'FolderOpened',
          showInMenu: true,
          permissions: ['category:list']
        }
      },
      {
        path: '/workflows',
        name: 'Workflows',
        component: () => import('@/views/Workflows.vue'),
        meta: {
          title: '工作流管理',
          icon: 'Connection',
          showInMenu: true,
          permissions: ['workflow:list']  // 修改为workflow:list，审批员有此权限
        }
      },
      {
        path: '/workflows/:id/design',
        name: 'WorkflowDesigner',
        component: () => import('@/views/WorkflowDesigner.vue'),
        meta: {
          title: '工作流设计器',
          icon: 'Connection',
          showInMenu: false
        }
      },
      {
        path: '/workflow-instances',
        name: 'WorkflowInstances',
        component: () => import('@/views/WorkflowInstances.vue'),
        meta: {
          title: '工作流实例',
          icon: 'List',
          showInMenu: true,
          permissions: ['workflow:list']  // 修改为workflow:list，审批员有此权限
        }
      },
      {
        path: '/workflow-tasks',
        name: 'WorkflowTasks',
        component: () => import('@/views/WorkflowTasks.vue'),
        meta: {
          title: '我的任务',
          icon: 'Checked',
          showInMenu: true,
          permissions: ['workflow:list']  // 修改为workflow:list，审批员有此权限
        }
      },
      {
        path: '/logs',
        name: 'Logs',
        component: () => import('@/views/Logs.vue'),
        meta: {
          title: '系统日志',
          icon: 'Document',
          showInMenu: true,
          permissions: ['log:list']
        }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心',
          icon: 'User',
          showInMenu: false
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)

  if (authRequired && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router

