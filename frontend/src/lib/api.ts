import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

type ApiResponse<T> = {
  code: string
  msg: string
  data: T
  timestamp: number
  traceId?: string
}

export const apiClient = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

apiClient.interceptors.request.use((config) => {
  const store = useAuthStore()
  const token = store.tokens?.accessToken
  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (resp) => {
    const payload = resp.data as ApiResponse<unknown>
    if (payload && typeof payload === 'object' && 'code' in payload && 'data' in payload) {
      if (payload.code !== '0') {
        const err = new Error(payload.msg || '请求失败')
        ;(err as any).traceId = (payload as any).traceId
        throw err
      }
      return payload.data
    }
    return resp.data
  },
  (err) => {
    throw err
  },
)

export const api = {
  get: <T>(url: string, params?: Record<string, any>) => apiClient.get<any, T>(url, { params }),
  post: <T>(url: string, data?: any) => apiClient.post<any, T>(url, data),
  patch: <T>(url: string, data?: any) => apiClient.patch<any, T>(url, data),
  put: <T>(url: string, data?: any) => apiClient.put<any, T>(url, data),
  delete: <T>(url: string) => apiClient.delete<any, T>(url),
}

