import { useState, useEffect } from 'react';
import { db } from '../lib/firebase';
import { collection, onSnapshot, query, orderBy } from 'firebase/firestore';
import { Inscription, INITIAL_SHASANAS } from '../constants';
import { ScrollText, MapPin, AlertCircle, ChevronRight, Clock } from 'lucide-react';
import { motion } from 'motion/react';

interface SavedDecodesViewProps {
  onSelect: (shasane: Inscription) => void;
}

export default function SavedDecodesView({ onSelect }: SavedDecodesViewProps) {
  const [shasanas, setShasanas] = useState<Inscription[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const q = query(collection(db, 'inscriptions'), orderBy('createdAt', 'desc'));
    const unsubscribe = onSnapshot(q, (snapshot) => {
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

  return (
    <div className="p-8 max-w-5xl mx-auto space-y-6 overflow-y-auto h-full pb-20">
      <div className="space-y-1">
        <h2 className="text-3xl font-bold tracking-tight text-slate-900">Saved Decodes</h2>
        <p className="text-slate-500">All historical inscriptions documented in the database.</p>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
           <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {shasanas.map((s, idx) => (
            <motion.div
              key={s.id || idx}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.05 }}
              onClick={() => onSelect(s)}
              className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm hover:shadow-md hover:border-blue-200 transition-all cursor-pointer group flex flex-col h-full"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="p-2 bg-slate-50 rounded-lg group-hover:bg-blue-50 transition-colors">
                  <ScrollText className="w-5 h-5 text-slate-600 group-hover:text-blue-600" />
                </div>
                {s.status === 'damaged' && (
                  <span className="bg-red-50 text-red-600 text-[10px] font-black px-2 py-0.5 rounded border border-red-100 flex items-center gap-1">
                    <AlertCircle className="w-3 h-3" />
                    DAMAGED
                  </span>
                )}
              </div>
              
              <h3 className="font-bold text-slate-900 group-hover:text-blue-600 transition-colors line-clamp-1">{s.title}</h3>
              <p className="text-xs text-slate-500 mt-2 line-clamp-3 leading-relaxed flex-grow">{s.description}</p>
              
              <div className="mt-4 pt-4 border-t border-slate-50 space-y-2">
                <div className="flex items-center gap-2 text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                  <MapPin className="w-3 h-3" />
                  {s.epoch}
                </div>
                <div className="flex items-center justify-between">
                   <div className="flex items-center gap-1 text-[10px] text-slate-400">
                     <Clock className="w-3 h-3" />
                     {s.createdAt?.seconds 
                       ? new Date(s.createdAt.seconds * 1000).toLocaleDateString() 
                       : (s.createdAt instanceof Date ? s.createdAt.toLocaleDateString() : 'N/A')}
                   </div>
                   <ChevronRight className="w-4 h-4 text-slate-300 group-hover:text-blue-400 group-hover:translate-x-1 transition-all" />
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
