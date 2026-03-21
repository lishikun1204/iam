import { defineStore } from 'pinia'
import { api } from '@/lib/api'

export type TokenSet = {
  accessToken: string
  refreshToken?: string
  expiresAt: number
}

type MeResp = {
  username: string
  authorities: string[]
}

const STORAGE_KEY = 'iam.tokens'

function loadTokens(): TokenSet | null {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as TokenSet
  } catch {
    return null
  }
}

function saveTokens(tokens: TokenSet | null) {
  if (!tokens) {
    sessionStorage.removeItem(STORAGE_KEY)
    return
  }
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(tokens))
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    tokens: loadTokens() as TokenSet | null,
    username: '' as string,
    authorities: [] as string[],
  }),
  getters: {
    isLoggedIn: (s) => Boolean(s.tokens?.accessToken),
  },
  actions: {
    setTokens(tokens: TokenSet) {
      this.tokens = tokens
      saveTokens(tokens)
    },
    clear() {
      this.tokens = null
      this.username = ''
      this.authorities = []
      saveTokens(null)
    },
    hasAuthority(code: string) {
      return this.authorities.includes(code)
    },
    async fetchMe() {
      const me = await api.get<MeResp>('/me')
      this.username = me.username
      this.authorities = me.authorities
      return me
    },
  },
})

