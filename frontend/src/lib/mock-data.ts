import { PlagiarismResult } from "./plagiarism-engine";

export interface CheckHistoryItem {
  id: string;
  title: string;
  date: string;
  percentage: number;
  severity: PlagiarismResult["severity"];
  wordCount: number;
  userName?: string;
  userEmail?: string;
}

export interface ClassItem {
  id: string;
  name: string;
  code: string;
  lecturerName: string;
  studentCount: number;
  threshold: number;
  description?: string;
}

export interface ClassStudent {
  id: string;
  name: string;
  email: string;
  joinDate: string;
  status: "approved" | "pending";
}

export interface SubmissionItem {
  id: string;
  studentName: string;
  studentEmail: string;
  title: string;
  date: string;
  percentage: number;
  severity: PlagiarismResult["severity"];
  className: string;
  classId: string;
  assignmentId?: string;
  content?: string;
  status?: "clean" | "flagged";
}

export interface AssignmentItem {
  id: string;
  classId: string;
  title: string;
  description: string;
  dueDate: string;
  createdDate: string;
}

export interface SystemStats {
  totalUsers: number;
  totalClasses: number;
  totalChecks: number;
  totalSubmissions: number;
}

export interface PlagiarismLevel {
  id: string;
  label: string;
  minPercent: number;
  maxPercent: number;
  color: string;
}

export const DEFAULT_PLAGIARISM_LEVELS: PlagiarismLevel[] = [
  { id: "l1", label: "SAFE", minPercent: 0, maxPercent: 20, color: "hsl(142, 71%, 45%)" },
  { id: "l2", label: "LOW", minPercent: 21, maxPercent: 40, color: "hsl(45, 93%, 47%)" },
  { id: "l3", label: "MODERATE", minPercent: 41, maxPercent: 60, color: "hsl(25, 95%, 53%)" },
  { id: "l4", label: "HIGH", minPercent: 61, maxPercent: 80, color: "hsl(0, 72%, 51%)" },
  { id: "l5", label: "SEVERE", minPercent: 81, maxPercent: 100, color: "hsl(0, 84%, 40%)" },
];

export const MOCK_CLASS_LEVELS: Record<string, PlagiarismLevel[]> = {
  "1": [
    { id: "l1", label: "SAFE", minPercent: 0, maxPercent: 20, color: "hsl(142, 71%, 45%)" },
    { id: "l2", label: "LOW", minPercent: 21, maxPercent: 40, color: "hsl(45, 93%, 47%)" },
    { id: "l3", label: "MODERATE", minPercent: 41, maxPercent: 60, color: "hsl(25, 95%, 53%)" },
    { id: "l4", label: "HIGH", minPercent: 61, maxPercent: 80, color: "hsl(0, 72%, 51%)" },
    { id: "l5", label: "SEVERE", minPercent: 81, maxPercent: 100, color: "hsl(0, 84%, 40%)" },
  ],
  "2": [
    { id: "l1", label: "SAFE", minPercent: 0, maxPercent: 25, color: "hsl(142, 71%, 45%)" },
    { id: "l2", label: "LOW", minPercent: 26, maxPercent: 45, color: "hsl(45, 93%, 47%)" },
    { id: "l3", label: "MODERATE", minPercent: 46, maxPercent: 65, color: "hsl(25, 95%, 53%)" },
    { id: "l4", label: "HIGH", minPercent: 66, maxPercent: 85, color: "hsl(0, 72%, 51%)" },
    { id: "l5", label: "SEVERE", minPercent: 86, maxPercent: 100, color: "hsl(0, 84%, 40%)" },
  ],
  "3": [
    { id: "l1", label: "SAFE", minPercent: 0, maxPercent: 30, color: "hsl(142, 71%, 45%)" },
    { id: "l2", label: "LOW", minPercent: 31, maxPercent: 50, color: "hsl(45, 93%, 47%)" },
    { id: "l3", label: "MODERATE", minPercent: 51, maxPercent: 70, color: "hsl(25, 95%, 53%)" },
    { id: "l4", label: "HIGH", minPercent: 71, maxPercent: 90, color: "hsl(0, 72%, 51%)" },
    { id: "l5", label: "SEVERE", minPercent: 91, maxPercent: 100, color: "hsl(0, 84%, 40%)" },
  ],
};



