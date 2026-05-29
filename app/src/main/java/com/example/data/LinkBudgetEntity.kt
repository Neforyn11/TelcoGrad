package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "link_budgets")
data class LinkBudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val frequency: Double, // GHz
    val distance: Double,  // km
    val txPower: Double,   // dBm
    val txGain: Double,    // dBi
    val rxGain: Double,    // dBi
    val losses: Double,    // dB
    val fspl: Double,      // dB
    val rxPower: Double,   // dBm
    val isFeasible: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
