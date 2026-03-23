import { request } from '../request';
import { refreshAccessToken } from '../oauth';

export function fetchGetUserInfo() {
  return request<{ username: string; authorities: string[] }>({ url: '/api/me' });
}

export async function fetchRefreshToken(refreshToken: string) {
  try {
    const data = await refreshAccessToken(refreshToken);

    const result: Api.Auth.LoginToken = {
      token: data.access_token,
      refreshToken: data.refresh_token || refreshToken
    };

    return { data: result, error: null, response: null as any };
  } catch (error) {
    return { data: null, error, response: null as any };
  }
}
