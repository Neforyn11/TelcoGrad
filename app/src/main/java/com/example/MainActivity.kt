package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SettingsInputAntenna
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdvisorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FormulaScreen
import com.example.ui.screens.LinkBudgetScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TelcoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize the centralized viewmodel using Compose VM provider
                val viewModel: TelcoViewModel = viewModel()
                val currentTab by viewModel.currentTab.collectAsState()
                val simParams by viewModel.simParams.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            tonalElevation = 10.dp
                        ) {
                            // Tab 0: Beranda
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.selectTab(0) },
                                label = { Text("Beranda", fontSize = 10.sp) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Beranda"
                                    )
                                }
                            )

                            // Tab 1: Sinyal Modulasi Lab
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.selectTab(1) },
                                label = { Text("Visual Lab", fontSize = 10.sp) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 1) Icons.Filled.GraphicEq else Icons.Outlined.GraphicEq,
                                        contentDescription = "Sinyal Lab"
                                    )
                                }
                            )

                            // Tab 2: Link Budget Calculator
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.selectTab(2) },
                                label = { Text("Link Budget", fontSize = 10.sp) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 2) Icons.Filled.SettingsInputAntenna else Icons.Outlined.SettingsInputAntenna,
                                        contentDescription = "Link Budget"
                                    )
                                }
                            )

                            // Tab 3: Advisor AI
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { viewModel.selectTab(3) },
                                label = { Text("Coach AI", fontSize = 10.sp) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 3) Icons.Filled.Psychology else Icons.Outlined.Psychology,
                                        contentDescription = "Coach AI"
                                    )
                                }
                            )

                            // Tab 4: Formula & Reference Handout
                            NavigationBarItem(
                                selected = currentTab == 4,
                                onClick = { viewModel.selectTab(4) },
                                label = { Text("Pustaka", fontSize = 10.sp) },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 4) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                                        contentDescription = "Pustaka"
                                    )
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally { width -> (width * 0.12f).toInt() } + fadeIn(animationSpec = tween(220, delayMillis = 60))) togetherWith
                                            (slideOutHorizontally { width -> -(width * 0.12f).toInt() } + fadeOut(animationSpec = tween(220)))
                                } else {
                                    (slideInHorizontally { width -> -(width * 0.12f).toInt() } + fadeIn(animationSpec = tween(220, delayMillis = 60))) togetherWith
                                            (slideOutHorizontally { width -> (width * 0.12f).toInt() } + fadeOut(animationSpec = tween(220)))
                                }.using(
                                    SizeTransform(clip = false)
                                )
                            },
                            label = "TabTransition"
                        ) { targetTab ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                when (targetTab) {
                                    0 -> DashboardScreen(
                                        onNavigateToTab = { viewModel.selectTab(it) }
                                    )
                                    1 -> SimulatorScreen(
                                        params = simParams,
                                        onParamsChange = { viewModel.updateSimParams(it) }
                                    )
                                    2 -> LinkBudgetScreen(
                                        viewModel = viewModel
                                    )
                                    3 -> AdvisorScreen(
                                        viewModel = viewModel
                                    )
                                    4 -> FormulaScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
