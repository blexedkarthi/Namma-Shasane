import { useState, useEffect } from 'react';
import { db } from '../lib/firebase';
import { collection, onSnapshot } from 'firebase/firestore';
import { Inscription, INITIAL_SHASANAS } from '../constants';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, PieChart, Pie } from 'recharts';
import { ShieldAlert, ShieldCheck, Database, LayoutGrid } from 'lucide-react';

export default function PreservationStatsView() {
  const [shasanas, setShasanas] = useState<Inscription[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsubscribe = onSnapshot(collection(db, 'inscriptions'), (snapshot) => {
      const fetched = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })) as Inscription[];
      const initialTitles = new Set(fetched.map(s => s.title));
      const all = [
        ...fetched,
        ...INITIAL_SHASANAS.filter(s => !initialTitles.has(s.title))
      ];
      setShasanas(all);
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  const stats = {
    total: shasanas.length,
    damaged: shasanas.filter(s => s.status === 'damaged').length,
    normal: shasanas.filter(s => s.status === 'normal').length,
  };

  const statusData = [
    { name: 'Normal', value: stats.normal, color: '#2563eb' },
    { name: 'Damaged', value: stats.damaged, color: '#ef4444' }
  ];

  // Group by Dynasty (Epoch)
  const epochCounts = shasanas.reduce((acc: any, s) => {
    acc[s.epoch] = (acc[s.epoch] || 0) + 1;
    return acc;
  }, {});

  const epochData = Object.keys(epochCounts).map(name => ({
    name,
    count: epochCounts[name]
  })).sort((a, b) => b.count - a.count);

  if (loading) return <div className="p-8">Loading stats...</div>;

  return (
    <div className="p-8 max-w-5xl mx-auto space-y-8 overflow-y-auto h-full pb-20">
      <div className="space-y-1">
        <h2 className="text-3xl font-bold tracking-tight text-slate-900">Preservation Stats</h2>
        <p className="text-slate-500">Real-time monitoring of inscriptions health and distribution.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center text-blue-600">
            <Database className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Total Cached</p>
            <p className="text-2xl font-bold text-slate-900">{stats.total}</p>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 bg-green-50 rounded-xl flex items-center justify-center text-green-600">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Secure State</p>
            <p className="text-2xl font-bold text-green-600">{stats.normal}</p>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 bg-red-50 rounded-xl flex items-center justify-center text-red-600">
            <ShieldAlert className="w-6 h-6" />
          </div>
          <div>
            <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Danger Flags</p>
            <p className="text-2xl font-bold text-red-600">{stats.damaged}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-800 mb-6 flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-slate-400" />
            Health Distribution
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={statusData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="flex justify-center gap-8 mt-4">
             {statusData.map(d => (
                <div key={d.name} className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: d.color }} />
                  <span className="text-xs font-medium text-slate-600">{d.name} ({d.value})</span>
                </div>
             ))}
          </div>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-800 mb-6 flex items-center gap-2">
            <LayoutGrid className="w-4 h-4 text-slate-400" />
            Dynasty Coverage
          </h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={epochData} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} stroke="#f1f5f9" />
                <XAxis type="number" hide />
                <YAxis dataKey="name" type="category" width={100} axisLine={false} tickLine={false} style={{ fontSize: '10px', fontWeight: 'bold' }} />
                <Tooltip />
                <Bar dataKey="count" fill="#334155" radius={[0, 4, 4, 0]} barSize={20} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
