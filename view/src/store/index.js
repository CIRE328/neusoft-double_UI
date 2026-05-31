/**
 * Vuex 状态管理模块
 * 管理用户登录态（用户信息、Token），并提供登录/登出异步操作
 */
import { createStore } from 'vuex'
import request from '../utils/request'

export default createStore({
    state: {
        user: JSON.parse(localStorage.getItem('user') || 'null'),
        token: localStorage.getItem('token') || ''
    },
    mutations: {
        /** 保存用户信息到 state 与 localStorage */
        SET_USER(state, user) {
            state.user = user
            localStorage.setItem('user', JSON.stringify(user))
        },
        /** 保存 Token 到 state 与 localStorage */
        SET_TOKEN(state, token) {
            state.token = token
            localStorage.setItem('token', token)
        },
        /** 清除登录态 */
        LOGOUT(state) {
            state.user = null
            state.token = ''
            localStorage.removeItem('user')
            localStorage.removeItem('token')
        }
    },
    actions: {
        /** 调用登录接口，成功后写入用户信息与 Token */
        async login({ commit }, { username, password }) {
            try {
                const res = await request.post('/auth/login', { username, password })
                if (res.code === 200) {
                    commit('SET_TOKEN', res.data.token)
                    commit('SET_USER', res.data.user)
                    return true
                }
                return false
            } catch (error) {
                console.error(error)
                return false
            }
        },
        /** 退出登录，清除本地状态 */
        logout({ commit }) {
            commit('LOGOUT')
        }
    },
    getters: {
        isAdmin: state => state.user?.roleId === 1,
        isHousekeeper: state => state.user?.roleId === 2
    }
})
