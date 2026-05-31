/**
 * HTTP 请求封装模块
 * 基于 axios 创建实例，统一配置 baseURL、请求/响应拦截器及错误提示
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

/** 请求拦截：自动附加 Authorization Token */
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

/** 响应拦截：统一处理业务状态码与网络错误 */
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || 'Error'))
        }
        return res
    },
    error => {
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default request
