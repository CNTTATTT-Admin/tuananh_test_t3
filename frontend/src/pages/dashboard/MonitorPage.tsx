import { useState, useEffect } from "react";
import { toast } from "sonner";
import { SeverityBadge } from "@/components/SeverityBadge";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Search, Activity, Loader2 } from "lucide-react";
import classService from "@/services/class.service";
import { Submission } from "@/types/class";

export default function MonitorPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [severityFilter, setSeverityFilter] = useState("all");
  const [submissions, setSubmissions] = useState<Submission[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchSubmissions = async (isBackground = false) => {
    try {
      if (!isBackground) setIsLoading(true);
      const data = await classService.getAllSubmissions();
      // Optionally sort by most recent if not already sorted by backend
      const sorted = Array.isArray(data)
        ? data.sort((a, b) => new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime())
        : [];
      setSubmissions(sorted);
    } catch (error) {
      if (!isBackground) toast.error("Failed to load monitor data");
    } finally {
      if (!isBackground) setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSubmissions();
    const interval = setInterval(() => {
      fetchSubmissions(true);
    }, 10000);
    return () => clearInterval(interval);
  }, []);

  const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
    if (percentage <= 20) return "safe";
    if (percentage <= 40) return "low";
    if (percentage <= 60) return "medium";
    return "high";
  };

  const filtered = submissions.filter((item) => {
    const severity = getSeverity(item.plagiarismPercent);
    const mappedSeverity = severity === "safe" ? "safe" : severity === "low" ? "suspicious" : severity === "medium" ? "high" : "severe";

    const matchesSearch =
      (item.assignmentTitle || "").toLowerCase().includes(searchQuery.toLowerCase()) ||
      (item.studentName || "").toLowerCase().includes(searchQuery.toLowerCase());
    
    // Simplistic handling of severity filter mapping
    const matchesSeverity = 
        severityFilter === "all" ? true :
        severityFilter === "safe" && severity === "safe" ? true :
        severityFilter === "suspicious" && severity === "low" ? true :
        severityFilter === "high" && severity === "medium" ? true :
        severityFilter === "severe" && severity === "high" ? true : false;

    return matchesSearch && matchesSeverity;
  });

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex items-center gap-3">
        <Activity className="w-8 h-8 text-primary" />
        <div>
           <h1 className="text-3xl font-serif font-bold tracking-tight">System Monitor</h1>
           <p className="text-muted-foreground mt-1">Real-time surveillance of plagiarism checks</p>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            className="pl-10 h-11 bg-white"
            placeholder="Search by student or assignment..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
        <Select value={severityFilter} onValueChange={setSeverityFilter}>
          <SelectTrigger className="w-full sm:w-48 h-11 bg-white">
            <SelectValue placeholder="Filter severity" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Severities</SelectItem>
            <SelectItem value="safe">Safe (0-20%)</SelectItem>
            <SelectItem value="suspicious">Suspicious (21-40%)</SelectItem>
            <SelectItem value="high">High Risk (41-60%)</SelectItem>
            <SelectItem value="severe">Severe (61-100%)</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Card className="shadow-lg overflow-hidden border-none bg-white/50 backdrop-blur-sm">
        <div className="p-4 border-b border-border bg-slate-50 flex justify-between items-center">
          <p className="text-sm font-medium text-slate-600">
            System Live Checks
          </p>
          <span className="text-xs font-bold text-primary bg-primary/10 px-2.5 py-1 rounded-full">
            {filtered.length} active records
          </span>
        </div>
        <Table>
          <TableHeader className="bg-slate-50">
            <TableRow>
              <TableHead className="font-bold text-slate-700">Student Identity</TableHead>
              <TableHead className="font-bold text-slate-700">Assignment Title</TableHead>
              <TableHead className="font-bold text-slate-700">Timestamp</TableHead>
              <TableHead className="font-bold text-slate-700 text-center">Plagiarism %</TableHead>
              <TableHead className="font-bold text-slate-700 w-32 text-center">Severity</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
               <TableRow>
                 <TableCell colSpan={5} className="py-20">
                   <div className="flex flex-col items-center justify-center gap-3">
                     <Loader2 className="w-8 h-8 animate-spin text-primary" />
                     <p className="text-slate-500 font-medium">Monitoring system traffic...</p>
                   </div>
                 </TableCell>
               </TableRow>
            ) : filtered.length === 0 ? (
               <TableRow>
                <TableCell colSpan={5} className="text-center text-slate-500 py-16">
                  No monitoring data matches your current filters.
                </TableCell>
              </TableRow>
            ) : filtered.map((item) => {
              const severityRaw = getSeverity(item.plagiarismPercent);
              return (
                <TableRow key={item.id} className="hover:bg-slate-50/80 transition-colors">
                  <TableCell>
                    <div>
                      <p className="font-bold text-sm text-slate-900">{item.studentName || "Anonymous"}</p>
                      <p className="text-[11px] font-mono text-slate-500 mt-0.5">{item.studentEmail}</p>
                    </div>
                  </TableCell>
                  <TableCell>
                    <span className="font-medium text-slate-700">{item.assignmentTitle || "System Check"}</span>
                  </TableCell>
                  <TableCell>
                    <p className="text-xs font-medium text-slate-600">
                        {new Date(item.submittedAt).toLocaleDateString()}
                    </p>
                    <p className="text-[10px] text-slate-400">
                        {new Date(item.submittedAt).toLocaleTimeString()}
                    </p>
                  </TableCell>
                  <TableCell className="text-center">
                    <span className={`text-lg font-black tracking-tighter ${
                        item.plagiarismPercent > 40 ? "text-rose-600" : item.plagiarismPercent > 20 ? "text-amber-500" : "text-emerald-500"
                    }`}>
                      {item.plagiarismPercent.toFixed(1)}%
                    </span>
                  </TableCell>
                  <TableCell className="text-center">
                    <SeverityBadge severity={severityRaw} />
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}
