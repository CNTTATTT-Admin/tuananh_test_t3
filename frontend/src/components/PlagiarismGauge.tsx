import { motion } from "framer-motion";

interface PlagiarismGaugeProps {
  percentage: number;
  severity: "safe" | "low" | "medium" | "high";
  size?: number;
}

const severityColors = {
  safe: "hsl(142, 71%, 45%)",
  low: "hsl(45, 93%, 47%)",
  medium: "hsl(25, 95%, 53%)",
  high: "hsl(0, 72%, 51%)",
};

export function PlagiarismGauge({ percentage, severity, size = 180 }: PlagiarismGaugeProps) {
  const strokeWidth = 12;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (percentage / 100) * circumference;
  const color = severityColors[severity];

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <svg width={size} height={size} className="-rotate-90">
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="hsl(var(--border))"
          strokeWidth={strokeWidth}
        />
        <motion.circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth={strokeWidth}
          strokeLinecap="round"
          strokeDasharray={circumference}
          initial={{ strokeDashoffset: circumference }}
          animate={{ strokeDashoffset: offset }}
          transition={{ duration: 1.5, ease: "easeOut" }}
        />
      </svg>
      <div className="absolute flex flex-col items-center">
        <motion.span
          className="text-4xl font-serif font-bold"
          style={{ color }}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.5 }}
        >
          {percentage.toFixed(2)}%
        </motion.span>
        <span className="text-xs text-muted-foreground font-medium mt-1">Similarity</span>
      </div>
    </div>
  );
}
