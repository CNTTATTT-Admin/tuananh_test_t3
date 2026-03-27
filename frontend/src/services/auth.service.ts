import axiosInstance from "@/lib/axios-instance";
import { LoginResponse, AccountResponse, RegisterResponse, User, UserRole, BackendUser } from "@/types/auth";

export const mapBackendUserToFrontend = (backendUser: BackendUser): User => {
  let role: UserRole = "student";
  const backendRoleName = backendUser.role?.name || "";
  const name = backendRoleName.toUpperCase();
  
  if (name.includes("ADMIN")) role = "admin";
  else if (name.includes("LECTURER") || name.includes("TEACHER")) role = "lecturer";
  else role = "student";
  
  return {
    id: backendUser.id,
    name: backendUser.name || backendUser.email.split("@")[0],
    email: backendUser.email,
    role: role,
  };
};

const authService = {
  login: async (username: string, password: string): Promise<LoginResponse> => {
    return axiosInstance.post("/auth/login", { username, password });
  },

  register: async (name: string, email: string, password: string): Promise<RegisterResponse> => {
    return axiosInstance.post("/auth/register", { name, email, password });
  },

  logout: async (): Promise<void> => {
    return axiosInstance.post("/auth/logout");
  },

  getAccount: async (): Promise<AccountResponse> => {
    return axiosInstance.get("/auth/account");
  },
};

export default authService;
