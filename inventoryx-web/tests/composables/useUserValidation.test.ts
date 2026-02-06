import { describe, it, expect, beforeEach } from 'vitest'
import { reactive, ref } from 'vue'
import { useUserValidation } from '~/composables/useUserValidation'
import { vi } from 'vitest'

// Need to mock Vue I18n if used inside (it's not used in the logic directly but good practice)
vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: (key: string) => key
  })
}))

describe('useUserValidation', () => {
  let form: any
  let isEditMode: any

  beforeEach(() => {
    form = reactive({
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      roleName: 'ROLE_USER'
    })
    isEditMode = ref(false)
  })

  it('should be invalid when required fields are empty', () => {
    const { isValid } = useUserValidation(form, isEditMode)
    expect(isValid.value).toBe(false)
  })

  it('should be valid when all fields are correct', () => {
    form.username = 'john_doe'
    form.email = 'john@example.com'
    form.password = 'password123'
    form.firstName = 'John'
    form.lastName = 'Doe'
    
    const { isValid } = useUserValidation(form, isEditMode)
    expect(isValid.value).toBe(true)
  })
})
