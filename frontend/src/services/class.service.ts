import axiosInstance from "@/lib/axios-instance";
import { ClassRoom, Assignment, ClassStudent, PlagiarismThreshold, Submission } from "@/types/class";

const classService = {
  // Classes
  getClasses: async (query: string = ""): Promise<any> => {
    return axiosInstance.get(`/classes?${query}`);
  },
  getClassById: async (id: number): Promise<ClassRoom> => {
    return axiosInstance.get(`/classes/${id}`);
  },
  createClass: async (name: string, description: string): Promise<ClassRoom> => {
    return axiosInstance.post("/classes", { name, description });
  },
  updateClass: async (id: number, name: string, description: string): Promise<ClassRoom> => {
    return axiosInstance.put("/classes", { id, name, description });
  },
  deleteClass: async (id: number): Promise<void> => {
    return axiosInstance.delete(`/classes/${id}`);
  },
  joinClass: async (classCode: string): Promise<any> => {
    const response = await axiosInstance.post("/classes/join", { classCode });
    return response.data;
  },
  leaveClass: async (classId: number): Promise<void> => {
    return axiosInstance.put("/classes/leave", classId);
  },

  // Students in Class
  getStudentsInClass: async (classId: number): Promise<any> => {
    return axiosInstance.get(`/classes/user/${classId}`);
  },
  addStudentToClass: async (email: string, classId: number): Promise<void> => {
    return axiosInstance.post("/classes/add", { email, classId });
  },
  removeStudentFromClass: async (userId: number, classId: number): Promise<void> => {
    return axiosInstance.delete(`/classes/${classId}/remove/${userId}`);
  },
  approveStudent: async (userId: number, classId: number): Promise<void> => {
    return axiosInstance.put("/classes/approved", { userId, classId });
  },
  rejectStudent: async (userId: number, classId: number): Promise<void> => {
    return axiosInstance.put("/classes/rejected", { userId, classId });
  },

  // Assignments
  getAssignmentsByClass: async (classId: number): Promise<any> => {
    return axiosInstance.get(`/assignments?filter=classRoom.id:${classId}`);
  },
  createAssignment: async (classId: number, title: string, description: string, dueDate: string): Promise<Assignment> => {
    return axiosInstance.post("/assignments", { classId, title, description, dueDate });
  },
  updateAssignment: async (id: number, title: string, description: string, dueDate: string): Promise<Assignment> => {
    return axiosInstance.put("/assignments", { id, title, description, dueDate });
  },
  deleteAssignment: async (id: number): Promise<void> => {
    return axiosInstance.delete(`/assignments/${id}`);
  },

  // Thresholds
  getThresholdsByClass: async (classId: number): Promise<PlagiarismThreshold[]> => {
    return axiosInstance.get(`/thresholds/class/${classId}`);
  },
  createThreshold: async (classId: number, levelName: string, min: number, max: number, color: string): Promise<PlagiarismThreshold> => {
    return axiosInstance.post("/thresholds", { classId, levelName, min, max, color });
  },
  updateThreshold: async (id: number, levelName: string, min: number, max: number, color: string): Promise<PlagiarismThreshold> => {
    return axiosInstance.put("/thresholds", { id, levelName, min, max, color });
  },
  deleteThreshold: async (id: number): Promise<void> => {
    return axiosInstance.delete(`/thresholds/${id}`);
  },
  saveAllThresholds: async (classId: number, thresholds: any[]): Promise<PlagiarismThreshold[]> => {
    return axiosInstance.post("/thresholds/all", { classId, thresholds });
  },

  // Submissions
  getSubmissionsByAssignment: async (assignmentId: number): Promise<Submission[]> => {
    return axiosInstance.get(`/assignment/${assignmentId}`);
  },
  getMySubmissions: async (studentId: number): Promise<Submission[]> => {
    return axiosInstance.get(`/my-history?studentId=${studentId}`);
  },
  submitAssignment: async (assignmentId: number | null, content: string, file: File | null): Promise<Submission> => {
    const formData = new FormData();
    if (assignmentId) {
      formData.append("assignmentId", assignmentId.toString());
    }
    if (content) formData.append("content", content);
    if (file) formData.append("file", file);

    return axiosInstance.post("/submissions", formData);
  },
  getSubmissionById: async (id: number): Promise<Submission> => {
    return axiosInstance.get(`/submissions/${id}`);
  },
  getAllSubmissions: async (): Promise<Submission[]> => {
    return axiosInstance.get("/submissions");
  },
  getMatchesBySubmissionId: async (submissionId: number): Promise<any[]> => {
    return axiosInstance.get(`/checks/${submissionId}`);
  },
};

export default classService;
