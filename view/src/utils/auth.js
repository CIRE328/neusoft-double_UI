/**
 * 认证工具模块
 * 提供从 localStorage 获取用户信息、token、userId 等方法
 */

export const getUser = () => {
    const userStr = localStorage.getItem('user')
    if (!userStr) return null
    try {
        return JSON.parse(userStr)
    } catch (e) {
        console.error('解析用户信息失败', e)
        return null
    }
}

export const getToken = () => localStorage.getItem('token')

export const getUserId = () => {
    const user = getUser()
    return user ? user.id : null
}