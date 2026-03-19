import { PlagiarismMatch } from "@/lib/plagiarism-engine";
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip";

interface HighlightedTextProps {
  text: string;
  matches: PlagiarismMatch[];
}

export function HighlightedText({ text, matches }: HighlightedTextProps) {
  if (matches.length === 0) {
    return <p className="text-sm leading-relaxed whitespace-pre-wrap">{text}</p>;
  }

  const segments: { text: string; match?: PlagiarismMatch }[] = [];
  let lastEnd = 0;

  for (const match of matches) {
    if (match.startIndex > lastEnd) {
      segments.push({ text: text.slice(lastEnd, match.startIndex) });
    }
    segments.push({ text: text.slice(match.startIndex, match.endIndex), match });
    lastEnd = match.endIndex;
  }

  if (lastEnd < text.length) {
    segments.push({ text: text.slice(lastEnd) });
  }

  return (
    <div className="text-sm leading-relaxed whitespace-pre-wrap">
      {segments.map((seg, i) =>
        seg.match ? (
          <Tooltip key={i}>
            <TooltipTrigger asChild>
              <mark className="bg-highlight-bg text-highlight-plagiarism cursor-help rounded px-0.5 border-b-2 border-highlight-plagiarism/40">
                {seg.text}
              </mark>
            </TooltipTrigger>
            <TooltipContent side="top" className="max-w-xs">
              <p className="font-semibold text-xs">Source: {seg.match.source}</p>
              <p className="text-xs text-muted-foreground mt-1">{seg.match.similarity}% similarity</p>
            </TooltipContent>
          </Tooltip>
        ) : (
          <span key={i}>{seg.text}</span>
        )
      )}
    </div>
  );
}
