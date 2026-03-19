import { createContext, useContext, useState, ReactNode, useEffect } from "react";
import authService, { mapBackendUserToFrontend } from "@/services/auth.service";
import { User, UserRole } from "@/types/auth";
import { toast } from "sonner";

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string, role: UserRole) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem("access_token");
      if (token) {
        try {
          const data = await authService.getAccount();
          if (data && data.user) {
            setUser(mapBackendUserToFrontend(data.user));
          } else {
            localStorage.removeItem("access_token");
          }
        } catch (error) {
          console.error("Auth initialization failed:", error);
          localStorage.removeItem("access_token");
        }
      }
      setIsLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const data = await authService.login(email, password);

      if (data && data.access_token) {
        localStorage.setItem("access_token", data.access_token);
        if (data.user) {
          setUser(mapBackendUserToFrontend(data.user));
        }
        toast.success("Logged in successfully");
      }
    } catch (error: any) {
      toast.error(error.message || "Login failed");
      throw error;
    }
  };

  const register = async (name: string, email: string, password: string, _role: UserRole) => {
    try {
      await authService.register(name, email, password);
      toast.success("Account created successfully. Please login.");
    } catch (error: any) {
      toast.error(error.message || "Registration failed");
      throw error;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      localStorage.removeItem("access_token");
      setUser(null);
      toast.info("Logged out");
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, isAuthenticated: !!user, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
