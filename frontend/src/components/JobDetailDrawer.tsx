import { X, CheckCircle2, Clock, AlertTriangle, Play, ShieldAlert, Timer } from 'lucide-react';
import type { Job } from '../types/job';

interface JobDetailDrawerProps {
  job: Job | null;
  onClose: () => void;
}

export function JobDetailDrawer({ job, onClose }: JobDetailDrawerProps) {
  if (!job) return null;

  let parsedPayload = {};
  try {
    parsedPayload = JSON.parse(job.payload);
  } catch {
    parsedPayload = { raw: job.payload };
  }

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-900/40 backdrop-blur-xs flex justify-end">
      <div className="w-full max-w-xl bg-white h-full shadow-2xl flex flex-col justify-between border-l border-slate-200 animate-in slide-in-from-right duration-200">
        
        {/* Header */}
        <div className="p-6 border-b border-slate-100 flex items-start justify-between">
          <div>
            <span className="text-xs font-mono font-medium text-slate-400 uppercase tracking-wider">Job Inspection</span>
            <h2 className="text-lg font-bold text-slate-900 mt-0.5">{job.jobType}</h2>
            <p className="text-xs font-mono text-slate-500 mt-1">{job.id}</p>
          </div>
          <button 
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6 overflow-y-auto flex-1 text-sm">
          
          {/* Status & Timing Pill */}
          <div className="grid grid-cols-2 gap-3">
            <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-xl">
              <span className="text-xs text-slate-400 block mb-1">Lifecycle State</span>
              <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold ${
                job.state === 'COMPLETED' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' :
                job.state === 'RUNNING' ? 'bg-blue-50 text-blue-700 border border-blue-200' :
                job.state === 'FAILED' ? 'bg-rose-50 text-rose-700 border border-rose-200' :
                'bg-amber-50 text-amber-700 border border-amber-200'
              }`}>
                {job.state === 'COMPLETED' && <CheckCircle2 className="w-3.5 h-3.5" />}
                {job.state === 'PENDING' && <Clock className="w-3.5 h-3.5" />}
                {job.state === 'RUNNING' && <Play className="w-3.5 h-3.5" />}
                {job.state === 'FAILED' && <AlertTriangle className="w-3.5 h-3.5" />}
                {job.state}
              </span>
            </div>

            <div className="p-3.5 bg-slate-50 border border-slate-100 rounded-xl">
              <span className="text-xs text-slate-400 block mb-1">Assigned Worker</span>
              <span className="font-mono text-xs text-slate-700">
                {job.assignedWorkerId ? job.assignedWorkerId : 'Unassigned (Waiting)'}
              </span>
            </div>
          </div>

          {/* ML Inference Cards */}
          <div>
            <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">ML Inference Predictor</h3>
            <div className="grid grid-cols-2 gap-3">
              <div className="p-3.5 bg-amber-50/50 border border-amber-100 rounded-xl flex items-center gap-3">
                <ShieldAlert className="w-5 h-5 text-amber-600" />
                <div>
                  <span className="text-xs text-slate-500 block">Risk Score</span>
                  <span className="text-sm font-bold text-slate-900">{job.predictedRiskScore} / 10.0</span>
                </div>
              </div>

              <div className="p-3.5 bg-blue-50/50 border border-blue-100 rounded-xl flex items-center gap-3">
                <Timer className="w-5 h-5 text-blue-600" />
                <div>
                  <span className="text-xs text-slate-500 block">Est. Duration</span>
                  <span className="text-sm font-bold text-slate-900">{job.predictedDurationMs} ms</span>
                </div>
              </div>
            </div>
          </div>

          {/* Execution History Timeline */}
          <div>
            <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-3">Lifecycle Progress</h3>
            <div className="border-l-2 border-slate-200 ml-3 pl-4 space-y-4">
              <div className="relative">
                <div className="absolute -left-[23px] top-1 w-3 h-3 rounded-full bg-emerald-500 ring-4 ring-white" />
                <p className="text-xs font-semibold text-slate-800">Job Ingested & Evaluated</p>
                <p className="text-xs text-slate-400">{new Date(job.createdAt).toLocaleTimeString()}</p>
              </div>
              <div className="relative">
                <div className={`absolute -left-[23px] top-1 w-3 h-3 rounded-full ring-4 ring-white ${
                  job.state !== 'PENDING' ? 'bg-blue-500' : 'bg-slate-300'
                }`} />
                <p className="text-xs font-semibold text-slate-800">Worker Claim & Lock</p>
                <p className="text-xs text-slate-400">
                  {job.state !== 'PENDING' ? 'Acquired via SKIP LOCKED' : 'Pending queue pick-up'}
                </p>
              </div>
              <div className="relative">
                <div className={`absolute -left-[23px] top-1 w-3 h-3 rounded-full ring-4 ring-white ${
                  job.state === 'COMPLETED' ? 'bg-emerald-500' : job.state === 'FAILED' ? 'bg-rose-500' : 'bg-slate-300'
                }`} />
                <p className="text-xs font-semibold text-slate-800">Final Execution Result</p>
                <p className="text-xs text-slate-400">
                  {job.state === 'COMPLETED' ? 'Successfully processed' : job.state === 'FAILED' ? 'Failed after retries' : 'In queue'}
                </p>
              </div>
            </div>
          </div>

          {/* JSON Payload Inspection */}
          <div>
            <h3 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Payload Data</h3>
            <pre className="p-4 bg-slate-900 text-emerald-400 rounded-xl text-xs font-mono overflow-x-auto shadow-inner">
              {JSON.stringify(parsedPayload, null, 2)}
            </pre>
          </div>

        </div>

        {/* Footer */}
        <div className="p-4 border-t border-slate-100 bg-slate-50/50 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-white border border-slate-200 hover:bg-slate-50 text-slate-700 text-xs font-medium rounded-lg shadow-xs transition cursor-pointer"
          >
            Close Inspector
          </button>
        </div>

      </div>
    </div>
  );
}
