import { useState, useEffect, useMemo } from "react";
import { Card } from "@/components/ui/card";
import { Users, BookOpen, Search, FileText, Loader2, Calendar } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend, AreaChart, Area,
} from "recharts";
import classService from "@/services/class.service";
import { userService } from "@/services/user.service";

interface StatData {
  totalUsers: number;
  totalClasses: number;
  totalChecks: number;
  totalSubmissions: number;
}

interface MonthlyCheck {
  month: string;
  checks: number;
  submissions: number;
}

interface SeverityDistribution {
  name: string;
  value: number;
  fill: string;
}

const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

export default function StatsPage() {
  const [loading, setLoading] = useState(true);
  
  // Base raw data
  const [baseStats, setBaseStats] = useState({ totalUsers: 0, totalClasses: 0 });
  const [rawSubmissions, setRawSubmissions] = useState<any[]>([]);

  // Filters
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  useEffect(() => {
    const fetchGlobalStats = async () => {
      try {
        const [usersResp, classesResp, subsResp] = await Promise.all([
          userService.getAllUsers(1, 1),
          classService.getClasses(),
          classService.getAllSubmissions()
        ]);

        const classes = classesResp.results || classesResp;
        setBaseStats({
            totalUsers: usersResp.meta?.total || 0,
            totalClasses: Array.isArray(classes) ? classes.length : 0
        });
        setRawSubmissions(Array.isArray(subsResp) ? subsResp : []);
      } catch (error) {
        console.error("Failed to load statistics", error);
      } finally {
        setLoading(false);
      }
    };

    fetchGlobalStats();
  }, []);

  // Derived state calculations
  const { stats, monthlyData, severityData } = useMemo(() => {
    let filteredSubs = rawSubmissions;

    if (startDate) {
        const start = new Date(startDate).getTime();
        filteredSubs = filteredSubs.filter(sub => new Date(sub.submittedAt).getTime() >= start);
    }
    if (endDate) {
        const end = new Date(endDate).getTime();
        filteredSubs = filteredSubs.filter(sub => new Date(sub.submittedAt).getTime() <= end + 86400000); // include end day
    }

    let safe = 0, low = 0, medium = 0, high = 0;
    const monthlyCounts: Record<string, number> = {};
    MONTHS.forEach(m => monthlyCounts[m] = 0);

    filteredSubs.forEach(sub => {
       const score = sub.plagiarismPercent || 0;
       if (score <= 20) safe++;
       else if (score <= 40) low++;
       else if (score <= 60) medium++;
       else high++;

       const d = new Date(sub.submittedAt);
       const mName = MONTHS[d.getMonth()];
       if (monthlyCounts[mName] !== undefined) {
           monthlyCounts[mName]++;
       }
    });

    const activeSeverity = [
      { name: "Safe (0-20%)", value: safe, fill: "hsl(var(--severity-safe))" },
      { name: "Low Risk (21-40%)", value: low, fill: "hsl(var(--severity-suspicious))" },
      { name: "Medium Risk (41-60%)", value: medium, fill: "hsl(var(--severity-high))" },
      { name: "High Risk (>60%)", value: high, fill: "hsl(var(--severity-severe))" }
    ].filter(s => s.value > 0);

    const newMonthlyData = MONTHS.map(month => ({
        month,
        checks: monthlyCounts[month],
        submissions: monthlyCounts[month]
    }));

    return {
        stats: {
            totalUsers: baseStats.totalUsers,
            totalClasses: baseStats.totalClasses,
            totalChecks: filteredSubs.length,
            totalSubmissions: filteredSubs.length
        },
        monthlyData: newMonthlyData,
        severityData: activeSeverity.length > 0 ? activeSeverity : [{ name: "No Data", value: 1, fill: "hsl(var(--muted))" }]
    };
  }, [rawSubmissions, baseStats, startDate, endDate]);

  if (loading) {
     return (
        <div className="flex flex-col items-center justify-center min-h-[400px]">
           <Loader2 className="w-10 h-10 animate-spin text-primary mb-4" />
           <p className="text-muted-foreground font-medium animate-pulse">Computing system analytics...</p>
        </div>
     );
  }

  const statCards = [
    { label: "Total Users", value: stats.totalUsers, icon: Users, color: "text-primary" },
    { label: "Total Classes", value: stats.totalClasses, icon: BookOpen, color: "text-emerald-500" },
    { label: "Filtered Checks", value: stats.totalChecks, icon: Search, color: "text-amber-500" },
    { label: "Filtered Submissions", value: stats.totalSubmissions, icon: FileText, color: "text-rose-500" },
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
             <h1 className="text-3xl font-serif font-bold tracking-tight text-slate-900 dark:text-slate-50">System Statistics</h1>
             <p className="text-slate-500 font-medium mt-1">Global platform metrics and operational insights</p>
          </div>
          
          <div className="flex flex-col sm:flex-row items-end gap-3 bg-white/40 p-3 rounded-2xl shadow-sm border border-slate-100 backdrop-blur-sm">
             <div className="space-y-1">
                <label className="text-[10px] font-bold text-slate-700 uppercase tracking-widest pl-1">From Date</label>
                <div className="relative">
                   <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                   <input 
                      type="date" 
                      className="flex h-10 w-full rounded-xl border-none bg-white px-9 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary shadow-sm text-slate-700" 
                      value={startDate} 
                      onChange={e => setStartDate(e.target.value)} 
                   />
                </div>
             </div>
             <div className="space-y-1">
                <label className="text-[10px] font-bold text-slate-700 uppercase tracking-widest pl-1">To Date</label>
                <div className="relative">
                   <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                   <input 
                      type="date" 
                      className="flex h-10 w-full rounded-xl border-none bg-white px-9 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary shadow-sm text-slate-700" 
                      value={endDate} 
                      onChange={e => setEndDate(e.target.value)} 
                   />
                </div>
             </div>
             {(startDate || endDate) && (
                 <Button onClick={() => { setStartDate(""); setEndDate(""); }} variant="outline" className="h-10 rounded-xl bg-white hover:bg-rose-50 hover:text-rose-600 transition-colors border-none shadow-sm">
                     Reset
                 </Button>
             )}
          </div>
      </div>

      {/* Stat Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {statCards.map((s, i) => (
          <Card key={i} className="p-5 shadow-lg border-none bg-white/50 backdrop-blur-sm hover:scale-105 transition-transform duration-300">
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm font-bold tracking-tight uppercase text-slate-400">{s.label}</span>
              <div className={`p-2 rounded-xl bg-slate-50 border shadow-sm ${s.color}`}>
                <s.icon className="w-5 h-5" />
              </div>
            </div>
            <p className="text-4xl font-black tabular-nums tracking-tighter text-slate-700">{s.value.toLocaleString()}</p>
          </Card>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Monthly Activity */}
        <Card className="p-6 shadow-xl border-none bg-white/50 backdrop-blur-sm group">
          <h3 className="font-serif font-bold text-lg mb-4 text-slate-700">Monthly Submissions</h3>
          <ResponsiveContainer width="100%" height={280}>
            <AreaChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: "hsl(var(--muted-foreground))" }} />
              <YAxis tick={{ fontSize: 12, fill: "hsl(var(--muted-foreground))" }} />
              <Tooltip
                contentStyle={{
                  backgroundColor: "hsl(var(--card))",
                  border: "1px solid hsl(var(--border))",
                  borderRadius: "var(--radius)",
                  fontSize: 12,
                }}
              />
              <Area
                type="monotone"
                dataKey="submissions"
                stroke="hsl(var(--primary))"
                fill="hsl(var(--primary) / 0.15)"
                strokeWidth={3}
                name="Checks/Submissions"
                animationDuration={1500}
              />
            </AreaChart>
          </ResponsiveContainer>
        </Card>

        {/* Severity Distribution */}
        <Card className="p-6 shadow-xl border-none bg-white/50 backdrop-blur-sm group">
          <h3 className="font-serif font-bold text-lg mb-4 text-slate-700">Severity Distribution</h3>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie
                data={severityData}
                cx="50%"
                cy="50%"
                innerRadius={70}
                outerRadius={105}
                paddingAngle={4}
                dataKey="value"
                label={({ name, percent }) => `${(percent * 100).toFixed(0)}%`}
                labelLine={false}
                animationDuration={1500}
                stroke="none"
              >
                {severityData.map((entry, index) => (
                  <Cell key={index} fill={entry.fill} className="hover:opacity-80 transition-opacity" />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: "hsl(var(--card))",
                  border: "none",
                  boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)",
                  borderRadius: "12px",
                  fontSize: 12,
                  fontWeight: "bold"
                }}
              />
              <Legend verticalAlign="bottom" height={36}/>
            </PieChart>
          </ResponsiveContainer>
        </Card>

        {/* Checks vs Submissions Bar */}
        <Card className="p-6 shadow-xl border-none bg-white/50 backdrop-blur-sm lg:col-span-2 group">
          <h3 className="font-serif font-bold text-lg mb-4 text-slate-700">Platform Growth Pulse</h3>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" vertical={false}/>
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: "hsl(var(--muted-foreground))" }} axisLine={false} tickLine={false}/>
              <YAxis tick={{ fontSize: 12, fill: "hsl(var(--muted-foreground))" }} axisLine={false} tickLine={false}/>
              <Tooltip
                cursor={{fill: 'hsl(var(--muted)/0.5)'}}
                contentStyle={{
                  backgroundColor: "hsl(var(--card))",
                  border: "none",
                  boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)",
                  borderRadius: "12px",
                  fontSize: 12,
                }}
              />
              <Bar dataKey="checks" fill="hsl(var(--primary))" radius={[6, 6, 0, 0]} name="Checks" animationDuration={1000} />
            </BarChart>
          </ResponsiveContainer>
        </Card>
      </div>
    </div>
  );
}


