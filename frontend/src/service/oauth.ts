import axios from 'axios';
import { sessionStg } from '@/utils/storage';

export const OAUTH_PKCE_VERIFIER_KEY = 'oauth_pkce_verifier';
export const OAUTH_STATE_KEY = 'oauth_state';
export const OAUTH_REDIRECT_KEY = 'oauth_redirect';

function getRedirectUri() {
  const configured = (import.meta.env.VITE_OAUTH_REDIRECT_URI as string) || '';
  if (configured) return configured;
  return `${window.location.origin}/oauth/callback`;
}

function getTokenEndpoint() {
  const authBase = import.meta.env.VITE_AUTH_BASE_URL as string;
  const isProxy = import.meta.env.DEV && import.meta.env.VITE_HTTP_PROXY === 'Y';
  return isProxy ? '/proxy-default/oauth2/token' : `${authBase}/oauth2/token`;
}

function base64UrlEncode(bytes: Uint8Array) {
  let binary = '';
  bytes.forEach(b => {
    binary += String.fromCharCode(b);
  });
  return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/g, '');
}

function randomBase64Url(byteLength: number) {
  const bytes = new Uint8Array(byteLength);
  crypto.getRandomValues(bytes);
  return base64UrlEncode(bytes);
}

async function sha256Base64Url(input: string) {
  const bytes = new TextEncoder().encode(input);
  const digest = await crypto.subtle.digest('SHA-256', bytes);
  return base64UrlEncode(new Uint8Array(digest));
}

export async function startPkceAuthorization(options?: { redirect?: string }) {
  const authBase = import.meta.env.VITE_AUTH_BASE_URL as string;
  const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID as string;
  const scope = (import.meta.env.VITE_OAUTH_SCOPE as string) || 'openid profile';

  const verifier = randomBase64Url(64);
  const challenge = await sha256Base64Url(verifier);
  const state = randomBase64Url(32);

  sessionStg.set(OAUTH_PKCE_VERIFIER_KEY, verifier);
  sessionStg.set(OAUTH_STATE_KEY, state);
  sessionStg.set(OAUTH_REDIRECT_KEY, options?.redirect || '');

  const redirectUri = getRedirectUri();

  const params = new URLSearchParams({
    response_type: 'code',
    client_id: clientId,
    redirect_uri: redirectUri,
    scope,
    code_challenge: challenge,
    code_challenge_method: 'S256',
    state
  });

  window.location.assign(`${authBase}/oauth2/authorize?${params.toString()}`);
}

export async function exchangeAuthorizationCode(code: string, state: string) {
  const authBase = import.meta.env.VITE_AUTH_BASE_URL as string;
  const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID as string;

  const savedState = sessionStg.get(OAUTH_STATE_KEY) || '';
  const verifier = sessionStg.get(OAUTH_PKCE_VERIFIER_KEY) || '';

  if (!savedState || !verifier || savedState !== state) {
    throw new Error('Invalid oauth state');
  }

  const redirectUri = getRedirectUri();

  const body = new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: clientId,
    code,
    redirect_uri: redirectUri,
    code_verifier: verifier
  });

  const { data } = await axios.post(getTokenEndpoint(), body, {
    timeout: 10_000,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  });

  return data as {
    access_token: string;
    refresh_token?: string;
    token_type: string;
    expires_in?: number;
    scope?: string;
    id_token?: string;
  };
}

export async function refreshAccessToken(refreshToken: string) {
  const clientId = import.meta.env.VITE_OAUTH_CLIENT_ID as string;

  const body = new URLSearchParams({
    grant_type: 'refresh_token',
    client_id: clientId,
    refresh_token: refreshToken
  });

  const { data } = await axios.post(getTokenEndpoint(), body, {
    timeout: 10_000,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  });

  return data as {
    access_token: string;
    refresh_token?: string;
    token_type: string;
    expires_in?: number;
    scope?: string;
    id_token?: string;
  };
}

export function getSavedOAuthRedirect() {
  return sessionStg.get(OAUTH_REDIRECT_KEY) || '';
}

export function clearOAuthTempStorage() {
  sessionStg.remove(OAUTH_PKCE_VERIFIER_KEY);
  sessionStg.remove(OAUTH_STATE_KEY);
  sessionStg.remove(OAUTH_REDIRECT_KEY);
}
