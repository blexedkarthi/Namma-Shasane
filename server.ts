import express from 'express';
import path from 'path';
import { createServer as createViteServer } from 'vite';
import { GoogleGenAI } from '@google/genai';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const PORT = 3000;

app.use(express.json());

// Initialize Gemini
const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,
  httpOptions: {
    headers: {
      'User-Agent': 'aistudio-build',
    }
  }
});

// API Routes
app.post('/api/decode', async (req, res) => {
  try {
    const prompt = `Act as an expert epigraphist specializing in Karnataka inscriptions. 
      Analyze the provided context (or image if available) and generate a historical inscription record.
      Return valid JSON with fields: title, description (English history), translationKannada, epoch (Dynasty), and a short fun fact.
      Make it feel authentic to Karnataka's heritage.`;

    const response = await ai.models.generateContent({
      model: "gemini-3-flash-preview",
      contents: prompt,
      config: {
        responseMimeType: "application/json"
      }
    });

    const text = response.text;
    if (!text) throw new Error("No response from Gemini");
    
    const data = JSON.parse(text);
    res.json(data);
  } catch (error) {
    console.error('Error in /api/decode:', error);
    res.status(500).json({ error: 'Failed to decode inscription' });
  }
});

app.post('/api/story', async (req, res) => {
  const { title, dynasty, period, location, description } = req.body;
  try {
    const prompt = `
      Generate a fascinating 3-paragraph historical story about the following ancient Karnataka inscription:
      Title: ${title}
      Dynasty: ${dynasty}
      Period: ${period}
      Location: ${location}
      Description: ${description}
      
      The story should mention the likely king or ruler, the context of the gift or law mentioned, and why this piece of history is important for modern Karnataka. Use an engaging and informative tone.
    `;
    
    const response = await ai.models.generateContent({
      model: "gemini-3-flash-preview",
      contents: prompt
    });

    res.json({ story: response.text });
  } catch (error) {
    console.error('Error in /api/story:', error);
    res.status(500).json({ error: 'Failed to generate story' });
  }
});

async function start() {
  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

start();
