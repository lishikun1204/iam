<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from '@/lib/api'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

type Dept = {
  id: string
  code: string
  name: string
  parentId?: string
  sortNum: number
  leader?: string
  phone?: string
}

const store = useAuthStore()
const canWrite = computed(() => store.hasAuthority('sys:dept:write'))

const loading = ref(false)
const list = ref<Dept[]>([])
const dialogOpen = ref(false)
const editingId = ref<string | null>(null)

const form = reactive({
  code: '',
  name: '',
  parentId: '',
  sortNum: 0,
  leader: '',
  phone: '',
})

async function load() {
  loading.value = true
  try {
    list.value = await api.get<Dept[]>('/depts')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.code = ''
  form.name = ''
  form.parentId = ''
  form.sortNum = 0
  form.leader = ''
  form.phone = ''
  dialogOpen.value = true
}

function openEdit(d: Dept) {
  editingId.value = d.id
  form.code = d.code
  form.name = d.name
  form.parentId = d.parentId ?? ''
  form.sortNum = d.sortNum
  form.leader = d.leader ?? ''
  form.phone = d.phone ?? ''
  dialogOpen.value = true
}

async function save() {
  try {
    if (!canWrite.value) return
    if (!editingId.value) {
      await api.post<Dept>('/depts', {
        code: form.code,
        name: form.name,
        parentId: form.parentId || null,
        sortNum: form.sortNum,
        leader: form.leader || null,
        phone: form.phone || null,
      })
      ElMessage.success('已创建')
    } else {
      await api.patch<Dept>(`/depts/${editingId.value}`, {
        name: form.name,
        parentId: form.parentId || null,
        sortNum: form.sortNum,
        leader: form.leader || null,
        phone: form.phone || null,
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
      <div class="text-lg font-semibold">部门管理</div>
      <el-button type="primary" :disabled="!canWrite" @click="openCreate">新增部门</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="code" label="部门编码" width="160" />
      <el-table-column prop="name" label="部门名称" />
      <el-table-column prop="leader" label="负责人" width="140" />
      <el-table-column prop="phone" label="联系电话" width="160" />
      <el-table-column prop="sortNum" label="排序" width="100" />
      <el-table-column label="操作" width="140">
        <template #default="scope">
          <el-button size="small" :disabled="!canWrite" @click="openEdit(scope.row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogOpen" :title="editingId ? '编辑部门' : '新增部门'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="部门编码">
          <el-input v-model="form.code" :disabled="Boolean(editingId)" />
        </el-form-item>
        <el-form-item label="部门名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="父部门ID">
          <el-input v-model="form.parentId" placeholder="可为空" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortNum" :min="0" class="w-full" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" :disabled="!canWrite" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

