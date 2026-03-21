<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { resetDynamicRoutes } from '@/router'
import { useTokenRefresh } from '@/composables/useTokenRefresh'

useTokenRefresh()

const router = useRouter()
const route = useRoute()
const store = useAuthStore()

const menuItems = computed(() => {
  const all = router.getRoutes()
  const children = all.filter((r) => r.path.startsWith('/console/') && r.meta && (r.meta as any).title)
  return children
    .filter((r) => {
      const required = (r.meta as any).requiredAuthority as string | undefined
      if (!required) return true
      return store.hasAuthority(required)
    })
    .sort((a, b) => a.path.localeCompare(b.path))
    .map((r) => ({
      path: r.path,
      title: (r.meta as any).title as string,
    }))
})

function onLogout() {
  store.clear()
  resetDynamicRoutes()
  router.replace('/login')
}
</script>

<template>
  <el-container class="min-h-screen">
    <el-aside width="220px" class="border-r border-zinc-200 bg-white">
      <div class="h-14 flex items-center px-4 border-b border-zinc-200">
        <div class="font-semibold">IAM</div>
      </div>
      <el-menu :default-active="route.path" router class="border-0">
        <el-menu-item index="/console">
          <span>概览</span>
        </el-menu-item>
        <el-menu-item v-for="m in menuItems" :key="m.path" :index="m.path">
          <span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header height="56px" class="bg-white border-b border-zinc-200 flex items-center justify-between">
        <div class="text-sm text-zinc-600">{{ store.username || '未命名用户' }}</div>
        <el-button type="default" @click="onLogout">退出</el-button>
      </el-header>
      <el-main class="bg-zinc-50">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

