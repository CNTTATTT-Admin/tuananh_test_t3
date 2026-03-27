// Mock plagiarism detection engine
// In production, this would call an API or use NLP algorithms

export interface PlagiarismMatch {
  text: string;
  startIndex: number;
  endIndex: number;
  source: string;
  similarity: number;
}

export interface PlagiarismResult {
  percentage: number;
  severity: "safe" | "low" | "medium" | "high";
  matches: PlagiarismMatch[];
  originalText: string;
  wordCount: number;
  checkDate: string;
}

const MOCK_SOURCES = [
  "Wikipedia - Introduction to Computer Science",
  "Academic Journal of Software Engineering, Vol. 12",
  "Stanford CS101 Course Notes",
  "MIT OpenCourseWare - Algorithms",
  "ResearchGate - Machine Learning Fundamentals",
  "IEEE Transactions on Pattern Analysis",
];

function getSeverity(percentage: number): PlagiarismResult["severity"] {
  if (percentage <= 20) return "safe";
  if (percentage <= 40) return "low";
  if (percentage <= 60) return "medium";
  return "high";
}

export function analyzePlagiarism(text: string): Promise<PlagiarismResult> {
  return new Promise((resolve) => {
    const delay = 1500 + Math.random() * 2000;
    setTimeout(() => {
      const words = text.trim().split(/\s+/);
      const wordCount = words.length;

      if (wordCount < 5) {
        resolve({
          percentage: 0,
          severity: "safe",
          matches: [],
          originalText: text,
          wordCount,
          checkDate: new Date().toISOString(),
        });
        return;
      }

      // Generate mock matches based on content
      const sentences = text.split(/[.!?]+/).filter((s) => s.trim().length > 10);
      const matchCount = Math.min(
        Math.floor(sentences.length * (0.1 + Math.random() * 0.5)),
        sentences.length
      );

      const selectedIndices = new Set<number>();
      while (selectedIndices.size < matchCount && selectedIndices.size < sentences.length) {
        selectedIndices.add(Math.floor(Math.random() * sentences.length));
      }

      const matches: PlagiarismMatch[] = [];
      let matchedChars = 0;

      selectedIndices.forEach((idx) => {
        const sentence = sentences[idx].trim();
        if (!sentence) return;
        const startIndex = text.indexOf(sentence);
        if (startIndex === -1) return;

        matchedChars += sentence.length;
        matches.push({
          text: sentence,
          startIndex,
          endIndex: startIndex + sentence.length,
          source: MOCK_SOURCES[Math.floor(Math.random() * MOCK_SOURCES.length)],
          similarity: 70 + Math.floor(Math.random() * 30),
        });
      });

      const percentage = Math.min(
        Math.round((matchedChars / text.length) * 100),
        95
      );

      resolve({
        percentage,
        severity: getSeverity(percentage),
        matches: matches.sort((a, b) => a.startIndex - b.startIndex),
        originalText: text,
        wordCount,
        checkDate: new Date().toISOString(),
      });
    }, delay);
  });
}

export function extractTextFromFile(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    if (file.type === "text/plain") {
      const reader = new FileReader();
      reader.onload = (e) => resolve(e.target?.result as string);
      reader.onerror = () => reject(new Error("Failed to read file"));
      reader.readAsText(file);
    } else {
      // Mock extraction for PDF/DOCX
      setTimeout(() => {
        resolve(
          `[Extracted content from ${file.name}]\n\nThis is simulated extracted text from the uploaded document. In a production system, the file would be parsed server-side using libraries like pdf-parse or mammoth.js to extract the actual text content.\n\nThe document appears to contain academic content discussing various topics related to computer science, software engineering, and information technology. The text has been formatted and structured for academic submission purposes.\n\nKey findings from the document include discussions about algorithm complexity, data structure optimization, and modern software development practices. The author references several established works in the field and provides analysis based on empirical data collected during the research phase.`
        );
      }, 800);
    }
  });
}
