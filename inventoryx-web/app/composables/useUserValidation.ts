import type { Ref } from "vue";

export interface UserFormData {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  roleName: string;
}

export function useUserValidation(form: UserFormData, isEditMode: Ref<boolean>) {
  const isValid = computed(() => {
    const basic =
      form.username.length >= 3 &&
      form.email.includes("@") &&
      form.firstName.length > 0 &&
      form.lastName.length > 0;

    if (isEditMode.value) return basic;
    return basic && form.password.length >= 6;
  });

  return {
    isValid,
  };
}
