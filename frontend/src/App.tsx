import { useState } from 'react';
import { QueryClient, QueryClientProvider, useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';
import { 
  Activity, 
  Layers, 
  Cpu, 
  Clock, 
  Plus, 
  RefreshCw, 
  CheckCircle2, 
  AlertTriangle, 
  Play, 
  Search,
  ExternalLink 
} from 'lucide-react';
import type { Job } from './types/job';
import { JobDetailDrawer } from './components/JobDetailDrawer';

const queryClient = new QueryClient();

function MainDashboard() {
  const qc = useQueryClient();
  const [selectedJob, setSelectedJob] = useState<Job | null>(null);
  const [isSubmitOpen, setIsSubmitOpen] = useState(false);
  const [jobType, setJobType] = useState('PAYMENT_PROCESSING');
  const [payload, setPayload] = useState('{"orderId":"ord-9921","amount":1500.00}');
  const [searchTerm, setSearchTerm] = useState('');

  const { data: jobs = [], isLoading } = useQuery<Job[]>({
    queryKey: ['jobs'],
    queryFn: async () => {
      const res = await axios.get('http://localhost:8080/api/v1/jobs');
      return res.data;
    },
    refetchInterval: 2500,
  });

  const submitMutation = useMutation({
    mutationFn: async () => {
      return axios.post('http://localhost:8080/api/v1/jobs/submit', {
        type: jobType,
        payload: payload,
      });
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['jobs'] });
      setIsSubmitOpen(false);
    },
  });

  const pendingCount = jobs.filter(j => j.state === 'PENDING').length;
  const runningCount = jobs.filter(j => j.state === 'RUNNING').length;
  const completedCount = jobs.filter(j => j.state === 'COMPLETED').length;
  const avgRiskScore = jobs.length > 0
    ? (jobs.reduce((acc, curr) => acc + (curr.predictedRiskScore || 0), 0) / jobs.length).toFixed(1)
    : '0.0';

  const filteredJobs = jobs.filter(j => 
    j.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
    j.jobType.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-slate-900 flex flex-col font-sans">
      <header className="h-16 border-b border-slate-200/80 bg-white px-8 flex items-center justify-between sticky top-0 z-10 shadow-xs">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white font-bold text-sm shadow-xs shadow-blue-500/20">
            DO
          </div>
          <div>
            <h1 className="text-sm font-bold text-slate-900 leading-tight">Distributed Orchestrator</h1>
            <p className="text-[11px] text-slate-400">Control Plane & Pipeline Telemetry</p>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 bg-emerald-50 text-emerald-700 px-3 py-1.5 rounded-full text-xs font-medium border border-emerald-200">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            Live Sync (2.5s)
          </div>
          <button
            onClick={() => setIsSubmitOpen(!isSubmitOpen)}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-medium px-3.5 py-2 rounded-lg shadow-xs transition cursor-pointer"
          >
            <Plus className="w-3.5 h-3.5" /> Submit Job
          </button>
        </div>
      </header>

      <main className="flex-1 p-8 max-w-7xl mx-auto w-full space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <span className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Pending Queue</span>
              <span className="text-2xl font-bold text-slate-900 mt-1 block">{pendingCount}</span>
            </div>
            <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
              <Clock className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <span className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Active Leases</span>
              <span className="text-2xl font-bold text-slate-900 mt-1 block">{runningCount}</span>
            </div>
            <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
              <Cpu className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <span className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Completed</span>
              <span className="text-2xl font-bold text-slate-900 mt-1 block">{completedCount}</span>
            </div>
            <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="w-5 h-5" />
            </div>
          </div>

          <div className="bg-white p-5 rounded-xl border border-slate-200/80 shadow-xs flex items-center justify-between">
            <div>
              <span className="text-xs font-medium text-slate-400 uppercase tracking-wider block">Avg Risk Score</span>
              <span className="text-2xl font-bold text-slate-900 mt-1 block">{avgRiskScore} <span className="text-xs text-slate-400 font-normal">/ 10</span></span>
            </div>
            <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
              <Activity className="w-5 h-5" />
            </div>
          </div>
        </div>

        {isSubmitOpen && (
          <div className="bg-white border border-slate-200/80 rounded-xl p-6 shadow-sm space-y-4 animate-in fade-in-50 duration-150">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-sm font-bold text-slate-900">Submit Pipeline Job</h3>
              <span className="text-xs text-slate-400">ML Evaluation applied automatically</span>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Job Type</label>
                <select
                  value={jobType}
                  onChange={(e) => setJobType(e.target.value)}
                  className="w-full text-xs border border-slate-200 rounded-lg p-2.5 bg-slate-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="PAYMENT_PROCESSING">PAYMENT_PROCESSING</option>
                  <option value="DATA_SYNC">DATA_SYNC</option>
                  <option value="BATCH_NOTIFICATION">BATCH_NOTIFICATION</option>
                </select>
              </div>

              <div className="md:col-span-2">
                <label className="block text-xs font-medium text-slate-600 mb-1">Payload (JSON)</label>
                <input
                  type="text"
                  value={payload}
                  onChange={(e) => setPayload(e.target.value)}
                  className="w-full text-xs font-mono border border-slate-200 rounded-lg p-2.5 bg-slate-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setIsSubmitOpen(false)}
                className="px-3 py-1.5 text-xs text-slate-600 hover:bg-slate-100 rounded-lg transition cursor-pointer"
              >
                Cancel
              </button>
              <button
                onClick={() => submitMutation.mutate()}
                disabled={submitMutation.isPending}
                className="flex items-center gap-1.5 px-4 py-1.5 text-xs font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition disabled:opacity-50 cursor-pointer"
              >
                {submitMutation.isPending ? 'Ingesting...' : 'Ingest & Evaluate'}
              </button>
            </div>
          </div>
        )}

        <div className="bg-white border border-slate-200/80 rounded-xl shadow-xs overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex items-center gap-2">
              <Layers className="w-4 h-4 text-slate-400" />
              <h2 className="text-sm font-bold text-slate-900">Orchestrator Execution Pipeline</h2>
              <span className="bg-slate-100 text-slate-600 px-2 py-0.5 rounded-full text-[11px] font-semibold">
                {jobs.length} total
              </span>
            </div>

            <div className="relative">
              <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                placeholder="Filter by UUID or Type..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs border border-slate-200 rounded-lg w-64 bg-slate-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#F8FAFC] border-b border-slate-100 text-slate-400 uppercase font-semibold tracking-wider">
                <tr>
                  <th className="px-6 py-3.5">Job ID</th>
                  <th className="px-6 py-3.5">Type</th>
                  <th className="px-6 py-3.5">Status</th>
                  <th className="px-6 py-3.5">Risk Score</th>
                  <th className="px-6 py-3.5">Est. Duration</th>
                  <th className="px-6 py-3.5">Submitted</th>
                  <th className="px-6 py-3.5 text-right">Inspect</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-medium">
                {isLoading && (
                  <tr>
                    <td colSpan={7} className="px-6 py-12 text-center text-slate-400">
                      <RefreshCw className="w-5 h-5 animate-spin mx-auto mb-2 text-slate-300" />
                      Loading pipeline states...
                    </td>
                  </tr>
                )}
                {!isLoading && filteredJobs.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-6 py-12 text-center text-slate-400">
                      No jobs currently in the orchestrator pipeline.
                    </td>
                  </tr>
                )}
                {filteredJobs.map((job) => (
                  <tr 
                    key={job.id} 
                    onClick={() => setSelectedJob(job)}
                    className="hover:bg-slate-50/80 transition cursor-pointer group"
                  >
                    <td className="px-6 py-4 font-mono text-[11px] text-slate-500 group-hover:text-blue-600 transition">
                      {job.id}
                    </td>
                    <td className="px-6 py-4 font-bold text-slate-800">
                      {job.jobType}
                    </td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-semibold ${
                        job.state === 'COMPLETED' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' :
                        job.state === 'RUNNING' ? 'bg-blue-50 text-blue-700 border border-blue-200' :
                        job.state === 'FAILED' ? 'bg-rose-50 text-rose-700 border border-rose-200' :
                        'bg-amber-50 text-amber-700 border border-amber-200'
                      }`}>
                        {job.state === 'COMPLETED' && <CheckCircle2 className="w-3 h-3" />}
                        {job.state === 'PENDING' && <Clock className="w-3 h-3" />}
                        {job.state === 'RUNNING' && <Play className="w-3 h-3" />}
                        {job.state === 'FAILED' && <AlertTriangle className="w-3 h-3" />}
                        {job.state}
                      </span>
                    </td>
                    <td className="px-6 py-4 font-semibold text-slate-700">
                      {job.predictedRiskScore}
                    </td>
                    <td className="px-6 py-4 text-slate-500">
                      {job.predictedDurationMs} ms
                    </td>
                    <td className="px-6 py-4 text-slate-400 text-[11px]">
                      {new Date(job.createdAt).toLocaleTimeString()}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button className="text-slate-400 group-hover:text-blue-600 p-1">
                        <ExternalLink className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </main>

      <JobDetailDrawer job={selectedJob} onClose={() => setSelectedJob(null)} />
    </div>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <MainDashboard />
    </QueryClientProvider>
  );
}
