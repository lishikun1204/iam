<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
</script>

<template>
  <div class="max-w-5xl mx-auto">
    <div class="bg-white border border-zinc-200 rounded-xl p-6">
      <div class="text-lg font-semibold">欢迎，{{ store.username || '用户' }}</div>
      <div class="text-sm text-zinc-500 mt-1">这里是 IAM 管理控制台（动态路由 + 按钮权限）</div>

      <div class="mt-5 flex flex-wrap gap-3">
        <router-link to="/console/users">
          <el-button v-permission="'sys:user:read'">用户管理</el-button>
        </router-link>
        <router-link to="/console/roles">
          <el-button v-permission="'sys:role:read'">角色管理</el-button>
        </router-link>
        <router-link to="/console/permissions">
          <el-button v-permission="'sys:perm:read'">权限管理</el-button>
        </router-link>
        <router-link to="/console/depts">
          <el-button v-permission="'sys:dept:read'">部门管理</el-button>
        </router-link>
      </div>
    </div>
  </div>
</template>
