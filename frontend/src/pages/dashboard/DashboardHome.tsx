import { PlagiarismChecker } from "@/components/PlagiarismChecker";

export default function DashboardHome() {
  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-3xl font-serif font-bold mb-6">Check Plagiarism</h1>
      <PlagiarismChecker />
    </div>
  );
}
