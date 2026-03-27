import { useState, useCallback, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { 
    FileText, 
    Upload, 
    AtSign, 
    AlertTriangle, 
    CheckCircle2, 
    Clock, 
    Settings2, 
    Search, 
    FileUp, 
    FileDown, 
    Info, 
    Loader2, 
    ChevronRight,
    ChevronLeft,
    Send,
    X
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Card } from "@/components/ui/card";
import { PlagiarismGauge } from "./PlagiarismGauge";
import { HighlightedText } from "./HighlightedText";
import { SeverityBadge } from "./SeverityBadge";
import { analyzePlagiarism, extractTextFromFile, PlagiarismResult, PlagiarismMatch } from "@/lib/plagiarism-engine";
import classService from "@/services/class.service";
import { useAuth } from "@/lib/auth-context";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";

interface PlagiarismLevel {
  id: string;
  label: string;
  minPercent: number;
  maxPercent: number;
  color: string;
}

interface PlagiarismCheckerProps {
  mode?: "check" | "submit";
  assignmentTitle?: string;
  onSubmit?: (text: string, result: PlagiarismResult, file: File | null) => void;
  levels?: PlagiarismLevel[];
}

export function PlagiarismChecker({ mode = "check", assignmentTitle, onSubmit, levels }: PlagiarismCheckerProps) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [inputText, setInputText] = useState("");
  const [currentFile, setCurrentFile] = useState<File | null>(null);
  const [fileName, setFileName] = useState<string | null>(null);
    const [isAnalyzing, setIsAnalyzing] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const ITEMS_PER_PAGE = 5;
  const [result, setResult] = useState<PlagiarismResult | null>(null);

  useEffect(() => {
    setCurrentPage(1);
  }, [result]);
  const [activeTab, setActiveTab] = useState<"text" | "file">("text");
  const [analyzingStatus, setAnalyzingStatus] = useState("");

  const handleFileUpload = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const validTypes = [
      "text/plain",
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (!validTypes.includes(file.type) && !file.name.match(/\.(txt|pdf|docx)$/i)) {
      toast.error("Unsupported file type. Please upload a PDF, DOCX, or TXT file.");
      return;
    }

    setFileName(file.name);
    setCurrentFile(file);
    setInputText("File content will be analyzed after submission."); // Indicate that text will be processed server-side
  }, []);

  const handleCheck = useCallback(async () => {
    if (!inputText.trim() && !currentFile) return; // Ensure there's text or a file
    setIsAnalyzing(true);
    setResult(null);
    setAnalyzingStatus("Uploading content...");
    
    try {
      // 1. Submit to backend
      const submission = await classService.submitAssignment(
        null, // No assignmentId
        activeTab === "text" ? inputText : "", // Send inputText only if activeTab is text
        activeTab === "file" ? currentFile : null // Send currentFile only if activeTab is file
      );
      
      const subId = submission.id;
      setAnalyzingStatus("Processing fingerprints and comparing...");
      
      // 2. Poll for result (Async plagiarism check)
      let pollCount = 0;
      const maxPolls = 120; // 120 seconds max
      
      const poll = async () => {
          if (pollCount >= maxPolls) {
              throw new Error("Analysis timed out. Please check back later in history.");
          }
          
        const updatedSub = await classService.getSubmissionById(subId);
        console.log("Current analysis status:", updatedSub.status);
        
        if (updatedSub.status === "COMPLETED" || updatedSub.status === "FAILED") {
            const rawMatches = await classService.getMatchesBySubmissionId(subId);
            return { sub: updatedSub, matches: rawMatches || [] };
        }
        
        // If still processing, wait and repeat
        pollCount++;
        setAnalyzingStatus(`Analyzing... (${pollCount}s)`);
        await new Promise(r => setTimeout(r, 1000));
        return poll();
      };

      const { sub, matches } = await poll();
      
      // 3. Map backend matches to frontend PlagiarismMatch
      const contentToAnalyze = activeTab === "file" ? sub.content : (sub.content || inputText); 
      const mappedMatches: PlagiarismMatch[] = matches.map(m => {
          const contentToAnalyze = sub.content || inputText;
          const words = contentToAnalyze.split(/\s+/);
          let startChar = 0;
          for (let i = 0; i < m.startPosition; i++) {
              if (words[i]) startChar += words[i].length + 1;
          }
          let endChar = startChar;
          for (let i = m.startPosition; i < m.endPosition; i++) {
              if (words[i]) endChar += words[i].length + 1;
          }

          return {
              text: contentToAnalyze.substring(startChar, endChar),
              startIndex: startChar,
              endIndex: endChar,
              source: m.document?.title || "External Source",
              similarity: m.similarityPercent || 0,
          };
      });

      const getSeverity = (percentage: number): "safe" | "low" | "medium" | "high" => {
        if (percentage <= 20) return "safe";
        if (percentage <= 40) return "low";
        if (percentage <= 60) return "medium";
        return "high";
      };

      setResult({
          percentage: sub.plagiarismPercent,
          severity: getSeverity(sub.plagiarismPercent),
          matches: mappedMatches,
          originalText: contentToAnalyze || "",
          wordCount: (contentToAnalyze || "").split(/\s+/).filter(Boolean).length,
          checkDate: new Date().toISOString()
      });
      
      if (user) {
          toast.success("Analysis complete! Saved to your history.");
      } else {
          toast.success("Analysis complete!");
      }

    } catch (error: any) {
      const errorMsg = error.response?.data?.message || error.message || "Plagiarism check failed";
      toast.error(errorMsg);
      console.error("Full error object:", error);
      if (error.response) {
          console.error("Error response data:", error.response.data);
      }
    } finally {
      setIsAnalyzing(false);
      setAnalyzingStatus("");
    }
  }, [inputText, activeTab, currentFile, user]);

  const handleSubmit = useCallback(() => {
    if (result && onSubmit) {
      onSubmit(inputText, result, activeTab === "file" ? currentFile : null);
    }
  }, [inputText, result, onSubmit, activeTab, currentFile]);

  const handleClear = () => {
    setInputText("");
    setFileName(null);
    setCurrentFile(null);
    setResult(null);
  };

  const currentLevel = result && levels?.find(l => result.percentage >= l.minPercent && result.percentage <= l.maxPercent);

  return (
    <div className="space-y-6">
      {/* Input Section */}
      <Card className="p-6 shadow-lg border-border/50">
        {mode === "submit" && assignmentTitle && (
            <div className="mb-4 pb-4 border-b">
                <h4 className="text-sm font-medium text-muted-foreground uppercase tracking-wider mb-1">Submitting for:</h4>
                <p className="font-serif font-bold text-lg">{assignmentTitle}</p>
            </div>
        )}

        {/* Tab Switcher */}
        <div className="flex gap-1 p-1 bg-muted rounded-lg w-fit mb-5">
          <button
            onClick={() => {
              setActiveTab("text");
              setFileName(null);
              setCurrentFile(null);
            }}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-all ${
              activeTab === "text"
                ? "bg-card text-foreground shadow-sm"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            <FileText className="inline-block w-4 h-4 mr-1.5 -mt-0.5" />
            Paste Text
          </button>
          <button
            onClick={() => {
              setActiveTab("file");
              setInputText("");
            }}
            className={`px-4 py-2 rounded-md text-sm font-medium transition-all ${
              activeTab === "file"
                ? "bg-card text-foreground shadow-sm"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            <Upload className="inline-block w-4 h-4 mr-1.5 -mt-0.5" />
            Upload File
          </button>
        </div>

        <AnimatePresence mode="wait">
          {activeTab === "text" ? (
            <motion.div
              key="text"
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -8 }}
            >
              <Textarea
                placeholder="Paste or type your text here to check for plagiarism..."
                className="min-h-[200px] resize-y text-sm bg-background border-border focus-visible:ring-primary/30"
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
              />
              <div className="flex items-center justify-between mt-3">
                <span className="text-xs text-muted-foreground">
                  {inputText.trim().split(/\s+/).filter(Boolean).length} words
                </span>
              </div>
            </motion.div>
          ) : (
            <motion.div
              key="file"
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -8 }}
            >
              <label className="flex flex-col items-center justify-center w-full h-44 border-2 border-dashed border-border rounded-lg cursor-pointer bg-muted/30 hover:bg-muted/50 transition-colors">
                <Upload className="w-8 h-8 text-muted-foreground mb-2" />
                <span className="text-sm font-medium text-muted-foreground">
                  {fileName || "Drop a file or click to upload"}
                </span>
                <span className="text-xs text-muted-foreground/70 mt-1">
                  PDF, DOCX, or TXT
                </span>
                <input
                  type="file"
                  className="hidden"
                  accept=".pdf,.docx,.txt"
                  onChange={handleFileUpload}
                />
              </label>
              {inputText && fileName && (
                <div className="mt-3 p-3 rounded-md bg-muted/50 text-xs text-muted-foreground max-h-24 overflow-y-auto">
                  <p className="font-medium text-foreground mb-1">File selected: {fileName}</p>
                  <p className="text-xs text-muted-foreground">{inputText}</p>
                </div>
              )}
            </motion.div>
          )}
        </AnimatePresence>

        <div className="flex gap-3 mt-5">
          <Button
            onClick={handleCheck}
            disabled={(!inputText.trim() && !currentFile) || isAnalyzing}
            className="gap-2"
            size="lg"
          >
            {isAnalyzing ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <Search className="w-4 h-4" />
            )}
            {isAnalyzing ? "Analyzing..." : "Analyze Content"}
          </Button>

          {mode === "submit" && result && (
            <Button
                variant="default"
                className="bg-green-600 hover:bg-green-700 text-white gap-1.5"
                size="lg"
                onClick={handleSubmit}
            >
                <Send className="w-4 h-4" />
                Submit Assignment
            </Button>
          )}

          {(inputText || result || currentFile) && (
            <Button variant="outline" onClick={handleClear} size="lg">
              <X className="w-4 h-4 mr-1" />
              Clear
            </Button>
          )}
        </div>
      </Card>

      {/* Scanning Animation */}
      <AnimatePresence>
        {isAnalyzing && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
          >
            <Card className="p-8 flex flex-col items-center justify-center">
              <div className="relative w-16 h-16 mb-4">
                <div className="absolute inset-0 rounded-full border-4 border-primary/20" />
                <div className="absolute inset-0 rounded-full border-4 border-primary/60 animate-pulse-ring" />
                <Search className="absolute inset-0 m-auto w-6 h-6 text-primary" />
              </div>
              <p className="text-sm font-medium text-foreground">{analyzingStatus || "Scanning for plagiarism..."}</p>
              <p className="text-xs text-muted-foreground mt-1">Comparing against known sources</p>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Results */}
      <AnimatePresence>
        {result && !isAnalyzing && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="space-y-4"
          >
            {/* Summary Card */}
            <Card className="p-6 shadow-lg">
              <div className="flex flex-col md:flex-row items-center gap-6">
                <PlagiarismGauge percentage={result.percentage} severity={result.severity} />
                <div className="flex-1 text-center md:text-left">
                  <div className="flex items-center justify-center md:justify-start gap-3 mb-2">
                    <h3 className="text-2xl font-serif font-bold">Analysis Report</h3>
                    {currentLevel && (
                        <span 
                            className="text-xs font-black px-3 py-1 rounded-full uppercase tracking-widest shadow-sm"
                            style={{ backgroundColor: `${currentLevel.color}20`, color: currentLevel.color, border: `1px solid ${currentLevel.color}40` }}
                        >
                            Level: {currentLevel.label}
                        </span>
                    )}
                  </div>
                  <div className="flex flex-wrap items-center gap-3 justify-center md:justify-start">
                    <SeverityBadge severity={result.severity} />
                    <span className="text-sm text-muted-foreground">{result.wordCount} words analyzed</span>
                    <span className="text-sm text-muted-foreground">{result.matches.length} matches found</span>
                  </div>
                  <div className="mt-4 w-full bg-muted rounded-full h-2.5 overflow-hidden">
                    <motion.div
                      className="h-full rounded-full"
                      style={{
                        backgroundColor: currentLevel?.color || (
                          result.severity === "safe" ? "hsl(var(--severity-safe))"
                          : result.severity === "low" ? "hsl(var(--severity-suspicious))"
                          : result.severity === "medium" ? "hsl(var(--severity-high))"
                          : "hsl(var(--severity-severe))"
                        ),
                      }}
                      initial={{ width: 0 }}
                      animate={{ width: `${result.percentage}%` }}
                      transition={{ duration: 1, ease: "easeOut" }}
                    />
                  </div>
                </div>
              </div>
            </Card>

            {/* Highlighted Content */}
            <Card className="p-6 shadow-lg">
              <h4 className="text-lg font-serif font-bold mb-4">Content Analysis</h4>
              <div className="p-4 rounded-lg bg-muted/30 border border-border/50 max-h-[400px] overflow-y-auto">
                <HighlightedText text={result.originalText} matches={result.matches} />
              </div>
            </Card>

            {/* Sources */}
            {result.matches.length > 0 && (
              <Card className="p-6 shadow-lg">
                <h4 className="text-lg font-serif font-bold mb-4">Matched Sources</h4>
                <div className="space-y-2">
                  {result.matches.slice((currentPage - 1) * ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE).map((m, i) => (
                    <div key={i} className="flex items-center justify-between p-3 rounded-lg bg-muted/30 border border-border/50">
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium truncate">{m.source}</p>
                        <p className="text-xs text-muted-foreground truncate mt-0.5">
                          "{m.text.slice(0, 60)}..."
                        </p>
                      </div>
                      <span className="text-sm font-semibold text-highlight-plagiarism ml-3 shrink-0">
                        {m.similarity.toFixed(2)}%
                      </span>
                    </div>
                  ))}
                </div>

                {/* Pagination Controls */}
                {result.matches.length > ITEMS_PER_PAGE && (
                    <div className="flex items-center justify-between mt-6 pt-4 border-t">
                        <p className="text-xs text-muted-foreground">
                            Showing {((currentPage - 1) * ITEMS_PER_PAGE) + 1} to {Math.min(currentPage * ITEMS_PER_PAGE, result.matches.length)} of {result.matches.length} matches
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
                                {Array.from({ length: Math.ceil(result.matches.length / ITEMS_PER_PAGE) }).map((_, i) => {
                                    const pageNum = i + 1;
                                    // Only show first, last, and pages around current
                                    if (pageNum === 1 || pageNum === Math.ceil(result.matches.length / ITEMS_PER_PAGE) || (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)) {
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
                                    if (pageNum === 2 || pageNum === Math.ceil(result.matches.length / ITEMS_PER_PAGE) - 1) {
                                        return <span key={pageNum} className="text-muted-foreground text-xs">...</span>;
                                    }
                                    return null;
                                })}
                            </div>
                            <Button 
                                variant="outline" 
                                size="icon" 
                                className="h-8 w-8" 
                                onClick={() => setCurrentPage(prev => Math.min(Math.ceil(result.matches.length / ITEMS_PER_PAGE), prev + 1))}
                                disabled={currentPage === Math.ceil(result.matches.length / ITEMS_PER_PAGE)}
                            >
                                <ChevronRight className="w-4 h-4" />
                            </Button>
                        </div>
                    </div>
                )}
              </Card>
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
