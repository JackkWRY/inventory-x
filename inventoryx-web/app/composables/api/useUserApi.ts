import type { User, CreateUserRequest, UpdateUserRequest } from "~/types/user";

export const useUserApi = () => {
  const { $api } = useNuxtApp();

  const getUsers = async (page = 0, size = 10) => {
    // Spring Data Page numbering starts at 0
    return $api.get<{ content: User[], totalElements: number, totalPages: number }>(`/users`, {
      params: { page, size, sort: 'createdAt,desc' }
    });
  };

  const createUser = (data: CreateUserRequest) => {
    return $api.post("/users/register", data);
  };

  const updateUser = (id: string, data: UpdateUserRequest) => {
    return $api.put(`/users/${id}`, data);
  };

  const toggleStatus = (id: string) => {
    return $api.patch(`/users/${id}/status`);
  };

  return {
    getUsers,
    createUser,
    updateUser,
    toggleStatus
  };
};