export const MOCK_HISTORY: CheckHistoryItem[] = [
  { id: "1", title: "Research Paper - AI Ethics.pdf", date: "2026-03-07", percentage: 12, severity: "safe", wordCount: 3200, userName: "Alex Student", userEmail: "student@demo.com" },
  { id: "2", title: "Software Engineering Essay.docx", date: "2026-03-06", percentage: 35, severity: "low", wordCount: 2100, userName: "Alice Johnson", userEmail: "alice@uni.edu" },
  { id: "3", title: "Database Systems Report.txt", date: "2026-03-05", percentage: 52, severity: "medium", wordCount: 4500, userName: "Bob Smith", userEmail: "bob@uni.edu" },
  { id: "4", title: "Machine Learning Analysis.pdf", date: "2026-03-04", percentage: 8, severity: "safe", wordCount: 1800, userName: "Alex Student", userEmail: "student@demo.com" },
  { id: "5", title: "Network Security Review.docx", date: "2026-03-03", percentage: 73, severity: "high", wordCount: 5200, userName: "David Lee", userEmail: "david@uni.edu" },
  { id: "6", title: "Cloud Computing Overview.pdf", date: "2026-03-02", percentage: 28, severity: "low", wordCount: 3100, userName: "Carol Davis", userEmail: "carol@uni.edu" },
  { id: "7", title: "Cybersecurity Trends 2026.docx", date: "2026-03-01", percentage: 5, severity: "safe", wordCount: 2800, userName: "Dr. Sarah Lecturer", userEmail: "lecturer@demo.com" },
  { id: "8", title: "IoT Architecture Paper.pdf", date: "2026-02-28", percentage: 61, severity: "high", wordCount: 4100, userName: "Alice Johnson", userEmail: "alice@uni.edu" },
];

export const MOCK_CLASSES: ClassItem[] = [
  { id: "1", name: "Introduction to Computer Science", code: "CS101-A", lecturerName: "Dr. Sarah Lecturer", studentCount: 32, threshold: 20, description: "Fundamental concepts of computer science including programming, algorithms, and data representation." },
  { id: "2", name: "Data Structures & Algorithms", code: "CS201-B", lecturerName: "Dr. Sarah Lecturer", studentCount: 28, threshold: 25, description: "Advanced data structures, algorithm design, and complexity analysis." },
  { id: "3", name: "Software Engineering", code: "SE301-A", lecturerName: "Dr. Sarah Lecturer", studentCount: 24, threshold: 30, description: "Software development lifecycle, design patterns, and project management." },
];

export const MOCK_CLASS_STUDENTS: Record<string, ClassStudent[]> = {
  "1": [
    { id: "s1", name: "Alex Student", email: "student@demo.com", joinDate: "2026-01-15", status: "approved" },
    { id: "s2", name: "Alice Johnson", email: "alice@uni.edu", joinDate: "2026-01-18", status: "approved" },
    { id: "s3", name: "Bob Smith", email: "bob@uni.edu", joinDate: "2026-01-20", status: "approved" },
    { id: "s4", name: "Carol Davis", email: "carol@uni.edu", joinDate: "2026-01-22", status: "pending" },
    { id: "s5", name: "David Lee", email: "david@uni.edu", joinDate: "2026-02-01", status: "pending" },
    { id: "s7", name: "Frank Brown", email: "frank@uni.edu", joinDate: "2026-03-10", status: "pending" },
  ],
  "2": [
    { id: "s1", name: "Alex Student", email: "student@demo.com", joinDate: "2026-02-01", status: "approved" },
    { id: "s2", name: "Alice Johnson", email: "alice@uni.edu", joinDate: "2026-02-03", status: "approved" },
    { id: "s6", name: "Emma Wilson", email: "emma@uni.edu", joinDate: "2026-02-05", status: "pending" },
  ],
  "3": [
    { id: "s3", name: "Bob Smith", email: "bob@uni.edu", joinDate: "2026-02-10", status: "approved" },
    { id: "s4", name: "Carol Davis", email: "carol@uni.edu", joinDate: "2026-02-12", status: "pending" },
  ],
};

export const MOCK_ASSIGNMENTS: AssignmentItem[] = [
  { id: "a1", classId: "1", title: "Assignment 1 - Sorting Algorithms", description: "Implement and compare at least 3 sorting algorithms. Analyze their time complexity.", dueDate: "2026-03-15", createdDate: "2026-02-20" },
  { id: "a2", classId: "1", title: "Assignment 2 - Recursion Problems", description: "Solve 5 recursion problems and explain your approach for each.", dueDate: "2026-03-22", createdDate: "2026-03-01" },
  { id: "a3", classId: "2", title: "Assignment 1 - Linked Lists", description: "Implement singly and doubly linked lists with all standard operations.", dueDate: "2026-03-18", createdDate: "2026-02-25" },
  { id: "a4", classId: "2", title: "Assignment 2 - Binary Trees", description: "Implement a BST with insert, delete, search, and traversal operations.", dueDate: "2026-03-25", createdDate: "2026-03-05" },
  { id: "a5", classId: "3", title: "Lab Report - OOP Principles", description: "Write a report on OOP principles with code examples in Java or Python.", dueDate: "2026-03-20", createdDate: "2026-03-01" },
];

