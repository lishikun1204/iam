<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { $t } from '@/locales';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();
const route = useRoute();

async function handleSubmit() {
  await authStore.startOAuthLogin(true);
}

onMounted(async () => {
  if (route.query.auto !== '1') return;
  await handleSubmit();
});
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
