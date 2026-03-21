<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from '@/lib/api'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

type UserStatus = 'ENABLED' | 'DISABLED'

type User = {
  id: string
  username: string
  fullName: string
  email?: string
  phone?: string
  avatar?: string
  status: UserStatus
  deptId?: string
}

const store = useAuthStore()
const canWrite = computed(() => store.hasAuthority('sys:user:write'))

const loading = ref(false)
const users = ref<User[]>([])
const dialogOpen = ref(false)
const editingId = ref<string | null>(null)

const form = reactive({
  username: '',
  fullName: '',
  email: '',
  phone: '',
  status: 'ENABLED' as UserStatus,
  password: 'Admin@123',
})

async function load() {
  loading.value = true
  try {
    users.value = await api.get<User[]>('/users')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.username = ''
  form.fullName = ''
  form.email = ''
  form.phone = ''
  form.status = 'ENABLED'
  form.password = 'Admin@123'
  dialogOpen.value = true
}

function openEdit(u: User) {
  editingId.value = u.id
  form.username = u.username
  form.fullName = u.fullName
  form.email = u.email ?? ''
  form.phone = u.phone ?? ''
  form.status = u.status
  dialogOpen.value = true
}

async function save() {
  try {
    if (!canWrite.value) return
    if (!editingId.value) {
      await api.post<User>('/users', {
        username: form.username,
        fullName: form.fullName,
        email: form.email || null,
        phone: form.phone || null,
        avatar: null,
        status: form.status,
        deptId: null,
        password: form.password,
      })
      ElMessage.success('已创建')
    } else {
      await api.patch<User>(`/users/${editingId.value}`, {
        fullName: form.fullName,
        email: form.email || null,
        phone: form.phone || null,
        avatar: null,
        status: form.status,
        deptId: null,
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
      <div class="text-lg font-semibold">用户管理</div>
      <el-button type="primary" :disabled="!canWrite" @click="openCreate">新增用户</el-button>
    </div>

    <el-table :data="users" v-loading="loading" border>
      <el-table-column prop="username" label="登录名" width="160" />
      <el-table-column prop="fullName" label="姓名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机" width="160" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" :disabled="!canWrite" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogOpen" :title="editingId ? '编辑用户' : '新增用户'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="登录名">
          <el-input v-model="form.username" :disabled="Boolean(editingId)" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.fullName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="w-full">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!editingId" label="密码">
          <el-input v-model="form.password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!canWrite" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

