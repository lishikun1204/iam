<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { fetchUsers } from '@/service/api';

defineOptions({
  name: 'RbacUsers'
});

const loading = ref(false);
const rows = ref<any[]>([]);

const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id' },
  { title: 'Username', key: 'username' },
  { title: 'Display Name', key: 'displayName' }
];

async function load() {
  loading.value = true;
  const { data, error } = await fetchUsers();
  if (!error) {
    rows.value = data || [];
  }
  loading.value = false;
}

onMounted(load);
</script>

<template>
  <NCard title="用户">
    <NDataTable :columns="columns" :data="rows" :loading="loading" />
  </NCard>
</template>

