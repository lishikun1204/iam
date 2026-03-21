<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from '@/lib/api'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

type PermissionType = 'MENU' | 'BUTTON' | 'API'
type PermissionStatus = 'ENABLED' | 'DISABLED'

type Permission = {
  id: string
  code: string
  name: string
  type: PermissionType
  url?: string
  httpMethod?: string
  parentId?: string
  sortNum: number
  status: PermissionStatus
}

const store = useAuthStore()
const canWrite = computed(() => store.hasAuthority('sys:perm:write'))

const loading = ref(false)
const list = ref<Permission[]>([])
const dialogOpen = ref(false)
const editingId = ref<string | null>(null)

const form = reactive({
  code: '',
  name: '',
  type: 'API' as PermissionType,
  url: '',
  httpMethod: 'GET',
  parentId: '',
  sortNum: 0,
  status: 'ENABLED' as PermissionStatus,
})

async function load() {
  loading.value = true
  try {
    list.value = await api.get<Permission[]>('/permissions')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.code = ''
  form.name = ''
  form.type = 'API'
  form.url = ''
  form.httpMethod = 'GET'
  form.parentId = ''
  form.sortNum = 0
  form.status = 'ENABLED'
  dialogOpen.value = true
}

function openEdit(p: Permission) {
  editingId.value = p.id
  form.code = p.code
  form.name = p.name
  form.type = p.type
  form.url = p.url ?? ''
  form.httpMethod = p.httpMethod ?? 'GET'
  form.parentId = p.parentId ?? ''
  form.sortNum = p.sortNum
  form.status = p.status
  dialogOpen.value = true
}

async function save() {
  try {
    if (!canWrite.value) return
    if (!editingId.value) {
      await api.post<Permission>('/permissions', {
        code: form.code,
        name: form.name,
        type: form.type,
        url: form.url || null,
        httpMethod: form.httpMethod || null,
        parentId: form.parentId || null,
        sortNum: form.sortNum,
        status: form.status,
      })
      ElMessage.success('已创建')
    } else {
      await api.patch<Permission>(`/permissions/${editingId.value}`, {
        name: form.name,
        type: form.type,
        url: form.url || null,
        httpMethod: form.httpMethod || null,
        parentId: form.parentId || null,
        sortNum: form.sortNum,
        status: form.status,
      })
      ElMessage.success('已更新')
    }
    dialogOpen.value = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  }
}

onMounted(load)
</script>

<template>
  <div class="max-w-5xl mx-auto">
    <div class="flex items-center justify-between mb-4">
      <div class="text-lg font-semibold">权限管理</div>
      <el-button type="primary" :disabled="!canWrite" @click="openCreate">新增权限</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="code" label="编码" width="220" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="url" label="URL" />
      <el-table-column prop="httpMethod" label="方法" width="100" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" :disabled="!canWrite" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogOpen" :title="editingId ? '编辑权限' : '新增权限'" width="560px">
      <el-form label-width="90px">
        <el-form-item label="编码">
          <el-input v-model="form.code" :disabled="Boolean(editingId)" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" class="w-full">
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
            <el-option label="API" value="API" />
          </el-select>
        </el-form-item>
        <el-form-item label="URL">
          <el-input v-model="form.url" />
        </el-form-item>
        <el-form-item label="方法">
          <el-select v-model="form.httpMethod" class="w-full">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="PATCH" value="PATCH" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortNum" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="w-full">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!canWrite" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

