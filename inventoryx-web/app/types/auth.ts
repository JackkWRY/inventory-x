export interface User {
  id: string; // ID is usually in token claims or user profile
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  roles: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  username: string;
  firstName: string;
  lastName: string;
  roles: string[];
  permissions: string[];
}

export interface LoginCommand {
  username: string;
  usernameOrEmail?: string; // Flexible if needed, but backend asks for username
  password: string;
}

export interface RegisterUserCommand {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  roleName: string;
}
