import { useState, useEffect } from "react";
import { SeverityBadge } from "@/components/SeverityBadge";
import { Card } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { useAuth } from "@/lib/auth-context";
import classService from "@/services/class.service";
import { Submission } from "@/types/class";
import { Loader2, FileText, ChevronRight, History } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function HistoryPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [history, setHistory] = useState<Submission[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      if (!user) return;
      try {
        const resp = await classService.getMySubmissions(user.id);
        // Sort by date descending
        const sorted = (resp as Submission[]).sort((a, b) => 
            new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime()
        );
        setHistory(sorted);
      } catch (error) {
        console.error("Failed to fetch history", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchHistory();
  }, [user]);

  const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
    if (percentage <= 20) return "safe";
    if (percentage <= 40) return "low";
    if (percentage <= 60) return "medium";
    return "high";
  };

  if (isLoading) {
    return (
        <div className="flex flex-col items-center justify-center min-h-[400px]">
            <Loader2 className="w-10 h-10 animate-spin text-primary mb-4" />
            <p className="text-muted-foreground animate-pulse">Loading your history...</p>
        </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="flex items-center gap-3 mb-8">
          <div className="p-3 rounded-2xl bg-primary/10 text-primary shadow-sm border border-primary/20">
              <History className="w-6 h-6" />
          </div>
          <div>
              <h1 className="text-3xl font-serif font-bold">Check History</h1>
              <p className="text-muted-foreground text-sm tracking-wide uppercase font-bold mt-1">Your past plagiarism checks</p>
          </div>
      </div>

      {history.length > 0 ? (
          <Card className="shadow-2xl border-border/50 overflow-hidden rounded-2xl bg-white/40 backdrop-blur-sm animate-in fade-in slide-in-from-bottom-4">
            <Table>
              <TableHeader className="bg-muted/50">
                <TableRow className="border-b border-border/50">
                  <TableHead className="py-4 px-6 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Document / Assignment</TableHead>
                  <TableHead className="font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Date</TableHead>
                  <TableHead className="font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Plagiarism</TableHead>
                  <TableHead className="font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Status</TableHead>
                  <TableHead className="text-right px-6 font-bold uppercase text-[10px] tracking-widest text-muted-foreground">Action</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {history.map((item) => (
                  <TableRow 
                    key={item.id} 
                    className="hover:bg-primary/5 transition-colors cursor-pointer border-b border-border/50"
                    onClick={() => navigate(`/dashboard/submissions/${item.id}`)}
                  >
                    <TableCell className="py-4 px-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-primary/5 flex items-center justify-center text-primary group-hover:scale-110 transition-transform">
                                <FileText className="w-5 h-5" />
                            </div>
                            <div>
                                <p className="font-bold text-sm tracking-tight">{item.assignmentTitle || "Quick Check"}</p>
                                <p className="text-[10px] text-muted-foreground font-mono mt-0.5 uppercase">ID: SH-{item.id.toString().padStart(6, '0')}</p>
                            </div>
                        </div>
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground whitespace-nowrap">
                        {new Date(item.submittedAt).toLocaleDateString()}
                        <span className="block text-[10px] opacity-60">{new Date(item.submittedAt).toLocaleTimeString()}</span>
                    </TableCell>
                    <TableCell>
                        <span className="text-lg font-black tracking-tighter" style={{ color: `hsl(var(--severity-${getSeverity(item.plagiarismPercent)}))` }}>
                            {item.plagiarismPercent}%
                        </span>
                    </TableCell>
                    <TableCell>
                        <SeverityBadge severity={getSeverity(item.plagiarismPercent)} />
                    </TableCell>
                    <TableCell className="text-right px-6">
                        <Button variant="ghost" size="icon" className="h-8 w-8 hover:bg-primary/10 text-primary transition-all">
                            <ChevronRight className="w-4 h-4" />
                        </Button>
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
            </Table>
          </Card>
      ) : (
          <Card className="p-20 text-center border-dashed border-2 bg-muted/20 rounded-3xl animate-in zoom-in-95 duration-500">
              <div className="w-20 h-20 rounded-full bg-muted flex items-center justify-center mx-auto mb-6 shadow-inner">
                  <History className="w-10 h-10 text-muted-foreground/30" />
              </div>
              <h3 className="text-xl font-bold mb-2">No history yet</h3>
              <p className="text-muted-foreground max-w-xs mx-auto mb-8 font-serif italic text-sm">
                  Your plagiarism check results will appear here once you perform an analysis.
              </p>
              <Button onClick={() => navigate("/dashboard")} className="shadow-lg shadow-primary/20 hover:scale-105 transition-transform px-8 py-6 rounded-2xl h-auto font-bold uppercase tracking-widest text-xs">
                    Start Checking Now
              </Button>
          </Card>
      )}
    </div>
  );
}
