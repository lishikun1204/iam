<script setup lang="ts">
import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();

async function handleSubmit() {
  await authStore.startOAuthLogin(true);
}
</script>

<template>
  <NSpace vertical :size="24">
    <NText depth="3">
      {{ $t('page.login.pwdLogin.oauthTip') }}
    </NText>
    <NButton type="primary" size="large" round block :loading="authStore.loginLoading" @click="handleSubmit">
      {{ $t('common.confirm') }}
    </NButton>
  </NSpace>
</template>

<style scoped></style>
