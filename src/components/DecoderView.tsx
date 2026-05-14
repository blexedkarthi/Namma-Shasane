import { motion, AnimatePresence } from 'motion/react';
import { X, MapPin, ScrollText, AlertTriangle, Languages, Sparkles, Loader2 } from 'lucide-react';
import { Inscription } from '../constants';
import { useState, useEffect } from 'react';
import { db, auth, googleProvider, signInWithPopup, handleFirestoreError, OperationType } from '../lib/firebase';
import { doc, updateDoc, serverTimestamp } from 'firebase/firestore';

interface DecoderViewProps {
  shasane: Inscription | null;
  onClose: () => void;
  onUpdate: (updated: Inscription) => void;
}

export default function DecoderView({ shasane, onClose, onUpdate }: DecoderViewProps) {
  const [language, setLanguage] = useState<'EN' | 'KN'>('EN');
  const [reporting, setReporting] = useState(false);
  const [aiStory, setAiStory] = useState('');
  const [loadingStory, setLoadingStory] = useState(false);

  useEffect(() => {
    setAiStory('');
    setLanguage('EN');
  }, [shasane?.id]);

  if (!shasane) return null;

  const generateStory = async () => {
    setLoadingStory(true);
    try {
      const response = await fetch('/api/story', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: shasane.title,
          dynasty: shasane.epoch,
          period: 'Ancient',
          location: 'Karnataka',
          description: shasane.description
        }),
      });
      const data = await response.json();
      setAiStory(data.story);
    } catch (error) {
      console.error("Story generation error:", error);
      setAiStory("The mists of time obscure this stone's deeper secrets today. Legend says it was commissioned by a great ruler to honor a local hero.");
    } finally {
      setLoadingStory(false);
    }
  };

  const handleSignIn = async () => {
    try {
      await signInWithPopup(auth, googleProvider);
    } catch (error) {
      console.error("Sign in error:", error);
    }
  };

  const handleReport = async () => {
    if (!shasane.id) return;
    
    if (!auth.currentUser) {
      handleSignIn();
      return;
    }

    const path = `inscriptions/${shasane.id}`;
    try {
      const docRef = doc(db, 'inscriptions', shasane.id);
      await updateDoc(docRef, {
        status: 'damaged',
        updatedAt: serverTimestamp()
      });
      onUpdate({ ...shasane, status: 'damaged' });
      setReporting(false);
    } catch (error) {
      handleFirestoreError(error, OperationType.UPDATE, path);
      // Fallback for demo
      onUpdate({ ...shasane, status: 'damaged' });
      setReporting(false);
    }
  };

  return (
    <AnimatePresence>
      {shasane && (
        <motion.div
          initial={{ opacity: 0, x: 100 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: 100 }}
          className="fixed top-0 right-0 h-full w-full md:w-[450px] z-[60] bg-white shadow-2xl flex flex-col border-l border-slate-200"
        >
          {/* Header */}
          <div className="p-6 flex items-center justify-between border-b border-slate-100">
            <h2 className="text-xl font-bold text-slate-900 tracking-tight">Location Details</h2>
            <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full transition">
              <X className="w-5 h-5 text-slate-500" />
            </button>
          </div>

          {/* Content */}
          <div className="flex-1 overflow-y-auto p-8 space-y-8">
            {/* Stone Image Placeholder */}
            <div className="aspect-video w-full bg-slate-100 rounded-xl border border-slate-200 flex items-center justify-center overflow-hidden relative group shadow-inner">
              {shasane.imageUrl ? (
                <img src={shasane.imageUrl} alt="Shasane" className="w-full h-full object-cover" />
              ) : (
                <div className="text-center p-4">
                  <ScrollText className="w-12 h-12 mx-auto text-slate-300 mb-2" />
                  <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Visual Reconstruction</p>
                </div>
              )}
              {shasane.status === 'damaged' && (
                <div className="absolute top-4 left-4 bg-red-600 text-white px-3 py-1 text-[10px] font-black rounded flex items-center gap-1 shadow-lg border border-red-500 uppercase tracking-tighter">
                  <AlertTriangle className="w-3 h-3" />
                  Damage Alert
                </div>
              )}
            </div>

            {/* Info Pills */}
            <div className="grid grid-cols-2 gap-3">
              <div className="bg-slate-50 p-3 rounded-lg border border-slate-100">
                <span className="block text-[10px] text-slate-400 uppercase font-bold text-slate-500">Dynasty</span>
                <span className="text-sm font-bold text-slate-700 flex items-center gap-1">
                  <MapPin className="w-3 h-3 text-blue-500" />
                  {shasane.epoch}
                </span>
              </div>
              <button 
                onClick={() => setLanguage(l => l === 'EN' ? 'KN' : 'EN')}
                className="bg-slate-50 p-3 rounded-lg border border-slate-100 text-left hover:bg-white hover:border-blue-200 transition group"
              >
                <span className="block text-[10px] text-slate-400 uppercase font-bold text-slate-500">Language</span>
                <span className="text-sm font-bold text-blue-600 flex items-center gap-1 group-hover:translate-x-1 transition-transform">
                  <Languages className="w-3 h-3" />
                  {language === 'EN' ? 'Kannada' : 'English'}
                </span>
              </button>
            </div>

            {/* Translation Body */}
            <div className="space-y-4">
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight leading-tight">
                {shasane.title}
              </h1>
              
              <div className="prose prose-slate prose-sm text-slate-600 leading-relaxed">
                <p className={`${language === 'KN' ? 'font-kannada text-lg leading-relaxed text-slate-800' : 'italic'}`}>
                  {language === 'KN' ? shasane.translationKannada : shasane.description}
                </p>
              </div>

              <div className="mt-8 pt-8 border-t border-slate-100 bg-slate-50/50 -mx-8 px-8 pb-8">
                <div className="flex items-center justify-between mb-4">
                  <h4 className="text-[10px] uppercase tracking-[0.2em] font-black text-slate-400">Historical Significance</h4>
                  {!aiStory && !loadingStory && (
                    <button 
                      onClick={generateStory}
                      className="text-[10px] font-bold text-blue-600 hover:text-blue-700 flex items-center gap-1 transition-colors px-2 py-1 bg-blue-50 rounded"
                    >
                      <Sparkles className="w-3 h-3" />
                      Expand Story
                    </button>
                  )}
                </div>
                
                {loadingStory && (
                  <div className="flex items-center gap-2 text-slate-400 py-2">
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span className="text-[11px] font-medium italic">Consulting ancient scrolls...</span>
                  </div>
                )}

                {aiStory ? (
                  <div className="text-xs text-slate-600 leading-relaxed animate-in fade-in slide-in-from-bottom-2 duration-500">
                    <p className="font-serif italic whitespace-pre-wrap">{aiStory}</p>
                  </div>
                ) : !loadingStory && (
                  <p className="text-xs text-slate-500 leading-relaxed">
                    The <strong>{shasane.title}</strong> is a primary historical document. It records royal decrees and societal norms of the <strong>{shasane.epoch}</strong>. Preserving these artifacts is essential for national heritage.
                  </p>
                )}
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="p-6 bg-white border-t border-slate-100">
            {reporting ? (
              <div className="space-y-3">
                <p className="text-sm font-bold text-red-600 flex items-center gap-2">
                  <AlertTriangle className="w-4 h-4" />
                  Confirm Danger Report?
                </p>
                <div className="flex gap-2">
                  <button 
                    onClick={handleReport}
                    className="flex-1 py-2.5 bg-red-600 text-white rounded text-sm font-bold hover:bg-red-700 transition shadow-md shadow-red-200"
                  >
                    Flag Location
                  </button>
                  <button 
                    onClick={() => setReporting(false)}
                    className="px-4 py-2.5 bg-slate-100 text-slate-600 rounded text-sm font-bold hover:bg-slate-200 transition"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
                <button 
                  onClick={() => setReporting(true)}
                  disabled={shasane.status === 'damaged'}
                  className={`w-full py-3 rounded text-sm flex items-center justify-center gap-2 font-bold transition shadow-sm ${
                    shasane.status === 'damaged' 
                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed border border-slate-200' 
                    : 'bg-slate-900 text-white hover:bg-slate-800 shadow-md shadow-slate-900/20'
                  }`}
                >
                  <AlertTriangle className="w-4 h-4" />
                  {shasane.status === 'damaged' ? 'Preservation Flagged' : 'Report Preservation Danger'}
                </button>
            )}
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
