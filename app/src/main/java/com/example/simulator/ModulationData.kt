package com.example.simulator

import kotlin.math.sin

data class SimulationParams(
    val carrierFreq: Float = 30f,    // Hz (visual scaled)
    val messageFreq: Float = 2f,     // Hz (visual scaled)
    val modIndex: Float = 0.8f,      // m (AM) or beta (FM)
    val noiseLevel: Float = 0.0f,    // visual noise level
    val messageType: String = "Sinus", // Sinus, Kotak, Segitiga
    val modulationType: String = "AM", // AM, FM, BPSK
    val bpskBits: String = "10110"
)

object ModulationSimulator {
    fun getMessageValue(t: Float, params: SimulationParams): Float {
        val fm = params.messageFreq
        return when (params.messageType) {
            "Kotak" -> {
                val cycle = (t * fm) % 1.0f
                if (cycle < 0.5f) 1.0f else -1.0f
            }
            "Segitiga" -> {
                val cycle = (t * fm) % 1.0f
                if (cycle < 0.5f) {
                    4.0f * cycle - 1.0f
                } else {
                    3.0f - 4.0f * cycle
                }
            }
            else -> { // "Sinus"
                sin(2.0 * Math.PI * fm * t).toFloat()
            }
        }
    }

    fun getCarrierValue(t: Float, params: SimulationParams): Float {
        val fc = params.carrierFreq
        return sin(2.0 * Math.PI * fc * t).toFloat()
    }

    fun getModulatedValue(t: Float, params: SimulationParams, noiseVal: Float = 0f): Float {
        val fc = params.carrierFreq
        val fm = params.messageFreq
        val m = params.modIndex

        val baseVal = when (params.modulationType) {
            "AM" -> {
                // s(t) = (1 + m * message(t)) * carrier(t)
                val msg = getMessageValue(t, params)
                (1.0f + m * msg) * sin(2.0 * Math.PI * fc * t).toFloat()
            }
            "FM" -> {
                // s(t) = sin(2 * pi * fc * t + beta * message_integral(t))
                // For a sinus message, integral is -cos, so we can approximate with direct sine phase shift:
                // s(t) = sin(2 * pi * fc * t + beta * sin(2 * pi * fm * t))
                val phaseOffset = m * sin(2.0 * Math.PI * fm * t).toFloat()
                sin(2.0 * Math.PI * fc * t + phaseOffset).toFloat()
            }
            "BPSK" -> {
                // Bit index based on time t divided into chunks matching the string length
                val bits = params.bpskBits
                if (bits.isEmpty()) {
                    sin(2.0 * Math.PI * fc * t).toFloat()
                } else {
                    val nBits = bits.length
                    val bitIndex = ((t * nBits).toInt()).coerceIn(0, nBits - 1)
                    val bit = bits[bitIndex]
                    val phase = if (bit == '1') 0f else Math.PI.toFloat()
                    sin(2.0 * Math.PI * fc * t + phase).toFloat()
                }
            }
            else -> getCarrierValue(t, params)
        }

        return baseVal + noiseVal
    }
}
