package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TelcoHeader

@Composable
fun DashboardScreen(
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            TelcoHeader(
                title = "Beranda Rekan",
                subtitle = "Pusat kesiapan karir & simulasi nirkabel lulusan Telekomunikasi"
            )
        }

        // Welcome Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Edu",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Selamat Datang, Fresh Graduate!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "S1 Teknik Elektro S.T. Telekomunikasi",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Selamat atas penyelesaian matrikulasi akademis Anda! Industri Telekomunikasi modern (5G NR, Fiber Optik, High-Altitude Platforms, IoT) menanti keahlian Anda. Aplikasi TelcoGrad Workspace ini dirancang khusus untuk memadukan analisis teoretis dengan simulasi praktis langsung di genggaman Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Skill Readiness Section
        item {
            Text(
                text = "Indeks Kesiapan Karir Telekomunikasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkillProgressBar(name = "RF & Link Budget Engineering", progress = 0.85f, level = "Ambis / Siap Kerja")
                SkillProgressBar(name = "Teori Modulasi & DSP Visual", progress = 0.90f, level = "Sangat Kuat")
                SkillProgressBar(name = "Analisis Standar 3GPP (5G/6G)", progress = 0.70f, level = "Menengah")
                SkillProgressBar(name = "Fiber Optic & GPON Planning", progress = 0.65f, level = "Siap Diuji")
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = "Modul Terintegrasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Feature 1 -> Simulator Sinyal
                Box(modifier = Modifier.weight(1f)) {
                    FeatureLaunchCard(
                        title = "Simulator Sinyal",
                        desc = "Eksplorasi AM, FM, BPSK secara visual real-time",
                        icon = Icons.Default.GraphicEq,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTab(1) }
                    )
                }
                // Feature 2 -> Link Budget
                Box(modifier = Modifier.weight(1f)) {
                    FeatureLaunchCard(
                        title = "RF & Link Budget",
                        desc = "Hitung FSPL dan margin kelayakan site",
                        icon = Icons.Default.SettingsInputAntenna,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onNavigateToTab(2) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Feature 3 -> Advisor AI
                Box(modifier = Modifier.weight(1f)) {
                    FeatureLaunchCard(
                        title = "AI Telco Coach",
                        desc = "Konsultasi 3GPP, karir, & simulasi wawancara",
                        icon = Icons.Default.Psychology,
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onNavigateToTab(3) }
                    )
                }
                // Feature 4 -> Formula Desk
                Box(modifier = Modifier.weight(1f)) {
                        FeatureLaunchCard(
                            title = "Pustaka Rumus",
                            desc = "Teorema Shannon, konverter dBm & unit praktis",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            accentColor = MaterialTheme.colorScheme.error,
                            onClick = { onNavigateToTab(4) }
                        )
                }
            }
        }

        // Industry Notes (Offline Informative Cards)
        item {
            Text(
                text = "Rekomendasi Riset & Tren 2026",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5G Advance / 5.5G (Release 18 & 19)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Standar baru ini memperkenalkan penyempurnaan AI/ML bawaan pada interface radio guna otomatisasi beamforming gila-gilaan, perbaikan MIMO masif, serta perluasan IoT satelit (Non-Terrestrial Network). Pahami regulasi alokasi frekuensi di Indonesia (C-band 3.5 GHz dan d-band 26 GHz).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Teknologi FTTx & Pembagian Bandwidth GPON",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fiber to the Home (FTTH) sangat kritikal untuk cellular backhaul 5G. GPON memiliki rasio pembagian standar 1:64 dengan bandwidth downstream 2.488 Gbps dan upstream 1.244 Gbps menggunakan panjang gelombang 1490nm/1310nm. Sangat penting bagi RF/Network Engineer baru memahami peredaman serat optik (0.2 dB/km pada 1550nm).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SkillProgressBar(
    name: String,
    progress: Float,
    level: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = level,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)),
                color = if (progress > 0.8f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun FeatureLaunchCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}
