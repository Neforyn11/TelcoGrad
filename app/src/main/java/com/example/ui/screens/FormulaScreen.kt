package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TelcoHeader
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.WaveformGreen

@Composable
fun FormulaScreen(
    modifier: Modifier = Modifier
) {
    // Interactive dBm-Watts converter state
    var dbmInput by remember { mutableStateOf("30.0") }
    var wattsOutput by remember { mutableStateOf("1.0") }

    var wattsInput by remember { mutableStateOf("2.0") }
    var dbmOutput by remember { mutableStateOf("33.01") }

    // Logic dbm to watts: P_mW = 10^(P_dBm / 10) -> P_W = P_mW / 1000
    fun convertDbmToWatts(dbmStr: String) {
        val dbm = dbmStr.toDoubleOrNull()
        if (dbm != null) {
            val mW = Math.pow(10.0, dbm / 10.0)
            val w = mW / 1000.0
            wattsOutput = if (w < 0.001) {
                String.format("%.3e", w)
            } else {
                String.format("%.4f", w)
            }
        } else {
            wattsOutput = "--"
        }
    }

    // Logic watts to dbm: P_mW = P_W * 1000 -> P_dBm = 10 * log10(P_mW)
    fun convertWattsToDbm(wattsStr: String) {
        val w = wattsStr.toDoubleOrNull()
        if (w != null && w > 0.0) {
            val mW = w * 1000.0
            val dbm = 10.0 * Math.log10(mW)
            dbmOutput = String.format("%.2f", dbm)
        } else {
            dbmOutput = "--"
        }
    }

    // Trigger initial conversions
    LaunchedEffect(Unit) {
        convertDbmToWatts(dbmInput)
        convertWattsToDbm(wattsInput)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            TelcoHeader(
                title = "Pustaka Rumus & Konverter",
                subtitle = "Buku saku praktis telekomunikasi dengan modul konversi daya interaktif"
            )
        }

        // 1. Dbm-Watts Converter Utility Card (Awesome Engineering Component!)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Konverter Daya RF Interaktif",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Konversi level daya radio gelombang mikro antara unit absolut (Watt) dan unit logaritmik (dBm).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section A: dBm to Watts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = dbmInput,
                            onValueChange = {
                                dbmInput = it
                                convertDbmToWatts(it)
                            },
                            label = { Text("Daya (dBm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                Text("Konversi absolut", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text("$wattsOutput Watt", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section B: Watts to dBm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = wattsInput,
                            onValueChange = {
                                wattsInput = it
                                convertWattsToDbm(it)
                            },
                            label = { Text("Daya (Watt)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                                Text("Konversi logaritmik", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text("$dbmOutput dBm", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ElectricCyan)
                            }
                        }
                    }
                }
            }
        }

        // Formula 2: Shannon-Hartley Capacity Theorem Card
        item {
            FormulaCard(
                title = "Teorema Kapasitas Shannon-Hartley",
                formulaCode = "C = B log2(1 + SNR)",
                desc = "Teorema fundamental limitasi transmisi data nirkabel. Menentukan kapasitas saluran komunikasi nirkabel bebas-error maksimum (throughput data) berdasarkan lebar pita frekuensi (Bandwidth) dan Signal-to-Noise Ratio (SNR).",
                parameters = listOf(
                    "C: Kapasitas Saluran Maksimum (bps - bits per second)",
                    "B: Bandwidth Saluran Transmisi (Hz)",
                    "SNR: Perbandingan Sinyal Terhadap Noise (bentuk linear rasio, bukan dB)"
                ),
                applicationNote = "Jika bandwidth 20 MHz (cth: LTE) dengal level SNR linear 31 (setara 15 dB), kapasitas maksimum teoretis adalah C = 20 * 10^6 * log2(1 + 31) = 20 * 5 = 100 Mbps."
            )
        }

        // Formula 3: Friis Transmission Equation Card
        item {
            FormulaCard(
                title = "Persamaan Transmisi Friis (RF Power Link)",
                formulaCode = "Pr = Pt + Gt + Gr - FSPL",
                desc = "Menghitung tingkat daya terima (Pr) pada saluran propagasi gelombang mikro ruang hampa line-of-sight secara langsung.",
                parameters = listOf(
                    "Pr: Daya Terima di terminal Rx (dBm)",
                    "Pt: Daya Transmiter yang dipancarkan (dBm)",
                    "Gt / Gr: Gain Antena Pemancar / Penerima (dBi)",
                    "FSPL: Redaman Ruang Bebas (dB), bertambah seiring membesarnya jarak d dan frekuensi f"
                ),
                applicationNote = "Persamaan ini melandasi kalkulator Link Budget yang kita gunakan di Tab ke-2. Di dunia nyata, Pr harus dikurangi rugi-rugi kabel, rugi redaman hujan atmosferik, dan rugi polarisasi silang."
            )
        }

        // Formula 4: Nilai Frekuensi Seluler Terkemuka (Indonesia)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WaveformGreen, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Referensi Alokasi Spektrum Seluler",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Spektrum frekuensi operator nirkabel nasional Indonesia yang wajib diketahui lulusan baru:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TextBulletLine(text = "Carrier Low-Band (700 MHz / 900 MHz): Spektrum jaminan cakupan wilayah rural (ekspansi jangkauan jauh).")
                    TextBulletLine(text = "Carrier Mid-Band (1.8 GHz, 2.1 GHz, 2.3 GHz): Spektrum andalan kapasitas penduduk padat (LTE & 5G standar).")
                    textSpacing()
                    TextBulletLine(text = "Carrier High-Band (3.5 GHz - C Band / 26 GHz - Millimeter Wave): Spektrum 5G Standalone dengan latensi ultra-rendah dan throughput gila 1-5 Gbps (untuk industri privat/smart factory).")
                }
            }
        }
    }
}

@Composable
fun FormulaCard(
    title: String,
    formulaCode: String,
    desc: String,
    parameters: List<String>,
    applicationNote: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Formula Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formulaCode,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Keterangan Variabel:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            parameters.forEach { param ->
                TextBulletLine(text = param)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Studi Kasus Pintas:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = WaveformGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = applicationNote,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun TextBulletLine(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("•", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Black, color = ElectricCyan)
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 16.sp
        )
    }
}

@Composable
fun textSpacing() {
    Spacer(modifier = Modifier.height(4.dp))
}
