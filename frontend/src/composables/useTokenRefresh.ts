import { onBeforeUnmount, onMounted } from 'vue'
import { refreshToken } from '@/lib/oauth'
import { useAuthStore } from '@/stores/auth'

export function useTokenRefresh() {
  const store = useAuthStore()
  let timer: number | null = null

  async function tick() {
    const tokens = store.tokens
    if (!tokens?.refreshToken) return
    const msLeft = tokens.expiresAt - Date.now()
    if (msLeft > 60_000) return

    const next = await refreshToken({ refreshToken: tokens.refreshToken })
    store.setTokens({
      accessToken: next.access_token,
      refreshToken: next.refresh_token ?? tokens.refreshToken,
      expiresAt: Date.now() + next.expires_in * 1000,
    })
  }

  onMounted(() => {
    timer = window.setInterval(() => {
      tick().catch(() => {
        store.clear()
      })
    }, 15_000)
  })

  onBeforeUnmount(() => {
    if (timer) window.clearInterval(timer)
  })
}

