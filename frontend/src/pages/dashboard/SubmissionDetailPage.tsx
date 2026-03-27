import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  FileText,
  User,
  Calendar,
  Gauge,
  AlertTriangle,
  CheckCircle2,
  ExternalLink,
  ChevronRight,
  ChevronLeft,
  Printer,
  Share2,
  Trash2
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { PlagiarismGauge } from "@/components/PlagiarismGauge";
import { SeverityBadge } from "@/components/SeverityBadge";
import classService from "@/services/class.service";
import { Submission } from "@/types/class";
import { toast } from "sonner";
import { motion } from "framer-motion";

export default function SubmissionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [submission, setSubmission] = useState<Submission | null>(null);
  const [matches, setMatches] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const ITEMS_PER_PAGE = 5;

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (!id || isNaN(parseInt(id))) return;
        const subIdNum = parseInt(id);
        const [subData, matchesData] = await Promise.all([
          classService.getSubmissionById(subIdNum),
          classService.getMatchesBySubmissionId(subIdNum)
        ]);
        
        if (subData) {
          setSubmission(subData);
        }
        if (Array.isArray(matchesData)) {
          setMatches(matchesData);
        }
      } catch (error) {
        toast.error("Failed to load submission report");
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
    if (percentage <= 20) return "safe";
    if (percentage <= 40) return "low";
    if (percentage <= 60) return "medium";
    return "high";
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="flex flex-col items-center gap-4">
          <div className="w-16 h-16 rounded-full border-4 border-primary/20 border-t-primary animate-spin" />
          <p className="text-muted-foreground font-medium animate-pulse">Generating Plagiarism Report...</p>
        </div>
      </div>
    );
  }

  if (!submission) {
    return (
      <Card className="max-w-md mx-auto p-12 text-center mt-20">
        <AlertTriangle className="w-12 h-12 text-destructive mx-auto mb-4" />
        <h2 className="text-2xl font-serif font-bold">Report Not Found</h2>
        <p className="text-muted-foreground mt-2">The requested submission report could not be located or has been archived.</p>
        <Button className="mt-6" onClick={() => navigate(-1)}>Go Back</Button>
      </Card>
    );
  }

  const severity = getSeverity(submission.plagiarismPercent);

  return (
    <div className="max-w-7xl mx-auto pb-20 space-y-8 animate-in fade-in duration-700">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-2">
        <div className="space-y-1">
          <Button variant="ghost" size="sm" className="gap-2 mb-2 -ml-2 text-muted-foreground hover:text-primary transition-colors" onClick={() => navigate(-1)}>
            <ArrowLeft className="w-4 h-4" /> Back to List
          </Button>
          <div className="flex items-center gap-3">
            <h1 className="text-4xl font-serif font-black tracking-tight">Report #SUB-{submission.id || "ID"}</h1>
            <Badge variant={severity === "safe" ? "secondary" : "destructive"} className="uppercase tracking-widest text-[10px] h-6 px-3">
              {severity} Risk
            </Badge>
          </div>
          <p className="text-muted-foreground font-medium flex items-center gap-2">
            Analysis session for <span className="text-foreground font-bold">{submission.assignmentTitle}</span>
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline" className="gap-2 shadow-sm"><Printer className="w-4 h-4" /> Export PDF</Button>
          <Button variant="outline" size="icon" className="shadow-sm"><Share2 className="w-4 h-4" /></Button>
          <Button variant="destructive" size="icon" className="shadow-sm bg-red-50 text-red-600 hover:bg-red-100 border-red-200"><Trash2 className="w-4 h-4" /></Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Risk Profile (Left) */}
        <div className="lg:col-span-4 space-y-6 lg:sticky lg:top-8">
          <Card className="relative overflow-hidden group shadow-xl border-none">
            <div className="absolute top-0 left-0 w-full h-1.5 bg-muted">
              <motion.div
                className={`h-full ${severity === "safe" ? "bg-green-500" : severity === "low" ? "bg-orange-500" : severity === "medium" ? "bg-orange-600" : "bg-red-600"}`}
                initial={{ width: 0 }}
                animate={{ width: `${submission.plagiarismPercent || 0}%` }}
                transition={{ duration: 1.5, ease: "easeOut" }}
              />
            </div>
            <div className="p-8 flex flex-col items-center text-center">
              <h3 className="text-xs font-black uppercase tracking-[0.2em] text-muted-foreground mb-6">Plagiarism Index</h3>
              <PlagiarismGauge percentage={submission.plagiarismPercent || 0} severity={severity} />
              <div className="mt-8 space-y-1">
                <p className="text-3xl font-black">{(submission.plagiarismPercent || 0).toFixed(2)}%</p>
                <p className="text-sm text-muted-foreground font-medium uppercase tracking-widest">Similarity Detected</p>
              </div>
            </div>
          </Card>

          <Card className="p-6 shadow-lg">
            <h4 className="font-bold text-sm uppercase tracking-wider mb-5 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-primary" /> Submission Metadata
            </h4>
            <div className="space-y-4">
              <div className="flex items-start gap-3 p-3 rounded-xl bg-muted/30">
                <div className="bg-primary/10 p-2 rounded-lg"><User className="w-4 h-4 text-primary" /></div>
                <div>
                  <p className="text-[10px] font-bold text-muted-foreground uppercase opacity-70">Author</p>
                  <p className="font-bold text-sm tracking-tight">{submission.studentName}</p>
                  <p className="text-[10px] text-muted-foreground font-mono">{submission.studentEmail}</p>
                </div>
              </div>
              <div className="flex items-start gap-3 p-3 rounded-xl bg-muted/30">
                <div className="bg-primary/10 p-2 rounded-lg"><Calendar className="w-4 h-4 text-primary" /></div>
                <div>
                  <p className="text-[10px] font-bold text-muted-foreground uppercase opacity-70">Timeline</p>
                  <p className="font-bold text-sm tracking-tight">
                    {new Date(submission.submittedAt).toLocaleDateString(undefined, {
                      year: 'numeric', month: 'long', day: 'numeric',
                      hour: '2-digit', minute: '2-digit'
                    })}
                  </p>
                </div>
              </div>
              {submission.fileUrl && (
                <div className="flex items-start gap-3 p-3 rounded-xl bg-muted/30">
                  <div className="bg-primary/10 p-2 rounded-lg"><FileText className="w-4 h-4 text-primary" /></div>
                  <div>
                    <p className="text-[10px] font-bold text-muted-foreground uppercase opacity-70">Archive URL</p>
                    <p className="font-bold text-sm tracking-tight truncate max-w-[180px]">{submission.fileUrl}</p>
                  </div>
                </div>
              )}
            </div>
          </Card>
        </div>

        {/* Content & matches (Right) */}
        <div className="lg:col-span-8 space-y-6">
          <Tabs defaultValue="content" className="w-full">
            <TabsList className="bg-muted w-full justify-start h-12 p-1 gap-1 rounded-xl">
              <TabsTrigger value="content" className="gap-2 px-6 rounded-lg data-[state=active]:bg-card data-[state=active]:shadow-sm">
                <FileText className="w-4 h-4" /> Full Submission
              </TabsTrigger>
              <TabsTrigger value="matches" className="gap-2 px-6 rounded-lg data-[state=active]:bg-card data-[state=active]:shadow-sm">
                <AlertTriangle className="w-4 h-4" /> Matched Sources ({matches.length})
              </TabsTrigger>
            </TabsList>

            <TabsContent value="content" className="pt-4 animate-in fade-in slide-in-from-top-4 duration-500">
              <Card className="p-8 shadow-xl max-h-[800px] overflow-y-auto leading-relaxed border-none rounded-2xl ring-1 ring-muted">
                <div className="prose prose-slate max-w-none">
                  <h2 className="text-xl font-serif font-black mb-6 pb-4 border-b">Document Content</h2>
                  <div className="whitespace-pre-wrap text-sm text-foreground/80 font-medium">
                    {submission.content || "No textual content extracted from this submission."}
                  </div>
                </div>
              </Card>
            </TabsContent>

            <TabsContent value="matches" className="pt-4 animate-in fade-in slide-in-from-top-4 duration-500">
              <div className="space-y-4">
                {matches.length > 0 ? (
                  <>
                    <div className="space-y-4">
                      {matches.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE).map((match, i) => (
                        <Card key={i} className="group overflow-hidden border-none shadow-md hover:shadow-xl transition-all duration-300 rounded-2xl ring-1 ring-muted">
                          <div className="p-5 flex flex-col md:flex-row items-start md:items-center gap-4">
                            <div className="shrink-0 w-12 h-12 bg-muted/30 rounded-xl flex items-center justify-center font-bold text-lg text-primary/40 group-hover:text-primary transition-colors">
                              {((currentPage - 1) * ITEMS_PER_PAGE) + i + 1}
                            </div>
                            <div className="flex-1 min-w-0">
                              <div className="flex items-center gap-2 mb-1">
                                <h5 className="font-black text-sm tracking-tight">{match.document?.title || "External Source"}</h5>
                                <Badge variant="outline" className="text-[9px] h-4 tracking-tighter uppercase">{match.document?.type || "ARTICLE"}</Badge>
                              </div>
                              <p className="text-xs text-muted-foreground/70 font-medium italic line-clamp-1">"{match.matchedText}"</p>
                            </div>
                            <div className="md:text-right shrink-0 flex items-center gap-4">
                              <div className="flex flex-col items-end gap-1">
                                <span className="text-xl font-black tabular-nums">{(match.similarityPercent || 0).toFixed(2)}%</span>
                                <div className="h-1 w-20 bg-muted rounded-full overflow-hidden">
                                  <div className="h-full bg-primary" style={{ width: `${match.similarityPercent || 0}%` }} />
                                </div>
                              </div>
                              <Button size="icon" variant="ghost" className="h-10 w-10 text-primary hover:bg-primary/10 rounded-xl"><ChevronRight className="w-5 h-5" /></Button>
                            </div>
                          </div>
                        </Card>
                      ))}
                    </div>

                    {/* Pagination Controls */}
                    {matches.length > ITEMS_PER_PAGE && (
                      <div className="flex items-center justify-between mt-6 pt-6 border-t font-sans">
                        <p className="text-xs text-muted-foreground font-medium">
                          Showing {((currentPage - 1) * ITEMS_PER_PAGE) + 1} to {Math.min(currentPage * ITEMS_PER_PAGE, matches.length)} of {matches.length} matches
                        </p>
                        <div className="flex items-center gap-1">
                          <Button 
                            variant="outline" 
                            size="icon" 
                            className="h-8 w-8" 
                            onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                            disabled={currentPage === 1}
                          >
                            <ChevronLeft className="w-4 h-4" />
                          </Button>
                          <div className="flex items-center gap-1 px-2">
                             {Array.from({ length: Math.ceil(matches.length / ITEMS_PER_PAGE) }).map((_, i) => {
                                const pageNum = i + 1;
                                if (pageNum === 1 || pageNum === Math.ceil(matches.length / ITEMS_PER_PAGE) || (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)) {
                                    return (
                                        <Button 
                                            key={pageNum}
                                            variant={currentPage === pageNum ? "default" : "ghost"}
                                            size="sm"
                                            className="h-8 w-8 p-0"
                                            onClick={() => setCurrentPage(pageNum)}
                                        >
                                            {pageNum}
                                        </Button>
                                    );
                                }
                                if (pageNum === 2 || pageNum === Math.ceil(matches.length / ITEMS_PER_PAGE) - 1) {
                                    return <span key={pageNum} className="text-muted-foreground text-xs px-1">...</span>;
                                }
                                return null;
                             })}
                          </div>
                          <Button 
                            variant="outline" 
                            size="icon" 
                            className="h-8 w-8" 
                            onClick={() => setCurrentPage(prev => Math.min(Math.ceil(matches.length / ITEMS_PER_PAGE), prev + 1))}
                            disabled={currentPage === Math.ceil(matches.length / ITEMS_PER_PAGE)}
                          >
                            <ChevronRight className="w-4 h-4" />
                          </Button>
                        </div>
                      </div>
                    )}
                  </>
                ) : (
                  <Card className="p-20 text-center border-dashed border-2 ring-0 bg-transparent opacity-40">
                    <CheckCircle2 className="w-16 h-16 mx-auto mb-4 text-primary" />
                    <h3 className="text-xl font-serif font-bold">Pristine Content</h3>
                    <p className="text-sm">No cross-document similarities found in our database.</p>
                  </Card>
                )}
              </div>
            </TabsContent>
          </Tabs>

          {/* Insight Card */}
          <Card className="p-6 bg-primary/5 border-primary/20 shadow-none rounded-2xl overflow-hidden relative">
            <div className="relative z-10 flex items-center gap-4">
              <div className="bg-primary/10 p-3 rounded-2xl"><Gauge className="w-6 h-6 text-primary" /></div>
              <div>
                <h4 className="font-black text-sm text-primary uppercase tracking-widest">Analysis Insight</h4>
                <p className="text-sm text-primary/80 font-medium mt-1 leading-snug">
                  {submission.plagiarismPercent > 40
                    ? "High similarity detected. Multiple segments appear to follow patterns from existing scholarly work. Recommended review of sourcing."
                    : submission.plagiarismPercent > 20
                      ? "Partial matching found. This level is typical for widely-cited concepts, but please verify paraphrasing quality."
                      : "Document demonstrates strong originality. Matches found are within standard academic citation ranges."
                  }
                </p>
              </div>
            </div>
            <div className="absolute top-0 right-0 p-4 opacity-5"><FileText className="w-32 h-32" /></div>
          </Card>
        </div>
      </div>
    </div>
  );
}
