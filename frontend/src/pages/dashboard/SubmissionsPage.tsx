import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { SeverityBadge } from "@/components/SeverityBadge";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Search, ExternalLink, FileText, Calendar, User } from "lucide-react";
import classService from "@/services/class.service";
import { Submission } from "@/types/class";
import { toast } from "sonner";

export default function SubmissionsPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [classFilter, setClassFilter] = useState("all");
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [classes, setClasses] = useState<{id: number, name: string}[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [subsData, classesData] = await Promise.all([
          classService.getAllSubmissions(),
          classService.getClasses()
        ]);
        setSubmissions(subsData);
        setClasses(classesData.results || classesData);
      } catch (error) {
        toast.error("Failed to load submission data");
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  const filtered = submissions.filter((sub) => {
    const matchesSearch =
      sub.studentName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      sub.assignmentTitle.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesClass = classFilter === "all" || sub.classId?.toString() === classFilter;
    return matchesSearch && matchesClass;
  });

  const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
    if (percentage <= 20) return "safe";
    if (percentage <= 40) return "low";
    if (percentage <= 60) return "medium";
    return "high";
  };

  return (
    <div className="max-w-7xl mx-auto space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-4xl font-serif font-black tracking-tight flex items-center gap-3">
            <div className="bg-primary/10 p-2 rounded-xl border border-primary/20 shadow-sm">
              <FileText className="w-8 h-8 text-primary" />
            </div>
            Academic Submissions
          </h1>
          <p className="text-muted-foreground mt-2 font-medium">Review and analyze student work across all departments</p>
        </div>
        
        <div className="flex bg-muted/30 p-1.5 rounded-xl border items-center gap-2">
           <div className="px-3 py-1.5 bg-card rounded-lg shadow-sm border text-xs font-bold text-primary flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
              {submissions.length} Total Submissions
           </div>
        </div>
      </div>

      {/* Filters Area */}
      <Card className="p-4 shadow-sm border-muted/60 bg-muted/5">
        <div className="flex flex-col lg:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              className="pl-10 h-11 bg-card border-muted-foreground/20 focus-visible:ring-primary/30 rounded-xl"
              placeholder="Filter by student name, assignment title..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <div className="flex flex-col sm:flex-row gap-3">
            <Select value={classFilter} onValueChange={setClassFilter}>
              <SelectTrigger className="w-full sm:w-64 h-11 bg-card border-muted-foreground/20 rounded-xl">
                <SelectValue placeholder="All Classes" />
              </SelectTrigger>
              <SelectContent className="rounded-xl">
                <SelectItem value="all">All Classes</SelectItem>
                {classes.map((cls) => (
                  <SelectItem key={cls.id} value={cls.id.toString()}>{cls.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </Card>

      <Card className="shadow-2xl overflow-hidden border-none rounded-2xl ring-1 ring-muted">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/50 hover:bg-muted/50">
              <TableHead className="py-5 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Student & Identity</TableHead>
              <TableHead className="py-5 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Assignment & Course</TableHead>
              <TableHead className="py-5 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Submission Date</TableHead>
              <TableHead className="py-5 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Integrity Score</TableHead>
              <TableHead className="py-5 font-bold uppercase text-[10px] tracking-widest text-muted-foreground text-right">Action</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
               Array.from({length: 5}).map((_, i) => (
                 <TableRow key={i} className="animate-pulse">
                   <TableCell colSpan={5} className="py-12"><div className="h-4 bg-muted rounded w-full" /></TableCell>
                 </TableRow>
               ))
            ) : filtered.map((sub) => {
              const severity = getSeverity(sub.plagiarismPercent);
              return (
                <TableRow key={sub.id} className="group hover:bg-muted/10 transition-colors">
                  <TableCell className="py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-muted/40 flex items-center justify-center border border-muted-foreground/10 group-hover:border-primary/20 transition-all">
                        <User className="w-5 h-5 text-muted-foreground/60" />
                      </div>
                      <div>
                        <p className="font-bold text-sm tracking-tight">{sub.studentName}</p>
                        <p className="text-[10px] text-muted-foreground font-mono tracking-tighter uppercase">{sub.studentEmail}</p>
                      </div>
                    </div>
                  </TableCell>
                  <TableCell className="py-4">
                    <div className="space-y-0.5">
                      <p className="font-bold text-sm leading-tight">{sub.assignmentTitle}</p>
                      <p className="text-[10px] text-muted-foreground font-medium flex items-center gap-1.5 opacity-70 group-hover:opacity-100 transition-opacity">
                        <Calendar className="w-3 h-3" /> Class ID: {sub.classId || "N/A"}
                      </p>
                    </div>
                  </TableCell>
                  <TableCell className="py-4 text-xs font-medium text-muted-foreground">
                    {new Date(sub.submittedAt).toLocaleDateString(undefined, {
                      year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                    })}
                  </TableCell>
                  <TableCell className="py-4">
                    <div className="flex items-center gap-3">
                       <span className={`text-base font-black tabular-nums ${
                          sub.plagiarismPercent > 40 ? "text-destructive" : sub.plagiarismPercent > 20 ? "text-orange-500" : "text-green-500"
                       }`}>
                          {sub.plagiarismPercent.toFixed(1)}%
                       </span>
                       <SeverityBadge severity={severity} />
                    </div>
                  </TableCell>
                  <TableCell className="py-4 text-right">
                    <Button 
                      variant="ghost" 
                      size="sm" 
                      className="gap-2 h-9 rounded-xl opacity-0 hover:opacity-100 transition-all group-hover:opacity-100 group-hover:bg-primary/5 group-hover:text-primary"
                      onClick={() => navigate(`/dashboard/submissions/${sub.id}`)}
                    >
                      Analyze Report <ExternalLink className="w-3.5 h-3.5" />
                    </Button>
                  </TableCell>
                </TableRow>
              );
            })}
            {!isLoading && filtered.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} className="py-20 text-center">
                   <div className="flex flex-col items-center gap-3 opacity-20 grayscale">
                    <FileText className="w-16 h-16" />
                    <p className="text-xl font-serif font-bold">No submissions found matching your criteria</p>
                    <p className="text-sm">Try adjusting your filters or search query</p>
                   </div>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}
