import { localStg, sessionStg } from '@/utils/storage';

/** Get token */
export function getToken() {
  return localStg.get('token') || '';
}

/** Clear auth storage */
export function clearAuthStorage() {
  localStg.remove('token');
  localStg.remove('refreshToken');
  sessionStg.remove('oauth_pkce_verifier');
  sessionStg.remove('oauth_state');
  sessionStg.remove('oauth_redirect');
}
