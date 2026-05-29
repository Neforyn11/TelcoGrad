package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.AppDatabase
import com.example.data.LinkBudgetEntity
import com.example.simulator.SimulationParams
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class TelcoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val linkBudgetDao = db.linkBudgetDao()

    // --- Navigation State ---
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // --- Signal Simulator State ---
    private val _simParams = MutableStateFlow(SimulationParams())
    val simParams: StateFlow<SimulationParams> = _simParams.asStateFlow()

    fun updateSimParams(params: SimulationParams) {
        _simParams.value = params
    }

    // --- Link Budget Calculator Input States ---
    val linkTitle = MutableStateFlow("Microwave Link Jabodetabek")
    val frequencyGHz = MutableStateFlow("7.0")      // Standard microwave freq, e.g. 7 GHz
    val distanceKm = MutableStateFlow("15.0")       // 15 km distance
    val txPowerDbm = MutableStateFlow("20.0")       // 20 dBm (100 mW)
    val txGainDbi = MutableStateFlow("35.0")        // 35 dBi parabolic antenna gain
    val rxGainDbi = MutableStateFlow("35.0")        // 35 dBi Rx physical antenna gain
    val lossesDb = MutableStateFlow("3.5")          // feed and branching losses
    val rxSensitivityDbm = MutableStateFlow("-75.0") // Threshold for QAM modulation

    // Calculated fields state
    private val _calcFspl = MutableStateFlow(0.0)
    val calcFspl: StateFlow<Double> = _calcFspl.asStateFlow()

    private val _calcRxPower = MutableStateFlow(0.0)
    val calcRxPower: StateFlow<Double> = _calcRxPower.asStateFlow()

    private val _calcFadeMargin = MutableStateFlow(0.0)
    val calcFadeMargin: StateFlow<Double> = _calcFadeMargin.asStateFlow()

    private val _isFeasible = MutableStateFlow(true)
    val isFeasible: StateFlow<Boolean> = _isFeasible.asStateFlow()

    // Room DB Flow for link budgets list
    val savedLinkBudgets: StateFlow<List<LinkBudgetEntity>> = kotlinx.coroutines.flow.flow {
        linkBudgetDao.getAllLinkBudgets().collect { emit(it) }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        recalculateLinkBudget()
    }

    fun recalculateLinkBudget() {
        val freq = frequencyGHz.value.toDoubleOrNull() ?: 1.0
        val dist = distanceKm.value.toDoubleOrNull() ?: 1.0
        val txP = txPowerDbm.value.toDoubleOrNull() ?: 0.0
        val txG = txGainDbi.value.toDoubleOrNull() ?: 0.0
        val rxG = rxGainDbi.value.toDoubleOrNull() ?: 0.0
        val loss = lossesDb.value.toDoubleOrNull() ?: 0.0
        val sens = rxSensitivityDbm.value.toDoubleOrNull() ?: -80.0

        // Free Space Path Loss (FSPL) standard cellular/microwave link formula:
        // FSPL (dB) = 92.44 + 20 log10(d_km) + 20 log10(f_GHz)
        val logD = Math.log10(dist.coerceAtLeast(0.001))
        val logF = Math.log10(freq.coerceAtLeast(0.001))
        val fsplVal = 92.44 + (20.0 * logD) + (20.0 * logF)

        // Rx Power (dBm) = Tx Power + Tx Gain + Rx Gain - FSPL - Other Losses
        val rxPVal = txP + txG + rxG - fsplVal - loss

        // Fade Margin = Rx Power - Receiver Sensitivity Threshold
        val margin = rxPVal - sens
        val feasibleVal = margin >= 0.0

        _calcFspl.value = fsplVal
        _calcRxPower.value = rxPVal
        _calcFadeMargin.value = margin
        _isFeasible.value = feasibleVal
    }

    fun saveCurrentLinkBudget() {
        viewModelScope.launch {
            val entity = LinkBudgetEntity(
                title = linkTitle.value.ifBlank { "Unscheduled Microwave Link" },
                frequency = frequencyGHz.value.toDoubleOrNull() ?: 1.0,
                distance = distanceKm.value.toDoubleOrNull() ?: 1.0,
                txPower = txPowerDbm.value.toDoubleOrNull() ?: 0.0,
                txGain = txGainDbi.value.toDoubleOrNull() ?: 0.0,
                rxGain = rxGainDbi.value.toDoubleOrNull() ?: 0.0,
                losses = lossesDb.value.toDoubleOrNull() ?: 0.0,
                fspl = _calcFspl.value,
                rxPower = _calcRxPower.value,
                isFeasible = _isFeasible.value
            )
            linkBudgetDao.insertLinkBudget(entity)
        }
    }

    fun deleteLinkBudget(entity: LinkBudgetEntity) {
        viewModelScope.launch {
            linkBudgetDao.deleteLinkBudget(entity)
        }
    }

    // --- AI Advisor Chat State ---
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            content = "Halo Rekan Engineer! Selamat atas kelulusan Anda di Teknik Elektro Telekomunikasi. Saya adalah Asisten AI Karir & Teknis Anda.\n\n" +
                    "Gunakan saran pintas di bawah ini atau ketik pertanyaan seputar perencanaan RF, standar 3GPP, Fiber Optik, atau persiapan wawancara!",
            isUser = false
        )
    ))
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun sendChatMessage(message: String) {
        if (message.isBlank()) return

        // 1. Add user message
        val newUserMsg = ChatMessage(content = message, isUser = true)
        _chatHistory.value = _chatHistory.value + newUserMsg

        // 2. Call AI with system instructions representing a senior telecommunications executive / mentor
        viewModelScope.launch {
            _isAiLoading.value = true
            val systemInstruction = "Anda adalah Dr. Sasmita, seorang pakar senior rekayasa telekomunikasi, penasihat akademis, dan pewawancara teknis senior di operator seluler terkemuka. " +
                    "Tugas Anda adalah menasihati, membimbing, dan melatih seorang lulusan baru (fresh graduate) S1 Teknik Elektro Telekomunikasi agar siap kerja dan memahami konsep RF Planning, 5G NR, Fiber Optik, Link Budget, dan standar 3GPP. " +
                    "Berikan jawaban teknis yang presisi, logis, menyemangati, dan praktis dalam Bahasa Indonesia. Format penjelasan Anda dengan bullet-points agar rapi dan mudah dicerna."

            val aiResponse = GeminiClient.generateResponse(prompt = message, systemInstruction = systemInstruction)
            val newAiMsg = ChatMessage(content = aiResponse, isUser = false)
            _chatHistory.value = _chatHistory.value + newAiMsg
            _isAiLoading.value = false
        }
    }

    fun clearChat() {
        _chatHistory.value = listOf(
            ChatMessage(
                content = "Konsultasi dikosongkan. Silakan ketik pertanyaan baru atau gunakan tombol jalan pintas di bawah!",
                isUser = false
            )
        )
    }
}
