package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.*
import com.example.ui.theme.*

import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    private val viewModel: CarVerifyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val modes = window.windowManager.defaultDisplay.supportedModes
            val highestRefreshRate = modes.maxByOrNull { it.refreshRate }
            if (highestRefreshRate != null && highestRefreshRate.refreshRate >= 90f) {
                val lp = window.attributes
                lp.preferredDisplayModeId = highestRefreshRate.modeId
                window.attributes = lp
            }
        }

        setContent {
            MyApplicationTheme {
                CarVerifyApp(viewModel)
            }
        }
    }
}

@Composable
fun CarVerifyApp(viewModel: CarVerifyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.Login) {
                Surface(color = BgColor, shadowElevation = 0.dp) {
                    Row(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(32.dp).background(Color.Transparent, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_carlore_logo), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(32.dp)) }
                            Spacer(Modifier.width(8.dp))
                            Text("CarLore", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                            Spacer(Modifier.width(8.dp))
                            Box(modifier = Modifier.background(PrimaryBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp)).border(1.dp, PrimaryBlue.copy(alpha=0.3f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("Verified", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(32.dp).border(1.dp, BorderColor, CircleShape), contentAlignment = Alignment.Center) { Text("🔔", fontSize = 14.sp) }
                            
                            Box {
                                var showProfileMenu by remember { mutableStateOf(false) }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .border(1.dp, BorderColor, CircleShape)
                                        .clickable { showProfileMenu = !showProfileMenu },
                                    contentAlignment = Alignment.Center
                                ) { 
                                    Text("👤", fontSize = 14.sp) 
                                }
                                
                                DropdownMenu(
                                    expanded = showProfileMenu,
                                    onDismissRequest = { showProfileMenu = false },
                                    modifier = Modifier.background(CardBg)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Guest User", color = TextPrimary, fontWeight = FontWeight.Bold) },
                                        onClick = { },
                                        leadingIcon = { Text("👤") }
                                    )
                                    HorizontalDivider(color = BorderColor)
                                    DropdownMenuItem(
                                        text = { Text(text = "Logout", color = DangerRed, fontWeight = FontWeight.Bold) },
                                        onClick = { 
                                            showProfileMenu = false
                                            viewModel.navigateTo(AppScreen.Login)
                                        },
                                        leadingIcon = { Text("🚪") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.Login) {
                Surface(color = BgColor, shadowElevation = 0.dp) {
                    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                        HorizontalDivider(color = BorderColor)
                        Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                            val navSearch = remember { { viewModel.navigateTo(AppScreen.Search) } }
                            val navReport = remember { { viewModel.navigateTo(AppScreen.Report) } }
                            val navCost = remember { { viewModel.navigateTo(AppScreen.CostEstimator) } }
                            val navBook = remember { { viewModel.navigateTo(AppScreen.Booking) } }
                            BottomTab("Search", "🔍", AppScreen.Search, currentScreen, navSearch)
                            BottomTab("Report", "📋", AppScreen.Report, currentScreen, navReport)
                            BottomTab("Costs", "⚡", AppScreen.CostEstimator, currentScreen, navCost)
                            BottomTab("Inspect", "👨‍🔧", AppScreen.Booking, currentScreen, navBook)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(BgColor)) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                val isWide = maxWidth > 420.dp
                val contMod = if(isWide) Modifier.width(420.dp).fillMaxHeight() else Modifier.fillMaxSize()
                
                Box(modifier = contMod.background(BgColor)) {
                    AnimatedContent(targetState = currentScreen, transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }, label = "screenTransition") { screen ->
                        when (screen) {
                            AppScreen.Login -> LoginScreen(viewModel)
                            AppScreen.Search -> SearchScreen(viewModel)
                            AppScreen.Report -> ReportScreen(viewModel)
                            AppScreen.CostEstimator -> CostEstimatorScreen(viewModel)
                            AppScreen.Booking -> InspectorBookingScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomTab(label: String, icon: String, screen: AppScreen, current: AppScreen, onClick: () -> Unit) {
    val isSel = screen == current
    val tc = if (isSel) PrimaryBlue else InactiveIcon
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    Box(modifier = Modifier.width(70.dp).fillMaxHeight().clickable { 
        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
        onClick() 
    }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Text(label, fontSize = 13.sp, color = tc, fontWeight = if(isSel) FontWeight.ExtraBold else FontWeight.SemiBold)
        }
        if (isSel) { Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(2.dp).background(PrimaryBlue)) }
    }
}
