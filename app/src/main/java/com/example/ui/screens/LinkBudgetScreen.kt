package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LinkBudgetEntity
import com.example.ui.components.MetricDisplay
import com.example.ui.components.TelcoHeader
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.WaveformGreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.viewmodel.TelcoViewModel

@Composable
fun LinkBudgetScreen(
    viewModel: TelcoViewModel,
    modifier: Modifier = Modifier
) {
    val linkTitle by viewModel.linkTitle.collectAsState()
    val frequencyGHz by viewModel.frequencyGHz.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val txPowerDbm by viewModel.txPowerDbm.collectAsState()
    val txGainDbi by viewModel.txGainDbi.collectAsState()
    val rxGainDbi by viewModel.rxGainDbi.collectAsState()
    val lossesDb by viewModel.lossesDb.collectAsState()
    val rxSensitivityDbm by viewModel.rxSensitivityDbm.collectAsState()

    val fspl by viewModel.calcFspl.collectAsState()
    val rxPower by viewModel.calcRxPower.collectAsState()
    val fadeMargin by viewModel.calcFadeMargin.collectAsState()
    val isFeasible by viewModel.isFeasible.collectAsState()

    val savedLinks by viewModel.savedLinkBudgets.collectAsState()

    var showHistory by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            TelcoHeader(
                title = "Lab Link Budget & FSPL",
                subtitle = "Analisis propagasi microwave line-of-sight & perhitungan redaman ruang bebas"
            )
        }

        // Output Result Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Feasible Status Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFeasible) WaveformGreen.copy(alpha = 0.08f) else ErrorRed.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFeasible) WaveformGreen.copy(alpha = 0.4f) else ErrorRed.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isFeasible) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isFeasible) WaveformGreen else ErrorRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = if (isFeasible) "LINK LAYAK (FEASIBLE)" else "LINK GAGAL (MARGIN REDUP)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isFeasible) WaveformGreen else ErrorRed
                            )
                            Text(
                                text = if (isFeasible) {
                                    "Level daya terima (${String.format("%.1f", rxPower)} dBm) melampaui sensitivitas penerima dengan fade margin aman ${String.format("%.1f", fadeMargin)} dB."
                                } else {
                                    "Level daya terima terlalu rendah. Naikkan daya pancar, gain antena, atau perkecil rugi-rugi kabel."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MetricDisplay(
                            label = "FSPL Redaman",
                            value = String.format("%.1f", fspl),
                            unit = "dB"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        MetricDisplay(
                            label = "Rx Daya Terima",
                            value = String.format("%.1f", rxPower),
                            unit = "dBm",
                            color = if (isFeasible) WaveformGreen else ErrorRed
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        MetricDisplay(
                            label = "Fade Margin",
                            value = String.format("%.1f", fadeMargin),
                            unit = "dB",
                            color = if (fadeMargin >= 15.0) WaveformGreen else if (fadeMargin >= 0.0) Color(0xFFF59E0B) else ErrorRed
                        )
                    }
                }
            }
        }

        // Link Visual Diagram
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DIAGRAM BLOK MODEL PROPAGASI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SettingsInputAntenna, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(32.dp))
                            Text("TX TOWER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("$txPowerDbm dBm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }

                        // Propagation path
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "FSPL: ${String.format("%.1f", fspl)} dB",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f).height(1.5.dp).background(Color.Gray))
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = ElectricCyan.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                Box(modifier = Modifier.weight(1f).height(1.5.dp).background(Color.Gray))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jarak: $distanceKm km",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SettingsInputAntenna, contentDescription = null, tint = if (isFeasible) WaveformGreen else ErrorRed, modifier = Modifier.size(32.dp))
                            Text("RX TOWER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", rxPower)} dBm", style = MaterialTheme.typography.bodySmall, color = if (isFeasible) WaveformGreen else ErrorRed)
                        }
                    }
                }
            }
        }

        // Toggle Buttons list or form
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showHistory = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showHistory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (!showHistory) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Parameter Link")
                }

                Button(
                    onClick = { showHistory = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showHistory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (showHistory) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Riwayat (${savedLinks.size})")
                }
            }
        }

        if (!showHistory) {
            // Form Parameters Editor
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sunting Desain Microwave Link",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Title/Identitas Link
                        OutlinedTextField(
                            value = linkTitle,
                            onValueChange = {
                                viewModel.linkTitle.value = it
                                viewModel.recalculateLinkBudget()
                            },
                            label = { Text("Identitas / Deskripsi Link") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Frekuensi input
                            OutlinedTextField(
                                value = frequencyGHz,
                                onValueChange = {
                                    viewModel.frequencyGHz.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Frekuensi") },
                                suffix = { Text("GHz") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            // Jarak input
                            OutlinedTextField(
                                value = distanceKm,
                                onValueChange = {
                                    viewModel.distanceKm.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Jarak Link") },
                                suffix = { Text("km") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Tx Power
                            OutlinedTextField(
                                value = txPowerDbm,
                                onValueChange = {
                                    viewModel.txPowerDbm.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Tx Daya Pancar") },
                                suffix = { Text("dBm") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            // losses
                            OutlinedTextField(
                                value = lossesDb,
                                onValueChange = {
                                    viewModel.lossesDb.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Rugi-Rugi Kabel") },
                                suffix = { Text("dB") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Tx Gain
                            OutlinedTextField(
                                value = txGainDbi,
                                onValueChange = {
                                    viewModel.txGainDbi.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Tx Ant Gain") },
                                suffix = { Text("dBi") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            // Rx Gain
                            OutlinedTextField(
                                value = rxGainDbi,
                                onValueChange = {
                                    viewModel.rxGainDbi.value = it
                                    viewModel.recalculateLinkBudget()
                                },
                                label = { Text("Rx Ant Gain") },
                                suffix = { Text("dBi") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Receiver Sensitivity
                        OutlinedTextField(
                            value = rxSensitivityDbm,
                            onValueChange = {
                                viewModel.rxSensitivityDbm.value = it
                                viewModel.recalculateLinkBudget()
                            },
                            label = { Text("Receiver Sensitivity Threshold") },
                            suffix = { Text("dBm") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            supportingText = { Text("Batas daya minimum penerimaan (sensitivitas radio).", fontSize = 10.sp) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        Button(
                            onClick = { viewModel.saveCurrentLinkBudget() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan Hasil ke Database")
                        }
                    }
                }
            }
        } else {
            // Saved Calculations list (Room Persistence)
            if (savedLinks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada riwayat perhitungan disimpan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                items(savedLinks) { link ->
                    SavedLinkCard(
                        link = link,
                        onDeleteClick = { viewModel.deleteLinkBudget(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedLinkCard(
    link: LinkBudgetEntity,
    onDeleteClick: (LinkBudgetEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = link.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Frek: ${link.frequency} GHz | Jarak: ${link.distance} km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            if (link.isFeasible) WaveformGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (link.isFeasible) "FEASIBLE" else "DOWN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (link.isFeasible) WaveformGreen else ErrorRed,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Redaman FSPL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text("${String.format("%.1f", link.fspl)} dB", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Daya Terima", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    Text("${String.format("%.1f", link.rxPower)} dBm", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (link.isFeasible) WaveformGreen else ErrorRed)
                }
                IconButton(
                    onClick = { onDeleteClick(link) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = ErrorRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
