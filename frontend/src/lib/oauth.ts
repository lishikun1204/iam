export type OAuthTokenResponse = {
  access_token: string
  token_type: string
  expires_in: number
  refresh_token?: string
  scope?: string
  id_token?: string
}

const PKCE_VERIFIER_KEY = 'iam.pkce.verifier'
const OAUTH_STATE_KEY = 'iam.oauth.state'

function randomString(bytes = 32) {
  const arr = new Uint8Array(bytes)
  crypto.getRandomValues(arr)
  return base64UrlEncode(arr)
}

function base64UrlEncode(input: ArrayBuffer | Uint8Array) {
  const bytes = input instanceof Uint8Array ? input : new Uint8Array(input)
  let str = ''
  for (const b of bytes) str += String.fromCharCode(b)
  return btoa(str).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '')
}

async function sha256(text: string) {
  const data = new TextEncoder().encode(text)
  return crypto.subtle.digest('SHA-256', data)
}

export async function startAuthorizationCodeLogin(opts?: { authBase?: string }) {
  const authBase = opts?.authBase ?? import.meta.env.VITE_AUTH_BASE ?? 'http://localhost:8080'

  const verifier = randomString(64)
  const challenge = base64UrlEncode(await sha256(verifier))
  const state = randomString(24)

  sessionStorage.setItem(PKCE_VERIFIER_KEY, verifier)
  sessionStorage.setItem(OAUTH_STATE_KEY, state)

  const redirectUri = `${window.location.origin}/oauth/callback`
  const params = new URLSearchParams({
    response_type: 'code',
    client_id: 'vue-client',
    scope: 'openid profile rbac',
    redirect_uri: redirectUri,
    code_challenge: challenge,
    code_challenge_method: 'S256',
    state,
  })
  window.location.href = `${authBase}/oauth2/authorize?${params.toString()}`
}

export async function exchangeCodeForToken(input: { code: string; state: string; authBase?: string }) {
  const authBase = input.authBase ?? import.meta.env.VITE_AUTH_BASE ?? 'http://localhost:8080'
  const savedState = sessionStorage.getItem(OAUTH_STATE_KEY)
  if (!savedState || savedState !== input.state) {
    throw new Error('state 校验失败')
  }
  const verifier = sessionStorage.getItem(PKCE_VERIFIER_KEY)
  if (!verifier) {
    throw new Error('缺少 PKCE verifier')
  }

  const redirectUri = `${window.location.origin}/oauth/callback`
  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: 'vue-client',
    code: input.code,
    redirect_uri: redirectUri,
    code_verifier: verifier,
  })

  const resp = await fetch(`${authBase}/oauth2/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body,
  })

  if (!resp.ok) {
    const text = await resp.text()
    throw new Error(`换取 token 失败: ${resp.status} ${text}`)
  }

  return (await resp.json()) as OAuthTokenResponse
}

export async function refreshToken(input: { refreshToken: string; authBase?: string }) {
  const authBase = input.authBase ?? import.meta.env.VITE_AUTH_BASE ?? 'http://localhost:8080'
  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    client_id: 'vue-client',
    refresh_token: input.refreshToken,
  })

  const resp = await fetch(`${authBase}/oauth2/token`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body,
  })
  if (!resp.ok) {
    const text = await resp.text()
    throw new Error(`刷新 token 失败: ${resp.status} ${text}`)
  }
  return (await resp.json()) as OAuthTokenResponse
}

