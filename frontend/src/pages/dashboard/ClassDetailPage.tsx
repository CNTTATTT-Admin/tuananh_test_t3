import { useState, useEffect, useCallback } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { useAuth } from "@/lib/auth-context";
import { SeverityBadge } from "@/components/SeverityBadge";
import { PlagiarismChecker } from "@/components/PlagiarismChecker";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ArrowLeft, Users, FileText, Upload, UserMinus, Settings2, Plus, ClipboardList, Calendar, Gauge, Trash2, CheckCircle2, Clock, CheckCheck, Loader2, ExternalLink, LogOut, ChevronRight } from "lucide-react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { PlagiarismResult } from "@/lib/plagiarism-engine";
import classService from "@/services/class.service";
import { ClassRoom, Assignment, ClassStudent, PlagiarismThreshold, Submission } from "@/types/class";

export default function ClassDetailPage() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isLecturer = user?.role === "lecturer";

  const [cls, setCls] = useState<ClassRoom | null>(null);
  const [localStudents, setLocalStudents] = useState<ClassStudent[]>([]);
  const [localAssignments, setLocalAssignments] = useState<Assignment[]>([]);
  const [levels, setLevels] = useState<PlagiarismThreshold[]>([]);
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [activeTab, setActiveTab] = useState("assignments");
  const [mySubmissions, setMySubmissions] = useState<Submission[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Form states
  const [createOpen, setCreateOpen] = useState(false);
  const [newAssignmentTitle, setNewAssignmentTitle] = useState("");
  const [newAssignmentDesc, setNewAssignmentDesc] = useState("");
  const [newAssignmentDue, setNewAssignmentDue] = useState("");

  // Submit assignment dialog
  const [submitOpen, setSubmitOpen] = useState(false);
  const [addStudentOpen, setAddStudentOpen] = useState(false);
  const [newStudentEmail, setNewStudentEmail] = useState("");
  const [selectedAssignment, setSelectedAssignment] = useState<Assignment | null>(null);

  // Level editing
  const [editingLevels, setEditingLevels] = useState<PlagiarismThreshold[]>([]);
  const [levelsDialogOpen, setLevelsDialogOpen] = useState(false);

  const fetchData = useCallback(async () => {
    if (!classId) return;
    const clsIdNum = parseInt(classId);
    setIsLoading(true);
    
    try {
      // 1. Mandatory data for both
      const [classData, assignmentsData, studentsData] = await Promise.all([
        classService.getClassById(clsIdNum),
        classService.getAssignmentsByClass(clsIdNum),
        classService.getStudentsInClass(clsIdNum)
      ]);

      setCls(classData);
      setLocalAssignments(assignmentsData.results || assignmentsData);
      setLocalStudents(studentsData.user || []);

      // 2. Teacher-only data
      if (isLecturer) {
        try {
          const thresholds = await classService.getThresholdsByClass(clsIdNum);
          setLevels(thresholds);
        } catch (e) {
          console.warn("Could not load thresholds", e);
        }
      }

      // 3. Student-only data
      if (!isLecturer && user?.id) {
        try {
          const subsResponse = await classService.getMySubmissions(user.id);
          const subs = (subsResponse as any).results || subsResponse;
          const classAssignmentIds = (assignmentsData.results || assignmentsData).map((a: any) => a.id);
          setMySubmissions(Array.isArray(subs) ? subs.filter((s: any) => s.assignmentId && classAssignmentIds.includes(s.assignmentId)) : []);
        } catch (e) {
          console.error("Failed to load subs", e);
        }
      }
    } catch (error: any) {
      toast.error(error.message || "Failed to load class data");
      navigate("/dashboard/classes"); // Go back if unauthorized or not found
    } finally {
      setIsLoading(false);
    }
  }, [classId, isLecturer, user?.id, navigate]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleCreateAssignment = async () => {
    if (!classId || !newAssignmentTitle.trim() || !newAssignmentDue) return;
    try {
      await classService.createAssignment(
        parseInt(classId),
        newAssignmentTitle.trim(),
        newAssignmentDesc.trim(),
        newAssignmentDue + "T23:59:59"
      );
      toast.success("Assignment created!");
      setNewAssignmentTitle("");
      setNewAssignmentDesc("");
      setNewAssignmentDue("");
      setCreateOpen(false);
      fetchData();
    } catch (error: any) {
      toast.error(error.message || "Failed to create assignment");
    }
  };

  const handleSubmission = async (text: string, _result: PlagiarismResult, file: File | null) => {
    if (!selectedAssignment) return;
    try {
      toast.loading("Submitting...", { id: "submitting" });
      await classService.submitAssignment(selectedAssignment.id, text, file);
      toast.success("Assignment submitted successfully!", { id: "submitting" });
      setSubmitOpen(false);
      setSelectedAssignment(null);
      fetchData(); 
    } catch (error: any) {
      toast.error(error.message || "Failed to submit assignment", { id: "submitting" });
    }
  };

  const loadSubmissionsForAssignment = async (assignment: Assignment) => {
    setSelectedAssignment(assignment);
    setActiveTab("submissions");
    try {
      const resp = await classService.getSubmissionsByAssignment(assignment.id);
      setSubmissions(resp);
    } catch (error: any) {
      toast.error(error.message || "Failed to load submissions");
    }
  };

  const getLevelForPercentage = (percentage: number) => {
    return levels.find(l => percentage >= l.min && percentage <= l.max);
  };

  const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
    if (percentage <= 20) return "safe";
    if (percentage <= 40) return "low";
    if (percentage <= 60) return "medium";
    return "high";
  };

  const handleApprove = async (studentId: number) => {
    if (!classId) return;
    try {
      await classService.approveStudent(studentId, parseInt(classId));
      toast.success("Student approved!");
      fetchData();
    } catch (error: any) {
      toast.error("Failed to approve student");
    }
  };

  const handleReject = async (studentId: number) => {
    if (!classId) return;
    try {
      await classService.rejectStudent(studentId, parseInt(classId));
      toast.success("Student rejected.");
      fetchData();
    } catch (error: any) {
      toast.error("Failed to reject student");
    }
  };

  const handleRemove = async (studentId: number) => {
    if (!classId) return;
    try {
      await classService.removeStudentFromClass(studentId, parseInt(classId));
      toast.success("Student removed.");
      fetchData();
    } catch (error: any) {
      toast.error("Failed to remove student");
    }
  };

  const handleLeaveClass = async () => {
    if (!classId || !window.confirm("Are you sure you want to leave this class?")) return;
    try {
      await classService.leaveClass(parseInt(classId));
      toast.success("You have left the class.");
      navigate("/dashboard/classes");
    } catch (error: any) {
      toast.error(error.message || "Failed to leave class");
    }
  };

  const handleAddStudent = async () => {
    if (!classId || !newStudentEmail.trim()) return;
    try {
      await classService.addStudentToClass(newStudentEmail.trim(), parseInt(classId));
      toast.success("Student added to class!");
      setNewStudentEmail("");
      setAddStudentOpen(false);
      fetchData();
    } catch (error: any) {
      toast.error(error.message || "Failed to add student");
    }
  };

  const handleViewSubmissions = async (assignment: Assignment) => {
    setSelectedAssignment(assignment);
    setActiveTab("submissions");
    try {
      const resp = await classService.getSubmissionsByAssignment(assignment.id);
      setSubmissions(resp);
    } catch (error: any) {
      toast.error(error.message || "Failed to load submissions");
    }
  };

  const handleSaveLevels = async () => {
    if (!classId) return;
    try {
      const clsId = parseInt(classId);
      // For simplicity, we just save/update what's in the editing list
      // In a real app, you'd handle deletions too
      await Promise.all(editingLevels.map(l => {
          if (!l.isDefault && l.id > 10000) { // New one
              return classService.createThreshold(clsId, l.levelName, l.min, l.max, l.color);
          } else { // Existing one
              return classService.updateThreshold(l.id, l.levelName, l.min, l.max, l.color);
          }
      }));
      
      toast.success("Plagiarism matrix updated!");
      setLevelsDialogOpen(false);
      fetchData();
    } catch (error: any) {
      toast.error(error.message || "Failed to update thresholds");
    }
  };

  const handleAddLevel = () => {
    const lastMax = editingLevels.length > 0 ? Math.max(...editingLevels.map(l => l.max)) : -1;
    if (lastMax >= 100) return;
    setEditingLevels([...editingLevels, {
      id: Date.now(),
      levelName: "NEW",
      min: lastMax + 1,
      max: Math.min(lastMax + 20, 100),
      color: "#3b82f6"
    }]);
  };

  const handleUpdateLevel = (id: number, field: keyof PlagiarismThreshold, value: any) => {
    setEditingLevels(editingLevels.map(l => l.id === id ? { ...l, [field]: value } : l));
  };

  const handleRemoveLevel = (id: number) => {
    setEditingLevels(editingLevels.filter(l => l.id !== id));
  };

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-20">
        <Loader2 className="w-10 h-10 animate-spin text-primary mb-4" />
        <p className="text-muted-foreground italic tracking-wide">Connecting to classroom...</p>
      </div>
    );
  }

  if (!cls) {
    return (
      <div className="max-w-3xl mx-auto text-center py-12">
        <p className="text-muted-foreground">Class not found.</p>
        <Link to="/dashboard/classes">
          <Button variant="outline" className="mt-4 gap-1.5 hover:bg-muted transition-colors">
            <ArrowLeft className="w-4 h-4" /> Back to Classes
          </Button>
        </Link>
      </div>
    );
  }

  const pendingStudents = localStudents.filter(s => s.status?.toUpperCase() === "PENDING");
  const approvedStudents = localStudents.filter(s => s.status?.toUpperCase() === "APPROVED");
  const rejectedStudents = localStudents.filter(s => s.status?.toUpperCase() === "REJECTED");

  return (
    <div className="max-w-5xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-3 mb-2">
        <Link to="/dashboard/classes">
          <Button variant="ghost" size="icon" className="h-8 w-8">
            <ArrowLeft className="w-4 h-4" />
          </Button>
        </Link>
        <div>
          <h1 className="text-3xl font-serif font-bold">{cls.name}</h1>
          <p className="text-sm text-muted-foreground mt-1">
             {cls.lecturer?.name} · Code: <span className="font-mono font-semibold text-primary">{cls.classCode}</span>
          </p>
        </div>
        {!isLecturer && (
          <Button 
            variant="outline" 
            size="sm" 
            className="ml-auto gap-2 text-destructive hover:bg-red-50"
            onClick={handleLeaveClass}
          >
            <LogOut className="w-4 h-4" /> Leave Class
          </Button>
        )}
      </div>
      {cls.description && (
        <p className="text-sm text-muted-foreground mb-4 ml-11 leading-relaxed italic">{cls.description}</p>
      )}

      {/* Level scale preview */}
      {levels.length > 0 && (
         <div className="ml-11 mb-6">
            <p className="text-xs text-muted-foreground font-bold uppercase tracking-widest mb-1.5 flex items-center gap-2">
                <Gauge className="w-3 h-3" /> Plagiarism Levels
            </p>
            <div className="flex rounded-full overflow-hidden h-2.5 max-w-md bg-muted border">
                {levels.map((level) => {
                    const width = level.max - level.min + 1;
                    return (
                        <div
                            key={level.id}
                            className="relative group cursor-default h-full"
                            style={{ width: `${width}%`, backgroundColor: level.color }}
                        >
                            <div className="absolute -top-8 left-1/2 -translate-x-1/2 hidden group-hover:block bg-popover text-popover-foreground text-[10px] font-bold px-2 py-1 rounded shadow-xl border whitespace-nowrap z-50">
                                {level.levelName}: {level.min}-{level.max}%
                            </div>
                        </div>
                    );
                })}
            </div>
            <div className="flex justify-between max-w-md mt-1.5">
                {levels.map(l => (
                    <span key={l.id} className="text-[10px] font-black uppercase tracking-tighter" style={{ color: l.color }}>{l.levelName}</span>
                ))}
            </div>
         </div>
      )}

      {/* Submit dialog */}
      <Dialog open={submitOpen} onOpenChange={(open) => { setSubmitOpen(open); if (!open) setSelectedAssignment(null); }}>
        <DialogContent className="sm:max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="font-serif text-2xl">Submit Assignment</DialogTitle>
          </DialogHeader>
          <PlagiarismChecker
            mode="submit"
            assignmentTitle={selectedAssignment?.title}
            onSubmit={handleSubmission}
            levels={levels.map(l => ({ 
                id: l.id.toString(), 
                label: l.levelName, 
                minPercent: l.min, 
                maxPercent: l.max, 
                color: l.color 
            }))}
          />
        </DialogContent>
      </Dialog>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
        <TabsList className="bg-muted/50 p-1 border h-10">
          <TabsTrigger value="assignments" className="gap-2 px-6">
            <ClipboardList className="w-3.5 h-3.5" /> Assignments ({localAssignments.length})
          </TabsTrigger>
          {!isLecturer && <TabsTrigger value="my-submissions" className="gap-2 px-6">My Submissions</TabsTrigger>}
          {isLecturer && <TabsTrigger value="submissions" className="gap-2 px-6">All Submissions</TabsTrigger>}
          <TabsTrigger value="students" className="gap-2 px-6">
            <Users className="w-3.5 h-3.5" /> Students ({approvedStudents.length})
            {isLecturer && pendingStudents.length > 0 && (
              <Badge variant="destructive" className="ml-1.5 h-5 min-w-5 px-1.5 text-[10px] bg-red-600 border-none">
                {pendingStudents.length}
              </Badge>
            )}
          </TabsTrigger>
          {isLecturer && (
            <TabsTrigger value="settings" className="gap-2 px-6">
              <Settings2 className="w-3.5 h-3.5" /> Settings
            </TabsTrigger>
          )}
        </TabsList>

        <TabsContent value="assignments" className="space-y-4 pt-4">
          <div className="flex justify-end gap-2">
            {isLecturer && (
              <Dialog open={createOpen} onOpenChange={setCreateOpen}>
                <DialogTrigger asChild>
                  <Button className="gap-1.5 shadow-sm">
                    <Plus className="w-4 h-4" /> Create Assignment
                  </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-lg">
                  <DialogHeader>
                    <DialogTitle className="font-serif text-xl">Create New Assignment</DialogTitle>
                  </DialogHeader>
                  <div className="space-y-4 pt-2">
                    <div className="space-y-1.5">
                      <Label>Title</Label>
                      <Input
                        value={newAssignmentTitle}
                        onChange={(e) => setNewAssignmentTitle(e.target.value)}
                        placeholder="e.g. Unit 4: System Architecture"
                      />
                    </div>
                    <div className="space-y-1.5">
                      <Label>Description</Label>
                      <Textarea
                        value={newAssignmentDesc}
                        onChange={(e) => setNewAssignmentDesc(e.target.value)}
                        placeholder="Describe the assignment requirements..."
                        className="min-h-[100px]"
                      />
                    </div>
                    <div className="space-y-1.5">
                      <Label>Due Date</Label>
                      <Input
                        type="date"
                        value={newAssignmentDue}
                        onChange={(e) => setNewAssignmentDue(e.target.value)}
                      />
                    </div>
                    <Button
                      className="w-full gap-1.5 h-10 font-bold"
                      onClick={handleCreateAssignment}
                      disabled={!newAssignmentTitle.trim() || !newAssignmentDue}
                    >
                      Publish Assignment
                    </Button>
                  </div>
                </DialogContent>
              </Dialog>
            )}
          </div>

          {localAssignments.length > 0 ? (
            <div className="grid gap-4 md:grid-cols-2">
              {localAssignments.map((assignment) => {
                const isOverdue = new Date(assignment.dueDate) < new Date();
                return (
                  <Card key={assignment.id} className="p-5 hover:shadow-md transition-all border-muted flex flex-col justify-between">
                    <div>
                        <div className="flex items-start justify-between gap-4 mb-2">
                          <div className="flex-1">
                            <h3 className="font-bold text-lg leading-tight">{assignment.title}</h3>
                            {assignment.description && (
                              <p className="text-sm text-muted-foreground mt-1 line-clamp-2">{assignment.description}</p>
                            )}
                          </div>
                        </div>
                        <div className="flex items-center gap-4 text-[11px] text-muted-foreground font-medium mt-4">
                          <span className={`flex items-center gap-1 ${isOverdue ? "text-destructive" : ""}`}>
                            <Clock className="w-3 h-3" />
                            {isOverdue ? "Closed: " : "Due: "} {assignment.dueDate}
                          </span>
                        </div>
                    </div>
                    <div className="flex items-center gap-2 mt-6 pt-4 border-t">
                        {!isLecturer && (
                          <Button
                            size="sm"
                            className="gap-1.5 h-8 flex-1"
                            disabled={isOverdue}
                            onClick={() => {
                              setSelectedAssignment(assignment);
                              setSubmitOpen(true);
                            }}
                          >
                            <Upload className="w-3.5 h-3.5" /> Submit Work
                          </Button>
                        )}
                        {isLecturer && (
                           <Button variant="outline" size="sm" className="gap-1.5 h-8 flex-1" onClick={() => loadSubmissionsForAssignment(assignment)}>
                             <Users className="w-3.5 h-3.5" /> View Submissions
                           </Button>
                        )}
                    </div>
                  </Card>
                );
              })}
            </div>
          ) : (
            <Card className="p-12 text-center border-dashed border-2">
              <ClipboardList className="w-10 h-10 text-muted-foreground/30 mx-auto mb-3" />
              <p className="text-muted-foreground italic">No assignments posted yet.</p>
            </Card>
          )}

          {/* Submissions area for lecturers */}
        </TabsContent>

        <TabsContent value="my-submissions" className="space-y-4 pt-4">
           {mySubmissions.length > 0 ? (
                <div className="grid gap-4">
                    {mySubmissions.map(sub => (
                        <Card key={sub.id} className="p-4 flex items-center justify-between">
                            <div className="flex items-center gap-3">
                                <div className="bg-primary/10 p-2 rounded-lg"><FileText className="w-5 h-5 text-primary" /></div>
                                <div>
                                    <h4 className="font-bold text-sm">{sub.assignmentTitle}</h4>
                                    <p className="text-xs text-muted-foreground">{new Date(sub.submittedAt).toLocaleString()}</p>
                                </div>
                            </div>
                            <div className="text-right flex items-center gap-4">
                                <div className="flex flex-col items-end">
                                    <span className="text-xl font-black">{sub.plagiarismPercent}%</span>
                                    <SeverityBadge severity={getSeverity(sub.plagiarismPercent)} />
                                </div>
                            </div>
                        </Card>
                    ))}
                </div>
           ) : (
                <Card className="p-12 text-center border-dashed border-2">
                    <FileText className="w-10 h-10 text-muted-foreground/20 mx-auto mb-3" />
                    <p className="text-muted-foreground italic">You haven't submitted anything to this class yet.</p>
                </Card>
           )}
        </TabsContent>

        <TabsContent value="submissions" className="space-y-4 pt-4">
            {selectedAssignment ? (
                <div className="space-y-4">
                    <div className="flex items-center justify-between mb-2">
                        <h2 className="text-xl font-bold">{selectedAssignment.title} <span className="text-muted-foreground font-normal">Submissions</span></h2>
                        <Button variant="ghost" size="sm" onClick={() => setSelectedAssignment(null)}>Clear Selection</Button>
                    </div>
                    {submissions.length > 0 ? (
                        <div className="grid gap-3">
                            {submissions.map(sub => (
                                <Card key={sub.id} className="p-4 flex items-center justify-between hover:bg-muted/30 transition-colors cursor-pointer" onClick={() => navigate(`/dashboard/submissions/${sub.id}`)}>
                                    <div className="flex items-center gap-4">
                                        <div className="w-10 h-10 rounded-full bg-primary/5 flex items-center justify-center font-bold text-primary">
                                            {sub.studentName?.charAt(0) || "S"}
                                        </div>
                                        <div>
                                            <h4 className="font-bold text-sm">{sub.studentName}</h4>
                                            <p className="text-xs text-muted-foreground">{new Date(sub.submittedAt).toLocaleString()}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-6">
                                        <div className="text-right">
                                            <div className="flex items-center gap-2 justify-end">
                                               <span className="text-lg font-black">{sub.plagiarismPercent}%</span>
                                               <SeverityBadge severity={getSeverity(sub.plagiarismPercent)} />
                                            </div>
                                            <p className="text-[10px] text-muted-foreground uppercase font-bold tracking-widest mt-1">Similarity Index</p>
                                        </div>
                                        <ChevronRight className="w-5 h-5 text-muted-foreground/30" />
                                    </div>
                                </Card>
                            ))}
                        </div>
                    ) : (
                        <Card className="p-12 text-center border-dashed border-2">
                            <Upload className="w-10 h-10 text-muted-foreground/20 mx-auto mb-3" />
                            <p className="text-muted-foreground italic">No students have submitted yet.</p>
                        </Card>
                    )}
                </div>
            ) : (
                <Card className="p-12 text-center">
                    <Users className="w-10 h-10 text-muted-foreground/20 mx-auto mb-3" />
                    <p className="text-muted-foreground italic tracking-wide">Select an assignment in the Overview tab to view its submissions.</p>
                </Card>
            )}
        </TabsContent>

        <TabsContent value="students" className="space-y-4 pt-4">
            {isLecturer && pendingStudents.length > 0 && (
                <Card className="shadow-lg overflow-hidden border-orange-200 bg-orange-50/10 mb-6">
                    <div className="px-5 py-3 bg-orange-50 border-b border-orange-100 flex items-center justify-between">
                        <h3 className="font-bold text-sm text-orange-800 flex items-center gap-2">
                            <Clock className="w-4 h-4" /> Enrollment Requests ({pendingStudents.length})
                        </h3>
                        <Button size="sm" variant="outline" className="h-7 border-orange-200 text-orange-700" onClick={() => pendingStudents.forEach(s => handleApprove(s.id))}>Approve All</Button>
                    </div>
                    <Table>
                        <TableBody>
                            {pendingStudents.map(s => (
                                <TableRow key={s.id}>
                                    <TableCell>
                                        <p className="font-bold text-sm">{s.name}</p>
                                        <p className="text-[10px] text-muted-foreground font-mono">{s.email}</p>
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <div className="flex justify-end gap-2">
                                            <Button size="sm" variant="outline" className="h-7 border-green-200 text-green-700" onClick={() => handleApprove(s.id)}>Approve</Button>
                                            <Button size="sm" variant="ghost" className="h-7 text-destructive" onClick={() => handleReject(s.id)}>Reject</Button>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Card>
            )}

            {isLecturer && rejectedStudents.length > 0 && (
                <Card className="shadow-sm overflow-hidden border-gray-200 bg-gray-50/10 mb-6 font-sans">
                    <div className="px-5 py-3 bg-gray-50 border-b border-gray-100 flex items-center justify-between">
                        <h3 className="font-bold text-xs text-gray-500 uppercase tracking-widest flex items-center gap-2">
                           <UserMinus className="w-4 h-4" /> Rejected Requests ({rejectedStudents.length})
                        </h3>
                    </div>
                    <Table>
                        <TableBody>
                            {rejectedStudents.map(s => (
                                <TableRow key={s.id} className="opacity-70 grayscale bg-white/50">
                                    <TableCell>
                                        <div className="flex flex-col">
                                            <span className="font-bold text-sm text-muted-foreground line-through decoration-destructive/30">{s.name}</span>
                                            <span className="text-[10px] text-muted-foreground font-mono">{s.email}</span>
                                        </div>
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Button size="sm" variant="ghost" className="h-7 text-primary hover:bg-primary/5 text-xs font-semibold" onClick={() => handleApprove(s.id)}>
                                            Approve
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Card>
            )}

            <Card className="shadow-sm overflow-hidden border-muted">
                <div className="px-5 py-3 bg-muted/40 border-b flex items-center justify-between">
                    <h3 className="font-bold text-sm">Approved Students ({approvedStudents.length})</h3>
                    {isLecturer && (
                        <Dialog open={addStudentOpen} onOpenChange={setAddStudentOpen}>
                            <DialogTrigger asChild>
                                <Button size="sm" variant="outline" className="h-8 gap-1.5 shadow-sm border-primary/20 text-primary hover:bg-primary/5">
                                    <Plus className="w-3.5 h-3.5" /> Add Student
                                </Button>
                            </DialogTrigger>
                            <DialogContent className="sm:max-w-md">
                                <DialogHeader>
                                    <DialogTitle className="font-serif">Add Student to Class</DialogTitle>
                                </DialogHeader>
                                <div className="space-y-4 pt-4">
                                    <div className="space-y-1.5">
                                        <Label>Student Email</Label>
                                        <Input 
                                            placeholder="student@university.edu" 
                                            value={newStudentEmail}
                                            onChange={(e) => setNewStudentEmail(e.target.value)}
                                        />
                                        <p className="text-[10px] text-muted-foreground italic">Note: If the student exists, they will be added directly. If not, they'll need to register first.</p>
                                    </div>
                                    <Button className="w-full h-10 font-bold" onClick={handleAddStudent} disabled={!newStudentEmail.trim()}>
                                        Add to Roster
                                    </Button>
                                </div>
                            </DialogContent>
                        </Dialog>
                    )}
                </div>
                {approvedStudents.length > 0 ? (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Name</TableHead>
                                <TableHead>Email</TableHead>
                                {isLecturer && <TableHead className="text-right">Actions</TableHead>}
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {approvedStudents.map(s => (
                                <TableRow key={s.id}>
                                    <TableCell className="font-medium text-sm">{s.name}</TableCell>
                                    <TableCell className="text-xs text-muted-foreground font-mono">{s.email}</TableCell>
                                    {isLecturer && (
                                        <TableCell className="text-right">
                                            <Button variant="ghost" size="icon" className="h-8 w-8 text-destructive/40 hover:text-destructive" onClick={() => handleRemove(s.id)}>
                                                <UserMinus className="w-3.5 h-3.5" />
                                            </Button>
                                        </TableCell>
                                    )}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                ) : (
                    <div className="p-10 text-center">
                        <Users className="w-10 h-10 text-muted-foreground/10 mx-auto mb-2" />
                        <p className="text-xs text-muted-foreground italic">Roster is currently empty.</p>
                    </div>
                )}
            </Card>
        </TabsContent>

        <TabsContent value="settings" className="space-y-6 pt-4">
            <Card className="p-6">
                <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-2">
                        <Gauge className="w-5 h-5 text-primary" />
                        <h3 className="text-lg font-bold font-serif">Configure Plagiarism Matrix</h3>
                    </div>
                    <Dialog open={levelsDialogOpen} onOpenChange={o => { setLevelsDialogOpen(o); if(o) setEditingLevels([...levels]); }}>
                        <DialogTrigger asChild><Button variant="outline" size="sm" className="gap-2"><Settings2 className="w-3.5 h-3.5" /> Edit Levels</Button></DialogTrigger>
                        <DialogContent className="sm:max-w-lg">
                            <DialogHeader><DialogTitle className="font-serif">Manage Danger Levels</DialogTitle></DialogHeader>
                            <div className="space-y-3 pt-4 max-h-[60vh] overflow-y-auto pr-2">
                                {editingLevels.sort((a,b) => a.min - b.min).map(l => (
                                    <div key={l.id} className="flex items-center gap-2 p-3 rounded-lg border bg-muted/20">
                                        <input type="color" value={l.color} onChange={e => handleUpdateLevel(l.id, "color", e.target.value)} className="w-7 h-7 rounded border-none p-0 cursor-pointer" />
                                        <Input value={l.levelName} onChange={e => handleUpdateLevel(l.id, "levelName", e.target.value.toUpperCase())} className="w-24 h-8 text-[10px] font-bold" />
                                        <div className="flex-1 flex items-center gap-1 justify-center">
                                            <Input type="number" value={l.min} onChange={e => handleUpdateLevel(l.id, "min", parseInt(e.target.value))} className="w-16 h-8 text-center" />
                                            <span className="text-muted-foreground">-</span>
                                            <Input type="number" value={l.max} onChange={e => handleUpdateLevel(l.id, "max", parseInt(e.target.value))} className="w-16 h-8 text-center" />
                                            <span className="text-[10px]">%</span>
                                        </div>
                                        <Button variant="ghost" size="icon" className="h-8 w-8 text-destructive" onClick={() => handleRemoveLevel(l.id)}><Trash2 className="w-3.5 h-3.5" /></Button>
                                    </div>
                                ))}
                                <Button variant="outline" size="sm" className="w-full gap-2 border-dashed" onClick={handleAddLevel}><Plus className="w-3.5 h-3.5" /> Add New Level</Button>
                            </div>
                            <div className="flex gap-2 pt-4">
                                <Button className="flex-1" onClick={handleSaveLevels}>Commit Changes</Button>
                                <Button variant="outline" className="flex-1" onClick={() => setLevelsDialogOpen(false)}>Cancel</Button>
                            </div>
                        </DialogContent>
                    </Dialog>
                </div>
                <div className="space-y-3">
                    {levels.map(l => (
                        <div key={l.id} className="flex items-center gap-3">
                            <div className="w-3 h-3 rounded-full" style={{ backgroundColor: l.color }} />
                            <span className="text-sm font-bold w-24" style={{ color: l.color }}>{l.levelName}</span>
                            <div className="flex-1 h-1.5 rounded-full bg-muted overflow-hidden">
                                <div className="h-full opacity-60" style={{ width: `${l.max - l.min + 1}%`, backgroundColor: l.color }} />
                            </div>
                            <span className="text-[10px] text-muted-foreground w-16 text-right font-mono">{l.min}-{l.max}%</span>
                        </div>
                    ))}
                </div>
            </Card>

            <Card className="p-6 max-w-xl">
                 <h3 className="text-lg font-bold font-serif mb-4 flex items-center gap-2"><Settings2 className="w-4 h-4" /> Class Core Settings</h3>
                 <div className="space-y-4">
                    <div className="space-y-1.5">
                        <Label>Class Title</Label>
                        <Input defaultValue={cls.name} />
                    </div>
                    <div className="space-y-1.5">
                        <Label>Class Description</Label>
                        <Textarea defaultValue={cls.description} className="min-h-[100px]" />
                    </div>
                    <div className="flex gap-3 pt-2">
                        <Button className="flex-1 h-10 font-bold" onClick={() => toast.success("Settings updated")}>Save General Settings</Button>
                        <Button variant="destructive" className="h-10 font-bold px-8">Delete Class</Button>
                    </div>
                 </div>
            </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
