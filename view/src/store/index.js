import { createStore } from 'vuex'
import request from '../utils/request'

export default createStore({
    state: {
        user: JSON.parse(localStorage.getItem('user') || 'null'),
        token: localStorage.getItem('token') || ''
    },
    mutations: {
        SET_USER(state, user) {
            state.user = user
            localStorage.setItem('user', JSON.stringify(user))
        },
        SET_TOKEN(state, token) {
            state.token = token
            localStorage.setItem('token', token)
        },
        LOGOUT(state) {
            state.user = null
            state.token = ''
            localStorage.removeItem('user')
            localStorage.removeItem('token')
        }
    },
    actions: {
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
        logout({ commit }) {
            commit('LOGOUT')
        }
    },
    getters: {
        isAdmin: state => state.user?.roleId === 1,
        isHousekeeper: state => state.user?.roleId === 2
    }
})