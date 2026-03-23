import { request } from '../request';

export function fetchUsers() {
  return request<any[]>({ url: '/api/users' });
}

export function fetchRoles() {
  return request<any[]>({ url: '/api/roles' });
}

export function fetchPermissions() {
  return request<any[]>({ url: '/api/permissions' });
}

export function fetchDepts() {
  return request<any[]>({ url: '/api/depts' });
}

