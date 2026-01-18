import { defineStore } from "pinia";
import type { User } from "../types/user";
import { useUserApi } from "~/composables/api/useUserApi";

export const useUserStore = defineStore("users", () => {
    const users = ref<User[]>([]);
    const totalRecords = ref(0);
    const loading = ref(false);
    const { getUsers, toggleStatus, createUser: apiCreateUser, updateUser: apiUpdateUser } = useUserApi();

    async function fetchUsers(page: number, size: number) {
        loading.value = true;
        try {
            const response = await getUsers(page, size);
            users.value = response.data.content;
            totalRecords.value = response.data.totalElements;
        } catch (error) {
            console.error("Failed to fetch users", error);
        } finally {
            loading.value = false;
        }
    }

    async function createUser(data: any) {
        try {
            await apiCreateUser(data);
            return true;
        } catch (error) {
            console.error("Failed to create user", error);
            throw error;
        }
    }

    async function updateUser(id: string, data: any) {
        try {
            await apiUpdateUser(id, data);
            return true;
        } catch (error) {
            console.error("Failed to update user", error);
            throw error;
        }
    }

    async function toggleUser(id: string) {
        try {
            await toggleStatus(id);
            // Optimistic update
            const user = users.value.find(u => u.id === id);
            if (user) {
                user.isActive = !user.isActive;
            }
            return true;
        } catch (error) {
            return false;
        }
    }

    return {
        users,
        totalRecords,
        loading,
        fetchUsers,
        createUser,
        updateUser,
        toggleUser
    };
});
