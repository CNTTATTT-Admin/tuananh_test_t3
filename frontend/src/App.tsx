import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "@/lib/auth-context";
import Index from "./pages/Index";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import DashboardHome from "./pages/dashboard/DashboardHome";
import HistoryPage from "./pages/dashboard/HistoryPage";
import ClassesPage from "./pages/dashboard/ClassesPage";
import ClassDetailPage from "./pages/dashboard/ClassDetailPage";
import SubmissionsPage from "./pages/dashboard/SubmissionsPage";
import SubmissionDetailPage from "./pages/dashboard/SubmissionDetailPage";
import UsersPage from "./pages/dashboard/UsersPage";
import StatsPage from "./pages/dashboard/StatsPage";
import MonitorPage from "./pages/dashboard/MonitorPage";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient();

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <AuthProvider>
        <Toaster />
        <Sonner />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }
            >
              <Route index element={<DashboardHome />} />
              <Route path="history" element={<HistoryPage />} />
              <Route path="classes" element={<ClassesPage />} />
              <Route path="classes/:classId" element={<ClassDetailPage />} />
              <Route path="submissions" element={<SubmissionsPage />} />
              <Route path="submissions/:id" element={<SubmissionDetailPage />} />
              <Route path="users" element={<UsersPage />} />
              <Route path="stats" element={<StatsPage />} />
              <Route path="monitor" element={<MonitorPage />} />
            </Route>
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
