<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { clearOAuthTempStorage } from '@/service/oauth';

defineOptions({
  name: 'OauthCallback'
});

const route = useRoute();
const authStore = useAuthStore();

const code = computed(() => String(route.query.code || ''));
const state = computed(() => String(route.query.state || ''));

onMounted(async () => {
  if (!code.value || !state.value) {
    clearOAuthTempStorage();
    await authStore.resetStore(true);
    return;
  }
  await authStore.handleOAuthCallback(code.value, state.value);
});
</script>

<template>
  <div class="h-full w-full flex-center">
    <NSpin size="large" />
  </div>
</template>
