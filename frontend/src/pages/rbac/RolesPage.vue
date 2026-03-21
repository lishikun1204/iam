<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from '@/lib/api'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

type RoleStatus = 'ENABLED' | 'DISABLED'
type DataScopeType = 'ALL' | 'CUSTOM' | 'DEPT' | 'DEPT_AND_CHILD'

type Role = {
  id: string
  code: string
  name: string
  levelNum: number
  dataScope: DataScopeType
  status: RoleStatus
}

const store = useAuthStore()
const canWrite = computed(() => store.hasAuthority('sys:role:write'))

const loading = ref(false)
const roles = ref<Role[]>([])
const dialogOpen = ref(false)
const editingId = ref<string | null>(null)

const form = reactive({
  code: '',
  name: '',
  levelNum: 1,
  dataScope: 'ALL' as DataScopeType,
  status: 'ENABLED' as RoleStatus,
})

async function load() {
  loading.value = true
  try {
    roles.value = await api.get<Role[]>('/roles')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.code = ''
  form.name = ''
  form.levelNum = 1
  form.dataScope = 'ALL'
  form.status = 'ENABLED'
  dialogOpen.value = true
}

function openEdit(r: Role) {
  editingId.value = r.id
  form.code = r.code
  form.name = r.name
  form.levelNum = r.levelNum
  form.dataScope = r.dataScope
  form.status = r.status
  dialogOpen.value = true
}

async function save() {
  try {
    if (!canWrite.value) return
    if (!editingId.value) {
      await api.post<Role>('/roles', {
        code: form.code,
        name: form.name,
        levelNum: form.levelNum,
        dataScope: form.dataScope,
        status: form.status,
      })
      ElMessage.success('已创建')
    } else {
      await api.patch<Role>(`/roles/${editingId.value}`, {
        name: form.name,
        levelNum: form.levelNum,
        dataScope: form.dataScope,
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
      <div class="text-lg font-semibold">角色管理</div>
      <el-button type="primary" :disabled="!canWrite" @click="openCreate">新增角色</el-button>
    </div>

    <el-table :data="roles" v-loading="loading" border>
      <el-table-column prop="code" label="角色编码" width="160" />
      <el-table-column prop="name" label="角色名称" />
      <el-table-column prop="levelNum" label="级别" width="100" />
      <el-table-column prop="dataScope" label="数据权限" width="160" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" :disabled="!canWrite" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogOpen" :title="editingId ? '编辑角色' : '新增角色'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="角色编码">
          <el-input v-model="form.code" :disabled="Boolean(editingId)" />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色级别">
          <el-input-number v-model="form.levelNum" :min="1" class="w-full" />
        </el-form-item>
        <el-form-item label="数据权限">
          <el-select v-model="form.dataScope" class="w-full">
            <el-option label="全部" value="ALL" />
            <el-option label="自定义" value="CUSTOM" />
            <el-option label="本部门" value="DEPT" />
            <el-option label="本部门及下级" value="DEPT_AND_CHILD" />
          </el-select>
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

