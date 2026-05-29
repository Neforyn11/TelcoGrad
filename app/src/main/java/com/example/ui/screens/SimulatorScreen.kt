package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simulator.ModulationSimulator
import com.example.simulator.SimulationParams
import com.example.ui.components.TelcoHeader
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.WaveformGreen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SimulatorScreen(
    params: SimulationParams,
    onParamsChange: (SimulationParams) -> Unit,
    modifier: Modifier = Modifier
) {
    // Phase offset for continuous oscilloscope-style moving waveform
    val infiniteTransition = rememberInfiniteTransition(label = "OscilloscopePhase")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            TelcoHeader(
                title = "Lab Sinyal & Modulasi",
                subtitle = "Simulasi visual interaktif pembawa, informasi & modulasi telekomunikasi"
            )
        }

        // Oscilloscope Viewer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OSILOSKOP REAL-TIME",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.Red.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE FEED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Screen Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color(0xFF030712), shape = RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1F2937), shape = RoundedCornerShape(12.dp))
                    ) {
                        OscilloscopeCanvas(
                            params = params,
                            timePhase = phaseOffset
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(label = "Informasi m(t)", color = WaveformGreen)
                        LegendItem(label = "Pembawa c(t)", color = BrightBlue)
                        LegendItem(label = "Modulasi s(t)", color = ElectricCyan)
                    }
                }
            }
        }

        // Modulator Controls
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Konfigurasi Sinyal & Modulasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Type of Modulation Selector Buttons (AM, FM, BPSK)
                    Text(
                        text = "Tipe Skema Modulasi:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("AM", "FM", "BPSK").forEach { type ->
                            val selected = params.modulationType == type
                            Button(
                                onClick = { onParamsChange(params.copy(modulationType = type)) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 10.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Message Waveform Selector (Sinus, Square, Triangle)
                    if (params.modulationType != "BPSK") {
                        Text(
                            text = "Model Sinyal Informasi m(t):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Sinus", "Kotak", "Segitiga").forEach { type ->
                                val selected = params.messageType == type
                                OutlinedButton(
                                    onClick = { onParamsChange(params.copy(messageType = type)) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
                                        contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = type,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        // BPSK Digital Input Bitstream Editor
                        Text(
                            text = "Pola Bit Digital BPSK (1 atau 0):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = params.bpskBits,
                            onValueChange = { input ->
                                val filtered = input.filter { it == '1' || it == '0' }.take(8)
                                onParamsChange(params.copy(bpskBits = filtered))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            placeholder = { Text("Pola Bit Digital (cth: 10110)") },
                            trailingIcon = {
                                Text(
                                    text = "${params.bpskBits.length}/8 Bit",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Sliders
                    // Carrier Freq
                    Text(
                        text = "Frekuensi Pembawa f_c: ${params.carrierFreq.toInt()} Hz",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = params.carrierFreq,
                        onValueChange = { onParamsChange(params.copy(carrierFreq = it)) },
                        valueRange = 15f..60f,
                        colors = SliderDefaults.colors(thumbColor = BrightBlue, activeTrackColor = BrightBlue)
                    )

                    // Message Freq
                    if (params.modulationType != "BPSK") {
                        Text(
                            text = "Frekuensi Informasi f_m: ${String.format("%.1f", params.messageFreq)} Hz",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = params.messageFreq,
                            onValueChange = { onParamsChange(params.copy(messageFreq = it)) },
                            valueRange = 0.5f..5f,
                            colors = SliderDefaults.colors(thumbColor = WaveformGreen, activeTrackColor = WaveformGreen)
                        )
                    }

                    // Modulation Index m/beta
                    if (params.modulationType != "BPSK") {
                        val label = if (params.modulationType == "AM") "Indeks Modulasi AM (m)" else "Indeks Modulasi FM (β)"
                        Text(
                            text = "$label: ${String.format("%.2f", params.modIndex)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = params.modIndex,
                            onValueChange = { onParamsChange(params.copy(modIndex = it)) },
                            valueRange = 0.1f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                        )
                    }

                    // Additive Noise
                    Text(
                        text = "Simulasi Noise Saluran AWGN: ${String.format("%.1f", params.noiseLevel * 100)} %",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = params.noiseLevel,
                        onValueChange = { onParamsChange(params.copy(noiseLevel = it)) },
                        valueRange = 0.0f..0.4f,
                        colors = SliderDefaults.colors(thumbColor = Color.LightGray, activeTrackColor = Color.Gray)
                    )
                }
            }
        }

        // Constellation / Vector Diagram (Academic EE touch!)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Draw Interactive Constellation Graph on Quadrature Plane
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFF070B19), shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Grid axis
                            drawLine(Color(0xFF1E293B), Offset(0f, h / 2f), Offset(w, h / 2f), 1f)
                            drawLine(Color(0xFF1E293B), Offset(w / 2f, 0f), Offset(w / 2f, h), 1f)

                            // Points on I/Q diagram
                            val sizeConst = 6f
                            if (params.modulationType == "BPSK") {
                                // 2 constellation points for -1 and 1 on Real / I Axis
                                drawCircle(ElectricCyan, sizeConst, Offset(w / 4f, h / 2f)) // '0' Point
                                drawCircle(ElectricCyan, sizeConst, Offset(3 * w / 4f, h / 2f)) // '1' Point
                            } else if (params.modulationType == "FM") {
                                // FM sweeps phase continuously (circle loop)
                                drawCircle(
                                    ElectricCyan,
                                    radius = w / 3f,
                                    center = Offset(w / 2f, h / 2f),
                                    style = Stroke(1.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f))
                                )
                                // current sweeping point
                                val angle = (phaseOffset) % (2f * Math.PI.toFloat())
                                drawCircle(
                                    ElectricCyan,
                                    sizeConst,
                                    Offset(w / 2f + (w / 3f) * cos(angle), h / 2f + (h / 3f) * sin(angle))
                                )
                            } else {
                                // AM sweeps linear amplitude along axis
                                drawLine(ElectricCyan, Offset(w / 5f, h / 2f), Offset(4 * w / 5f, h / 2f), 2f)
                                val sweepVal = 1f + params.modIndex * sin(phaseOffset)
                                val normPos = w / 2f + (w / 4.5f) * (sweepVal / (1f + params.modIndex)) * (if (sin(phaseOffset * 8f) > 0) 1 else -1)
                                drawCircle(ElectricCyan, sizeConst, Offset(normPos, h / 2f))
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Diagram Konstelasi Signal Plane",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (params.modulationType) {
                                "BPSK" -> "Menampilkan diagram IQ BPSK dengan perpindahan fasa sudut 180° derajat (dua posisi polar biner diletakkan pada sumbu I)."
                                "FM" -> "Menampilkan vektor fasa FM berotasi melingkar kontinu dengan fasa berubah proporsional terhadap integral sinyal pesan."
                                "AM" -> "Amplitudo sinyal meluas proporsional dengan indeks modulasi m di sepanjang bidang horizontal I-plane."
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OscilloscopeCanvas(
    params: SimulationParams,
    timePhase: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Clean osilloscope grid (analog retro layout spec)
        val gridStepX = width / 10f
        val gridStepY = height / 6f
        for (i in 1..9) {
            drawLine(
                Color(0xFF1F2937),
                Offset(i * gridStepX, 0f),
                Offset(i * gridStepX, height),
                1f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f, 4f), 0f)
            )
        }
        for (j in 1..5) {
            drawLine(
                Color(0xFF1F2937),
                Offset(0f, j * gridStepY),
                Offset(width, j * gridStepY),
                1f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f, 4f), 0f)
            )
        }
        // Center Axes
        drawLine(Color(0xFF374151), Offset(0f, height / 2f), Offset(width, height / 2f), 1.5f)
        drawLine(Color(0xFF374151), Offset(width / 2f, 0f), Offset(width / 2f, height), 1.5f)

        // Wave plots
        // We plot 3 panels vertically:
        // Panel 1: Message m(t) [Top center]
        // Panel 2: Carrier c(t) [Middle center - smaller scale]
        // Panel 3: Modulated s(t) [Full screen layered or positioned nicely]

        val pointsCount = 300
        val messagePath = Path()
        val carrierPath = Path()
        val modulatedPath = Path()

        // Generate pseudo-noise profile using phase + index
        val noiseGenerator = Random(42)

        for (i in 0 until pointsCount) {
            val tFactor = i.toFloat() / pointsCount.toFloat() // 0.0 to 1.0 representing horizontal visual span
            // Include oscilloscope time translation
            val tWithPhase = tFactor + (timePhase / (2f * Math.PI.toFloat())) * 0.1f

            // Calculations
            val messageVal = ModulationSimulator.getMessageValue(tWithPhase, params)
            val carrierVal = ModulationSimulator.getCarrierValue(tWithPhase, params)

            val noiseOffset = if (params.noiseLevel > 0f) {
                (noiseGenerator.nextFloat() * 2f - 1f) * params.noiseLevel
            } else 0f
            val modulatedVal = ModulationSimulator.getModulatedValue(tWithPhase, params, noiseOffset)

            // Canvas coordinate mappings
            val x = tFactor * width

            // Panel Y coordinates (Panel heights = height / 4f)
            // 1. Message: top tier
            val yMsg = height / 4f - (messageVal * (height / 6f))

            // 2. Carrier: middle tier
            val yCarrier = (2.2f * height / 4f) - (carrierVal * (height / 10f))

            // 3. Modulated: bottom tier
            val yMod = (3.2f * height / 4f) - (modulatedVal * (height / 6f))

            if (i == 0) {
                messagePath.moveTo(x, yMsg)
                carrierPath.moveTo(x, yCarrier)
                modulatedPath.moveTo(x, yMod)
            } else {
                messagePath.lineTo(x, yMsg)
                carrierPath.lineTo(x, yCarrier)
                modulatedPath.lineTo(x, yMod)
            }
        }

        // Draw paths on OS screen
        drawPath(
            path = messagePath,
            color = WaveformGreen,
            style = Stroke(width = 2.5f)
        )
        drawPath(
            path = carrierPath,
            color = BrightBlue,
            style = Stroke(width = 1.5f)
        )
        drawPath(
            path = modulatedPath,
            color = ElectricCyan,
            style = Stroke(width = 3.0f)
        )
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp, 3.dp)
                .background(color, shape = RoundedCornerShape(1.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}
