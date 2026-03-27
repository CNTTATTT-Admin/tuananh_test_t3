import { motion } from "framer-motion";
import { Shield, Zap, Eye, BarChart3 } from "lucide-react";
import { Header } from "@/components/Header";
import { PlagiarismChecker } from "@/components/PlagiarismChecker";

const features = [
  { icon: Zap, title: "Instant Analysis", desc: "Get results in seconds" },
  { icon: Eye, title: "Smart Detection", desc: "AI-powered comparison" },
  { icon: BarChart3, title: "Detailed Reports", desc: "Source-level breakdown" },
];

const Index = () => {
  return (
    <div className="min-h-screen bg-background">
      <Header />

      {/* Hero */}
      <section className="relative overflow-hidden border-b border-border/30">
        <div className="absolute inset-0 bg-gradient-to-br from-primary/5 via-transparent to-primary/3" />
        <div className="container relative py-16 md:py-24 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
          >
            <div className="flex items-center justify-center gap-2 mb-4">
              <Shield className="w-10 h-10 text-primary" />
            </div>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-serif font-bold text-foreground mb-4 leading-tight">
              Plagiarism Detection
              <br />
              <span className="text-gradient">Made Simple</span>
            </h1>
            <p className="text-lg text-muted-foreground max-w-xl mx-auto">
              Upload documents or paste text to instantly detect similarity and protect academic integrity.
            </p>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="flex justify-center gap-8 mt-10"
          >
            {features.map((f, i) => (
              <div key={i} className="flex flex-col items-center gap-1.5">
                <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
                  <f.icon className="w-5 h-5 text-primary" />
                </div>
                <span className="text-xs font-semibold text-foreground">{f.title}</span>
                <span className="text-xs text-muted-foreground">{f.desc}</span>
              </div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* Checker */}
      <section className="container max-w-3xl py-12">
        <PlagiarismChecker />
      </section>
    </div>
  );
};

export default Index;
