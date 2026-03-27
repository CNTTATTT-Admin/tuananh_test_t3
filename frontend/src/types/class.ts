import { User } from "./auth";

export interface ClassRoom {
  id: number;
  name: string;
  description: string;
  classCode: string;
  lecturer?: User;
}

export interface Assignment {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  createdDate: string;
  classRoom?: ClassRoom;
}

export interface ClassStudent {
  id: number;
  name: string;
  email: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
}

export interface PlagiarismThreshold {
  id: number;
  levelName: string;
  min: number;
  max: number;
  color: string;
  description?: string;
  isDefault?: boolean;
}

export interface Submission {
  id: number;
  assignmentId?: number;
  assignmentTitle?: string;
  studentId: number;
  studentName: string;
  studentEmail?: string;
  classId?: number;
  plagiarismPercent: number;
  status: string;
  submittedAt: string;
  content?: string;
  fileUrl?: string;
}
