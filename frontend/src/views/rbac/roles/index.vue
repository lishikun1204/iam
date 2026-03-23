<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { fetchRoles } from '@/service/api';

defineOptions({
  name: 'RbacRoles'
});

const loading = ref(false);
const rows = ref<any[]>([]);

const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id' },
  { title: 'Code', key: 'code' },
  { title: 'Name', key: 'name' }
];

async function load() {
  loading.value = true;
  const { data, error } = await fetchRoles();
  if (!error) {
    rows.value = data || [];
  }
  loading.value = false;
}

onMounted(load);
</script>

<template>
  <NCard title="角色">
    <NDataTable :columns="columns" :data="rows" :loading="loading" />
  </NCard>
</template>