export const MOCK_SUBMISSIONS: SubmissionItem[] = [
  { id: "1", studentName: "Alice Johnson", studentEmail: "alice@uni.edu", title: "Assignment 1 - Sorting Algorithms", date: "2026-03-07", percentage: 15, severity: "safe", className: "Introduction to Computer Science", classId: "1", assignmentId: "a1", status: "clean" },
  { id: "2", studentName: "Bob Smith", studentEmail: "bob@uni.edu", title: "Assignment 1 - Sorting Algorithms", date: "2026-03-07", percentage: 42, severity: "medium", className: "Introduction to Computer Science", classId: "1", assignmentId: "a1", status: "flagged" },
  { id: "3", studentName: "Carol Davis", studentEmail: "carol@uni.edu", title: "Assignment 2 - Binary Trees", date: "2026-03-06", percentage: 8, severity: "safe", className: "Data Structures & Algorithms", classId: "2", assignmentId: "a4", status: "clean" },
  { id: "4", studentName: "David Lee", studentEmail: "david@uni.edu", title: "Lab Report - OOP Principles", date: "2026-03-05", percentage: 67, severity: "high", className: "Software Engineering", classId: "3", assignmentId: "a5", status: "flagged" },
  { id: "5", studentName: "Alex Student", studentEmail: "student@demo.com", title: "Assignment 1 - Sorting Algorithms", date: "2026-03-07", percentage: 18, severity: "safe", className: "Introduction to Computer Science", classId: "1", assignmentId: "a1", status: "clean" },
  { id: "6", studentName: "Alex Student", studentEmail: "student@demo.com", title: "Assignment 2 - Binary Trees", date: "2026-03-06", percentage: 22, severity: "low", className: "Data Structures & Algorithms", classId: "2", assignmentId: "a4", status: "clean" },
  { id: "7", studentName: "Emma Wilson", studentEmail: "emma@uni.edu", title: "Assignment 2 - Binary Trees", date: "2026-03-06", percentage: 55, severity: "medium", className: "Data Structures & Algorithms", classId: "2", assignmentId: "a4", status: "flagged" },
];

export const MOCK_STATS: SystemStats = {
  totalUsers: 284,
  totalClasses: 12,
  totalChecks: 1847,
  totalSubmissions: 956,
};

export const MOCK_MONTHLY_CHECKS = [
  { month: "Oct", checks: 120, submissions: 65 },
  { month: "Nov", checks: 185, submissions: 92 },
  { month: "Dec", checks: 145, submissions: 78 },
  { month: "Jan", checks: 230, submissions: 115 },
  { month: "Feb", checks: 310, submissions: 168 },
  { month: "Mar", checks: 280, submissions: 142 },
];

export const MOCK_SEVERITY_DISTRIBUTION = [
  { name: "Safe", value: 542, fill: "hsl(142, 71%, 45%)" },
  { name: "Low", value: 328, fill: "hsl(45, 93%, 47%)" },
  { name: "Medium", value: 187, fill: "hsl(25, 95%, 53%)" },
  { name: "High", value: 89, fill: "hsl(0, 72%, 51%)" },
];

export const MOCK_ALL_USERS = [
  { id: "1", name: "Alex Student", email: "student@demo.com", role: "student" as const, joinDate: "2026-01-15" },
  { id: "2", name: "Dr. Sarah Lecturer", email: "lecturer@demo.com", role: "lecturer" as const, joinDate: "2025-09-01" },
  { id: "3", name: "Admin User", email: "admin@demo.com", role: "admin" as const, joinDate: "2025-08-01" },
  { id: "4", name: "Alice Johnson", email: "alice@uni.edu", role: "student" as const, joinDate: "2026-02-10" },
  { id: "5", name: "Bob Smith", email: "bob@uni.edu", role: "student" as const, joinDate: "2026-02-12" },
  { id: "6", name: "Carol Davis", email: "carol@uni.edu", role: "student" as const, joinDate: "2026-01-20" },
  { id: "7", name: "David Lee", email: "david@uni.edu", role: "student" as const, joinDate: "2026-02-15" },
  { id: "8", name: "Emma Wilson", email: "emma@uni.edu", role: "student" as const, joinDate: "2026-02-20" },
  { id: "9", name: "Dr. James Miller", email: "jmiller@uni.edu", role: "lecturer" as const, joinDate: "2025-10-01" },
];
