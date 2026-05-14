package com.nammashasane.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.nammashasane.app.data.Inscription
import com.nammashasane.app.data.InscriptionDatabase
import com.nammashasane.app.data.InscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InscriptionRepository
    
    private val _inscriptions = MutableStateFlow<List<Inscription>>(emptyList())
    val inscriptions: StateFlow<List<Inscription>> = _inscriptions.asStateFlow()
    
    private val _selectedInscription = MutableStateFlow<Inscription?>(null)
    val selectedInscription: StateFlow<Inscription?> = _selectedInscription.asStateFlow()
    
    private val _aiStory = MutableStateFlow("")
    val aiStory: StateFlow<String> = _aiStory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Using a placeholder API key. In a real app, this should be in local.properties or a secure backend.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_GEMINI_API_KEY_HERE" 
    )

    init {
        val dao = InscriptionDatabase.getDatabase(application).inscriptionDao()
        repository = InscriptionRepository(dao)
        
        viewModelScope.launch {
            repository.allInscriptions.collect {
                _inscriptions.value = it
            }
        }
    }

    fun selectInscription(inscription: Inscription) {
        _selectedInscription.value = inscription
        generateAIStory(inscription)
    }

    private fun generateAIStory(inscription: Inscription) {
        _isLoading.value = true
        _aiStory.value = ""
        
        viewModelScope.launch {
            try {
                val prompt = """
                    Generate a fascinating 3-paragraph historical story about the following ancient Karnataka inscription:
                    Title: ${inscription.title}
                    Dynasty: ${inscription.dynasty}
                    Period: ${inscription.period}
                    Location: ${inscription.location}
                    Description: ${inscription.description}
                    
                    The story should mention the likely king or ruler, the context of the gift or law mentioned, and why this piece of history is important for modern Karnataka. Use an engaging and informative tone.
                """.trimIndent()
                
                val response = generativeModel.generateContent(prompt)
                _aiStory.value = response.text ?: inscription.description
            } catch (e: Exception) {
                _aiStory.value = "Unable to generate AI story. Original record: ${inscription.description}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addInscription(inscription: Inscription) {
        viewModelScope.launch {
            repository.insert(inscription)
        }
    }

    fun markAsDamaged(inscription: Inscription) {
        viewModelScope.launch {
            repository.update(inscription.copy(isDamaged = true))
        }
    }

    fun clearSelection() {
        _selectedInscription.value = null
        _aiStory.value = ""
    }
}
