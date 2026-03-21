<script setup lang="ts">
import { exchangeCodeForToken } from '@/lib/oauth'
import { useAuthStore } from '@/stores/auth'
import { applyDynamicRoutes } from '@/router'
import { ElMessage } from 'element-plus'
import { CheckCircle2, Loader2, ShieldCheck, XCircle } from 'lucide-vue-next'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const store = useAuthStore()

const loading = ref(true)
const errorMsg = ref('')
const detailMsg = ref('')
const step = ref(0)

const activeStep = computed(() => {
  if (errorMsg.value) return step.value
  return Math.max(step.value, 0)
})

async function run() {
  loading.value = true
  errorMsg.value = ''
  detailMsg.value = ''
  step.value = 0

  try {
    const code = typeof route.query.code === 'string' ? route.query.code : ''
    const state = typeof route.query.state === 'string' ? route.query.state : ''
    if (!code || !state) {
      throw new Error('缺少 code/state')
    }

    step.value = 1
    const tokenResp = await exchangeCodeForToken({ code, state })
    const expiresAt = Date.now() + tokenResp.expires_in * 1000
    store.setTokens({
      accessToken: tokenResp.access_token,
      refreshToken: tokenResp.refresh_token,
      expiresAt,
    })

    step.value = 2
    const me = await store.fetchMe()

    step.value = 3
    applyDynamicRoutes(me.authorities)

    const stored = sessionStorage.getItem('iam.oauth.redirect')
    sessionStorage.removeItem('iam.oauth.redirect')
    const redirect = stored || '/console'
    await router.replace(redirect)
  } catch (e: any) {
    store.clear()
    errorMsg.value = e?.message || '登录失败'
    detailMsg.value = typeof e === 'string' ? e : JSON.stringify(e, null, 2)
    ElMessage.error(errorMsg.value)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await run()
})
</script>

<template>
  <div
    class="relative min-h-screen overflow-hidden bg-gradient-to-br from-slate-50 via-white to-sky-50 before:pointer-events-none before:absolute before:inset-0 before:bg-[radial-gradient(circle_at_30%_20%,rgba(14,165,233,0.14),transparent_45%),radial-gradient(circle_at_70%_65%,rgba(99,102,241,0.12),transparent_50%)]"
  >
    <div class="mx-auto flex min-h-screen w-full max-w-lg items-center px-4 py-10">
      <div class="w-full rounded-2xl border border-slate-200 bg-white/80 p-7 shadow-[0_18px_50px_-22px_rgba(15,23,42,0.35)] backdrop-blur">
        <div class="flex items-start justify-between gap-4">
          <div>
            <div class="text-lg font-semibold text-slate-900">正在完成登录</div>
            <div class="mt-1 text-sm text-slate-600">交换令牌并同步权限数据</div>
          </div>
          <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-sky-600/10">
            <ShieldCheck class="h-5 w-5 text-sky-700" />
          </div>
        </div>

        <div class="mt-6 space-y-3">
          <div class="flex items-center justify-between rounded-xl border border-slate-200 bg-white/70 px-4 py-3">
            <div class="text-sm text-slate-700">1. 交换 token</div>
            <div class="flex items-center">
              <Loader2 v-if="loading && activeStep <= 1" class="h-4 w-4 animate-spin text-slate-500" />
              <CheckCircle2 v-else-if="!errorMsg && activeStep > 1" class="h-4 w-4 text-emerald-600" />
              <XCircle v-else-if="errorMsg && activeStep <= 1" class="h-4 w-4 text-rose-600" />
            </div>
          </div>
          <div class="flex items-center justify-between rounded-xl border border-slate-200 bg-white/70 px-4 py-3">
            <div class="text-sm text-slate-700">2. 拉取用户与权限</div>
            <div class="flex items-center">
              <Loader2 v-if="loading && activeStep === 2" class="h-4 w-4 animate-spin text-slate-500" />
              <CheckCircle2 v-else-if="!errorMsg && activeStep > 2" class="h-4 w-4 text-emerald-600" />
              <XCircle v-else-if="errorMsg && activeStep === 2" class="h-4 w-4 text-rose-600" />
            </div>
          </div>
          <div class="flex items-center justify-between rounded-xl border border-slate-200 bg-white/70 px-4 py-3">
            <div class="text-sm text-slate-700">3. 初始化动态路由</div>
            <div class="flex items-center">
              <Loader2 v-if="loading && activeStep === 3" class="h-4 w-4 animate-spin text-slate-500" />
              <CheckCircle2 v-else-if="!errorMsg && activeStep > 3" class="h-4 w-4 text-emerald-600" />
              <XCircle v-else-if="errorMsg && activeStep >= 3" class="h-4 w-4 text-rose-600" />
            </div>
          </div>
        </div>

        <div v-if="loading" class="mt-6 text-sm text-slate-600">
          请稍候，正在处理…
        </div>

        <div v-else-if="errorMsg" class="mt-6">
          <el-alert :title="errorMsg" type="error" show-icon :closable="false" />
          <div class="mt-4 flex gap-3">
            <el-button class="flex-1" @click="run">重试</el-button>
            <el-button type="primary" class="flex-1" @click="$router.replace('/login')">返回登录</el-button>
          </div>
          <div v-if="detailMsg" class="mt-4 rounded-xl border border-slate-200 bg-slate-50 p-3">
            <div class="text-xs font-medium text-slate-700">错误详情</div>
            <pre class="mt-2 max-h-40 overflow-auto whitespace-pre-wrap break-words font-mono text-xs text-slate-600">{{ detailMsg }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
