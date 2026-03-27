import axiosInstance from "@/lib/axios-instance";

export interface Role {
  id: number;
  name: string;
  description?: string;
}

export interface PaginatedRoles {
  meta: {
    page: number;
    pageSize: number;
    pages: number;
    total: number;
  };
  results: Role[];
}

export const roleService = {
  getAllRoles: async (page = 1, size = 100): Promise<PaginatedRoles> => {
    const springPage = page > 0 ? page - 1 : 0;
    const response = await axiosInstance.get(`/roles?page=${springPage}&size=${size}`);
    return response as any;
  },

  getRoleById: async (id: number): Promise<Role> => {
    const response = await axiosInstance.get(`/roles/${id}`);
    return response as any;
  },

  createRole: async (role: any): Promise<Role> => {
    const response = await axiosInstance.post("/roles", role);
    return response as any;
  },

  updateRole: async (role: any): Promise<Role> => {
    const response = await axiosInstance.put("/roles", role);
    return response as any;
  },

  deleteRole: async (id: number): Promise<void> => {
    await axiosInstance.delete(`/roles/${id}`);
  }
};
