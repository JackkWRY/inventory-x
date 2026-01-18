export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  isActive: boolean;
  isLocked: boolean;
  roles: string[];
  lastLoginAt?: string;
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  password: string; // Only for create
  email: string;
  firstName: string;
  lastName: string;
  roleName: string;
}

export interface UpdateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  roleName?: string;
}
