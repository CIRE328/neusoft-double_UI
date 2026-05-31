/**
 * 路由配置模块
 * 定义登录、管理员、健康管家等页面路由，并在导航前校验登录状态与角色权限
 */
import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const routes = [
    { path: '/login', component: () => import('../view/login.vue') },
    {
        path: '/admin',
        component: () => import('../view/admin/AdminLayout.vue'),
        meta: { role: 1 },
        children: [
            { path: 'checkin', component: () => import('../view/admin/customer/Checkin.vue') },
            { path: 'checkout', component: () => import('../view/admin/customer/Checkout.vue') },
            { path: 'outward-audit', component: () => import('../view/admin/customer/OutwardAudit.vue') },
            { path: 'bed-map', component: () => import('../view/admin/bed/BedMap.vue') },
            { path: 'bed-manage', component: () => import('../view/admin/bed/BedManage.vue') },
            { path: 'nursing-level', component: () => import('../view/admin/nursing/LevelManage.vue') },
            { path: 'nursing-item', component: () => import('../view/admin/nursing/ItemManage.vue') },
            { path: 'customer-level', component: () => import('../view/admin/nursing/CustomerLevel.vue') },
            { path: 'nursing-record', component: () => import('../view/admin/nursing/NursingRecord.vue') },
            { path: 'assign-hk', component: () => import('../view/admin/housekeeper/Assign.vue') },
            { path: 'service-watch', component: () => import('../view/admin/housekeeper/ServiceWatch.vue') },
            { path: 'user-manage', component: () => import('../view/admin/user/UserManage.vue') },
            { path: '', redirect: '/admin/checkin' }
        ]
    },
    {
        path: '/housekeeper',
        component: () => import('../view/housekeeper/HousekeeperLayout.vue'),
        meta: { role: 2 },
        children: [
            { path: 'daily-nursing', component: () => import('../view/housekeeper/DailyNursing.vue') },
            { path: 'nursing-record-query', component: () => import('../view/housekeeper/NursingRecordQuery.vue') },
            { path: 'outward-apply', component: () => import('../view/housekeeper/OutwardApply.vue') },
            { path: 'checkout-apply', component: () => import('../view/housekeeper/CheckoutApply.vue') },
            { path: '', redirect: '/housekeeper/daily-nursing' }
        ]
    },
    { path: '/', redirect: '/login' }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

/** 全局路由守卫：校验登录状态与角色权限 */
router.beforeEach((to, from, next) => {
    const user = store.state.user
    if (to.path === '/login') return next()
    if (!user) return next('/login')
    if (to.meta.role && user.roleId !== to.meta.role) return next('/login')
    next()
})

export default router
