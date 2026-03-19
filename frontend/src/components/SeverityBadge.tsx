import { cn } from "@/lib/utils";

interface SeverityBadgeProps {
  severity: "safe" | "low" | "medium" | "high";
  className?: string;
}

const config = {
  safe: { label: "Safe", bg: "bg-severity-safe/15", text: "text-severity-safe", border: "border-severity-safe/30" },
  low: { label: "Low Risk", bg: "bg-severity-suspicious/15", text: "text-severity-suspicious", border: "border-severity-suspicious/30" },
  medium: { label: "Medium Risk", bg: "bg-severity-high/15", text: "text-severity-high", border: "border-severity-high/30" },
  high: { label: "High Risk", bg: "bg-severity-severe/15", text: "text-severity-severe", border: "border-severity-severe/30" },
};

export function SeverityBadge({ severity, className }: SeverityBadgeProps) {
  const c = config[severity];
  return (
    <span className={cn("inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border", c.bg, c.text, c.border, className)}>
      {c.label}
    </span>
  );
}
