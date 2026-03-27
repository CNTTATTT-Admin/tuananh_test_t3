import axiosInstance from "@/lib/axios-instance";

export interface User {
  id: number;
  name: string;
  email: string;
  role?: {
    id: number;
    name: string;
  };
  gender?: string;
  age?: number;
  createdAt?: string;
}

export interface PaginatedUsers {
  meta: {
    page: number;
    pageSize: number;
    pages: number;
    total: number;
  };
  results: User[];
}

export const userService = {
  getAllUsers: async (page = 1, size = 10, filter = ""): Promise<PaginatedUsers> => {
    const springPage = page > 0 ? page - 1 : 0;
    const response = await axiosInstance.get(`/users?page=${springPage}&size=${size}${filter ? `&filter=${filter}` : ""}`);
    return response as any;
  },

  getUserById: async (id: number): Promise<User> => {
    const response = await axiosInstance.get(`/users/${id}`);
    return response as any;
  },

  createUser: async (user: any): Promise<User> => {
    const response = await axiosInstance.post("/users", user);
    return response as any;
  },

  updateUser: async (user: any): Promise<User> => {
    const response = await axiosInstance.put("/users", user);
    return response as any;
  },

  deleteUser: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/users/${id}`);
  },

  promoteToLecturer: async (id: number): Promise<void> => {
    await axiosInstance.put(`/users/lecturer/${id}`);
  }
};
