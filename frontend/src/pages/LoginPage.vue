<script setup lang="ts">
import { startAuthorizationCodeLogin } from '@/lib/oauth'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { LockKeyhole, ShieldCheck, Users } from 'lucide-vue-next'
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'

const store = useAuthStore()
const router = useRouter()
const route = useRoute()

onMounted(() => {
  if (store.isLoggedIn) {
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/console'
    router.replace(redirect)
  }
})

function onLogin() {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/console'
  sessionStorage.setItem('iam.oauth.redirect', redirect)
  startAuthorizationCodeLogin()
}

async function copyDefaultAccount() {
  try {
    await navigator.clipboard.writeText('admin / Admin@123')
    ElMessage.success('已复制默认账号')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}
</script>

<template>
  <div
    class="relative min-h-screen overflow-hidden bg-gradient-to-br from-slate-50 via-white to-sky-50 before:pointer-events-none before:absolute before:inset-0 before:bg-[radial-gradient(circle_at_30%_20%,rgba(14,165,233,0.14),transparent_45%),radial-gradient(circle_at_70%_65%,rgba(99,102,241,0.12),transparent_50%)]"
  >
    <div class="mx-auto w-full max-w-5xl px-4 py-14 sm:py-20">
      <div class="grid items-center gap-10 lg:grid-cols-2">
        <div class="text-center lg:text-left">
          <div class="inline-flex items-center gap-2 rounded-full border border-slate-200 bg-white/70 px-3 py-1 text-xs font-medium text-slate-700 backdrop-blur">
            <ShieldCheck class="h-4 w-4 text-sky-600" />
            OAuth2.1 授权码模式 + RBAC
          </div>

          <div class="mt-5 text-3xl font-semibold tracking-tight text-slate-900 sm:text-4xl">
            IAM 控制台
          </div>
          <div class="mt-3 text-sm leading-6 text-slate-600 sm:text-base">
            统一身份认证、动态路由、按钮级权限控制。登录后自动拉取菜单与权限。
          </div>

          <div class="mt-8 grid gap-4 text-left sm:grid-cols-3">
            <div class="rounded-xl border border-slate-200 bg-white/70 p-4 backdrop-blur">
              <div class="flex items-center gap-2 text-sm font-medium text-slate-900">
                <LockKeyhole class="h-4 w-4 text-indigo-600" />
                安全登录
              </div>
              <div class="mt-1 text-xs leading-5 text-slate-600">
                授权码 + PKCE，JWT 访问令牌
              </div>
            </div>
            <div class="rounded-xl border border-slate-200 bg-white/70 p-4 backdrop-blur">
              <div class="flex items-center gap-2 text-sm font-medium text-slate-900">
                <Users class="h-4 w-4 text-sky-600" />
                RBAC
              </div>
              <div class="mt-1 text-xs leading-5 text-slate-600">
                角色、权限、部门数据隔离
              </div>
            </div>
            <div class="rounded-xl border border-slate-200 bg-white/70 p-4 backdrop-blur">
              <div class="flex items-center gap-2 text-sm font-medium text-slate-900">
                <ShieldCheck class="h-4 w-4 text-emerald-600" />
                可审计
              </div>
              <div class="mt-1 text-xs leading-5 text-slate-600">
                登录与操作记录可追溯
              </div>
            </div>
          </div>
        </div>

        <div class="mx-auto w-full max-w-md">
          <div class="rounded-2xl border border-slate-200 bg-white/80 p-7 shadow-[0_18px_50px_-22px_rgba(15,23,42,0.35)] backdrop-blur">
            <div class="flex items-start justify-between gap-4">
              <div>
                <div class="text-lg font-semibold text-slate-900">欢迎使用</div>
                <div class="mt-1 text-sm text-slate-600">点击登录将跳转到后端授权服务器（表单登录）</div>
              </div>
              <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-sky-600/10">
                <ShieldCheck class="h-5 w-5 text-sky-700" />
              </div>
            </div>

            <div class="mt-6">
              <el-button type="primary" class="w-full" size="large" @click="onLogin">登录</el-button>
            </div>

            <div class="mt-5 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
              <div class="flex items-center justify-between gap-3">
                <div>
                  <div class="text-xs font-medium text-slate-700">默认账号</div>
                  <div class="mt-1 font-mono text-xs text-slate-700">admin / Admin@123</div>
                </div>
                <el-button size="small" @click="copyDefaultAccount">复制</el-button>
              </div>
            </div>

            <div class="mt-6 text-center text-xs text-slate-500">
              登录后将自动拉取用户信息、菜单与权限
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
