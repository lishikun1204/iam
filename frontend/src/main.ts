import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { permissionDirective } from './directives/permission'

// 创建Vue应用实例
const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus)

// 使用路由
app.use(router)

app.directive('permission', permissionDirective)

// 挂载应用
app.mount('#app')
