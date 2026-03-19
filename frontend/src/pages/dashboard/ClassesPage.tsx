import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "@/lib/auth-context";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogClose } from "@/components/ui/dialog";
import { BookOpen, Users, Plus, Copy, ArrowRight, Loader2 } from "lucide-react";
import { toast } from "sonner";
import classService from "@/services/class.service";
import { ClassRoom } from "@/types/class";

export default function ClassesPage() {
  const { user } = useAuth();
  const isLecturer = user?.role === "lecturer";
  const [classes, setClasses] = useState<ClassRoom[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [joinCode, setJoinCode] = useState("");
  const [newClassName, setNewClassName] = useState("");
  const [newClassDesc, setNewClassDesc] = useState("");

  const fetchClasses = async () => {
    setIsLoading(true);
    try {
      const response = await classService.getClasses();
      // Handle potential pagination wrapper
      const data = response.results || response;
      setClasses(Array.isArray(data) ? data : []);
    } catch (error: any) {
      toast.error(error.message || "Failed to fetch classes");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchClasses();
  }, []);

  const handleCreateClass = async () => {
    if (!newClassName.trim()) return;
    try {
      await classService.createClass(newClassName, newClassDesc);
      toast.success("Class created successfully!");
      setNewClassName("");
      setNewClassDesc("");
      fetchClasses();
    } catch (error: any) {
      toast.error(error.message || "Failed to create class");
    }
  };

  const handleJoinClass = async () => {
    if (!joinCode.trim()) return;
    try {
      const message = await classService.joinClass(joinCode);
      toast.success(message || "Yêu cầu đã được gửi!");
      setJoinCode("");
      fetchClasses();
    } catch (error: any) {
      toast.error(error.message || "Failed to join class");
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-serif font-bold">
          {isLecturer ? "Manage Classes" : "My Classes"}
        </h1>
        {isLecturer ? (
          <Dialog>
            <DialogTrigger asChild>
              <Button className="gap-1.5"><Plus className="w-4 h-4" />Create Class</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle className="font-serif">Create New Class</DialogTitle>
              </DialogHeader>
              <div className="space-y-4 pt-2">
                <div className="space-y-1.5">
                  <Label>Class Name</Label>
                  <Input
                    value={newClassName}
                    onChange={(e) => setNewClassName(e.target.value)}
                    placeholder="e.g. Introduction to CS"
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>Description</Label>
                  <Input
                    value={newClassDesc}
                    onChange={(e) => setNewClassDesc(e.target.value)}
                    placeholder="Brief description of the class"
                  />
                </div>
                <DialogClose asChild>
                  <Button className="w-full" onClick={handleCreateClass}>
                    Create
                  </Button>
                </DialogClose>
              </div>
            </DialogContent>
          </Dialog>
        ) : (
          <Dialog>
            <DialogTrigger asChild>
              <Button className="gap-1.5"><Plus className="w-4 h-4" />Join Class</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle className="font-serif">Join a Class</DialogTitle>
              </DialogHeader>
              <div className="space-y-4 pt-2">
                <div className="space-y-1.5">
                  <Label>Class Code</Label>
                  <Input value={joinCode} onChange={(e) => setJoinCode(e.target.value)} placeholder="e.g. CS101-A" />
                </div>
                <DialogClose asChild>
                  <Button className="w-full" onClick={handleJoinClass}>Join</Button>
                </DialogClose>
              </div>
            </DialogContent>
          </Dialog>
        )}
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <Loader2 className="w-10 h-10 animate-spin text-primary" />
        </div>
      ) : classes.length > 0 ? (
        <div className="grid gap-4 md:grid-cols-2">
          {classes.map((cls) => (
            <Link key={cls.id} to={`/dashboard/classes/${cls.id}`} className="group">
              <Card className="p-5 shadow-md hover:shadow-lg transition-shadow h-full">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h3 className="font-serif font-bold text-lg group-hover:text-primary transition-colors">{cls.name}</h3>
                    <p className="text-sm text-muted-foreground mt-1">{cls.lecturer?.name || "No Lecturer"}</p>
                  </div>
                  <BookOpen className="w-5 h-5 text-muted-foreground shrink-0" />
                </div>
                {cls.description && (
                  <p className="text-xs text-muted-foreground mt-2 line-clamp-2">{cls.description}</p>
                )}
                <div className="flex items-center justify-between mt-4">
                  <div className="flex items-center gap-4 text-sm">
                    <button
                      className="flex items-center gap-1 text-primary font-medium hover:underline"
                      onClick={(e) => {
                        e.preventDefault();
                        navigator.clipboard.writeText(cls.classCode);
                        toast.success("Code copied!");
                      }}
                    >
                      <Copy className="w-3.5 h-3.5" /> {cls.classCode}
                    </button>
                  </div>
                  <ArrowRight className="w-4 h-4 text-muted-foreground group-hover:text-primary transition-colors" />
                </div>
              </Card>
            </Link>
          ))}
        </div>
      ) : (
        <Card className="p-12 text-center">
          <BookOpen className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
          <h2 className="text-xl font-semibold mb-2">No classes found</h2>
          <p className="text-muted-foreground">
            {isLecturer ? "Create your first class to get started!" : "Join a class using a code from your lecturer."}
          </p>
        </Card>
      )}
    </div>
  );
}
