<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { fetchDepts } from '@/service/api';

defineOptions({
  name: 'RbacDepts'
});

const loading = ref(false);
const rows = ref<any[]>([]);

const columns: DataTableColumns<any> = [
  { title: 'ID', key: 'id' },
  { title: 'Name', key: 'name' },
  { title: 'Parent ID', key: 'parentId' }
];

async function load() {
  loading.value = true;
  const { data, error } = await fetchDepts();
  if (!error) {
    rows.value = data || [];
  }
  loading.value = false;
}

onMounted(load);
</script>

<template>
  <NCard title="部门">
    <NDataTable :columns="columns" :data="rows" :loading="loading" />
  </NCard>
</template>

