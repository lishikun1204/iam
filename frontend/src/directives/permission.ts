import type { Directive } from 'vue'
import { useAuthStore } from '@/stores/auth'

export const permissionDirective: Directive<HTMLElement, string> = {
  mounted(el, binding) {
    const store = useAuthStore()
    const required = binding.value
    if (!required) return
    if (!store.hasAuthority(required)) {
      el.style.display = 'none'
    }
  },
  updated(el, binding) {
    const store = useAuthStore()
    const required = binding.value
    if (!required) return
    el.style.display = store.hasAuthority(required) ? '' : 'none'
  },
}

