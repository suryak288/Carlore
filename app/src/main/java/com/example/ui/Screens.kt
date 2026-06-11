package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.example.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import com.example.ui.theme.*

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        content = content
    )
}

@Composable
fun ScoreRing(score: Int, size: Int = 40, strokeWidth: Float = 4f, fontSize: Int = 13) {
    val ringColor = when {
        score >= 80 -> SuccessGreen
        score >= 60 -> WarningAmber
        else -> DangerRed
    }
    val ringBackgroundColor = BorderColor
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(ringBackgroundColor, style = Stroke(width = strokeWidth.dp.toPx()))
            drawArc(ringColor, -90f, 360f * (score / 100f), false, style = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round))
        }
        Text(score.toString(), fontSize = fontSize.sp, fontWeight = FontWeight.Bold, color = ringColor)
    }
}

@Composable
fun LoginScreen(viewModel: CarVerifyViewModel) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotModal by remember { mutableStateOf(false) }
    var isSendingEmail by remember { mutableStateOf(false) }
    var emailSentSuccess by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }

    androidx.compose.runtime.LaunchedEffect(isSendingEmail) {
        if (isSendingEmail) {
            kotlinx.coroutines.delay(1500)
            isSendingEmail = false
            emailSentSuccess = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(64.dp).background(Color.Transparent, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_carlore_logo), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(56.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("CarLore", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Text("Verify any used car before you buy.", fontSize = 15.sp, color = TextMuted, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email", color = TextMuted) },
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password", color = TextMuted) },
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "Forgot Password?",
                    color = PrimaryBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            forgotEmailInput = email
                            showForgotModal = true
                            emailSentSuccess = false
                            isSendingEmail = false
                        }
                        .padding(4.dp)
                )
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = DangerRed, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp).align(Alignment.Start))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password."
                    } else {
                        viewModel.navigateTo(AppScreen.Search) 
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    if (showForgotModal) {
        AlertDialog(
            onDismissRequest = { 
                if (!isSendingEmail) showForgotModal = false 
            },
            title = {
                Text(if (emailSentSuccess) "Email Sent" else "Reset Password", fontWeight = FontWeight.Bold)
            },
            text = {
                if (emailSentSuccess) {
                    Text("We've sent password reset instructions to your email address. Please check your inbox and follow the link to reset your password.")
                } else if (isSendingEmail) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Text("Sending instructions...", color = TextMuted)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Enter your email address and we'll send you a link to reset your password.")
                        OutlinedTextField(
                            value = forgotEmailInput,
                            onValueChange = { forgotEmailInput = it },
                            label = { Text("Email", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = CardBg,
                                unfocusedContainerColor = CardBg,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                if (emailSentSuccess) {
                    TextButton(onClick = { showForgotModal = false }) {
                        Text("OK", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                } else if (!isSendingEmail) {
                    TextButton(onClick = { 
                        if (forgotEmailInput.isNotBlank()) {
                            isSendingEmail = true
                        }
                    }) {
                        Text("Send Email", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!isSendingEmail && !emailSentSuccess) {
                    TextButton(onClick = { showForgotModal = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            },
            containerColor = CardBg,
            titleContentColor = TextPrimary,
            textContentColor = TextMuted
        )
    }
}

@Composable
fun SearchScreen(viewModel: CarVerifyViewModel) {
    val regInput by viewModel.regInput.collectAsState()
    val state by viewModel.vehicleState.collectAsState()
    val focusManager = LocalFocusManager.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val recentSearches by viewModel.recentSearchesFlow.collectAsState()
    var selectedPillIdx by remember { mutableStateOf(0) }
    var showSeeAllFeatures by remember { mutableStateOf(false) }
    var showSeeAllRecentSearches by remember { mutableStateOf(false) }
    var selectedCheckModal by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    androidx.compose.runtime.LaunchedEffect(state) {
        if (state is VehicleState.Success) {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Verify any used car", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text("before you buy", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                Text("RTO · Accidents · Active loans · 5-yr costs —\ninstant report", fontSize = 13.sp, color = TextMuted, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Medium, lineHeight = 18.sp)
                
                OutlinedTextField(
                    value = regInput,
                    onValueChange = { viewModel.onRegInputChange(it) },
                    placeholder = { Text("Enter reg. no. e.g. AP31...", color = TextMuted, fontWeight = FontWeight.Medium) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = PrimaryBlue) },
                    trailingIcon = { Box(modifier = Modifier.padding(end=8.dp).size(28.dp).background(BlueBg, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) { Text("⊞", color=PrimaryBlue, fontSize=16.sp) } },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).border(1.dp, PrimaryBlue, RoundedCornerShape(12.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SearchBarBg,
                        unfocusedContainerColor = SearchBarBg
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        focusManager.clearFocus()
                        viewModel.checkVehicleNow(regInput) 
                    })
                )

                val isError = state is VehicleState.Error
                if (isError) {
                    Text((state as VehicleState.Error).message, color = DangerRed, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
                }

                Button(
                    onClick = { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        focusManager.clearFocus()
                        viewModel.checkVehicleNow(regInput) 
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(48.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardBg)
                ) {
                    if (state is VehicleState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Checking...", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    } else {
                        Text("Check Now →", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
        
        item {
            AppCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatItem("48,210", PrimaryBlue, "REPORTS")
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(BorderColor))
                    StatItem("63%", WarningAmber, "ISSUES FOUND")
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(BorderColor))
                    StatItem("₹38K", SuccessGreen, "AVG. SAVED")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pre-purchase checks", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("See all", fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable {
                    showSeeAllFeatures = true
                }.padding(4.dp))
            }
        }

        item {
            LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val pills = listOf("Accidents", "Odometer", "Loans", "Flood", "Theft", "RC Status")
                items(pills.size) { idx ->
                    val isActive = idx == selectedPillIdx
                    Box(modifier = Modifier
                        .clickable { selectedPillIdx = idx }
                        .background(if (isActive) BlueBg else SurfaceColor, RoundedCornerShape(20.dp)).border(1.dp, if (isActive) PrimaryBlue else BorderColor, RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(pills[idx], fontSize = 14.sp, color = if (isActive) PrimaryBlue else TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val openModal = { title: String, icon: String, detail: String -> 
                    selectedCheckModal = Triple(icon, title, detail) 
                }
                when (selectedPillIdx) {
                    0 -> {
                        item { CheckCard("💥", "Hidden Accidents", "60% of sellers hide repairs. We check 3 independent sources.", RedBg) { openModal("Hidden Accidents", "💥", "We fetch accident records from over 100 RTOs and leading insurance providers. Small dents to total-loss accidents are uncovered. This ensures you know the true history of the bodywork.") } }
                        item { CheckCard("📝", "Structural Damage", "Identify replaced panels or frame damage using AI analysis.", AmberBg) { openModal("Structural Damage", "📝", "We use AI to analyze historical photos of the vehicle (if available) and check OEM databases for major part replacement orders. A compromised frame poses a serious safety risk.") } }
                    }
                    1 -> {
                        item { CheckCard("⏱️", "Odometer Fraud", "Cross-check mileage across insurance and service records.", AmberBg) { openModal("Odometer Fraud", "⏱️", "We cross-reference the odometer readings logged during each insurance renewal, PUC check, and authorized service center visit to plot a linear curve and spot roll-backs.") } }
                        item { CheckCard("🏎️", "Service History", "Match meter readings with official OEM workshop logs.", BlueBg) { openModal("Service History", "🏎️", "We pull maintenance logs including regular servicing, oil changes, part replacements, and any reported mechanical failures from authorized workshops and major multi-brand garages.") } }
                    }
                    2 -> {
                        item { CheckCard("🏦", "Active Loans", "Verify hypothecation before making any payment.", BlueBg) { openModal("Active Loans", "🏦", "We verify the hypothecation status of the vehicle to ensure it is free from existing financial liens. Buying a car with an active loan restricts your ability to transfer ownership.") } }
                        item { CheckCard("📜", "NOC Status", "Ensure NOC is issued by the finance bank.", GreenBg) { openModal("NOC Status", "📜", "Check if the current owner has successfully obtained a No Objection Certificate from the lending institution and RTO, confirming the loan is indeed paid off.") } }
                    }
                    3 -> {
                        item { CheckCard("🌊", "Flood Damage", "GPS and insurance history reveals flood-affected vehicles.", GreenBg) { openModal("Flood Damage", "🌊", "We analyze GPS and weather history matching the location of vehicle registration to identify if the car has been exposed to catastrophic floods, flagging insurance claims with 'water damage'.") } }
                        item { CheckCard("🌧️", "Rust Inspection", "Identify hidden rust in chassis and undercarriage.", RedBg) { openModal("Rust Inspection", "🌧️", "We check if the car originates from coastal regions and provide specific focus areas like the undercarriage and wheel wells where rust is commonly deliberately painted over.") } }
                    }
                    4 -> {
                        item { CheckCard("🚨", "Stolen Vehicle", "Verify with national crime records and FIR database.", RedBg) { openModal("Stolen Vehicle", "🚨", "We instantly run the vehicle's registration against the national police database to ensure the vehicle has no FIRs or theft reports. Protect yourself from legal trouble.") } }
                        item { CheckCard("🚔", "Blacklisted Status", "Check if court has blocked the RC transfer.", AmberBg) { openModal("Blacklisted Status", "🚔", "Verify if the vehicle has been blacklisted by authorities for reasons such as tax default, pending severe challans, or court orders that block ownership transfer.") } }
                    }
                    else -> {
                        item { CheckCard("📑", "RC Validity", "Verify registration expiry and RTO details.", BlueBg) { openModal("RC Validity", "📑", "Confirmation of the registration certificate's validity, ensuring the fitness certificate, insurance, and PUC are up-to-date and legitimately registered in the seller's name.") } }
                        item { CheckCard("🎫", "Challan History", "Check pending traffic fines tied to the license plate.", RedBg) { openModal("Challan History", "🎫", "A complete report of all past and unpaid traffic fines associated with the license plate, showing you exactly what unpaid debts you might inherit.") } }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            AppCard(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("How CarLore works", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        StepItem("1", "Enter\nnumber")
                        Text("   →   ", color = TextMuted, fontWeight = FontWeight.Medium)
                        StepItem("2", "Check 8\nsources")
                        Text("   →   ", color = TextMuted, fontWeight = FontWeight.Medium)
                        StepItem("3", "Instant\nreport")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recently verified", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("View all", fontSize = 14.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { showSeeAllRecentSearches = true }.padding(4.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        items(
            count = recentSearches.take(3).size,
            key = { idx -> recentSearches[idx].regNumber }
        ) { idx ->
            val car = recentSearches[idx]
            val onClick = remember(car.regNumber) { { viewModel.loadRecentSearch(car.regNumber) } }
            AppCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { 
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onClick() 
            }) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(BgColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Text("🚗") }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(car.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${car.regNumber} · ${car.info}", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(car.pill1Title to car.pill1Color, car.pill2Title to car.pill2Color).forEach { (label, col) ->
                                if (label.isNotEmpty()) {
                                    val (bg, tc) = when(col) {
                                        "red" -> RedBg to DangerRed
                                        "green" -> GreenBg to SuccessGreen
                                        "amber" -> AmberBg to WarningAmber
                                        else -> GrayBg to TextMuted
                                    }
                                    Box(modifier = Modifier.background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(label, fontSize = 11.sp, color = tc, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                    ScoreRing(score = car.trustScore)
                }
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }

    AnimatedVisibility(
        visible = showSeeAllFeatures,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = BgColor, shadowElevation = 2.dp) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("✕", fontSize = 20.sp, modifier = Modifier.clickable { showSeeAllFeatures = false }.padding(8.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("All Reports & Checks", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val allChecks = listOf(
                        "Hidden Accidents in multiple RTO bases",
                        "Odometer fraud via service logs",
                        "Active Loans & Hypothecation",
                        "Flood Damage analysis from GPS details",
                        "Stolen / Blacklisted by local authorities",
                        "RC Status / Name Transfer Validation",
                        "Challan & Pending Fines Tracker",
                        "Taxi / Commercial Use detection",
                        "OEM Service History Verification"
                    )
                    items(allChecks.size) { idx ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(PrimaryBlue, CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Text(allChecks[idx], fontSize = 15.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                        if (idx < allChecks.size - 1) {
                            HorizontalDivider(color = BorderColor, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showSeeAllRecentSearches,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = BgColor, shadowElevation = 2.dp) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("✕", fontSize = 20.sp, modifier = Modifier.clickable { showSeeAllRecentSearches = false }.padding(8.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Past Reports", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 12.dp)) {
                    items(recentSearches.size, key = { recentSearches[it].regNumber }) { idx ->
                        val car = recentSearches[idx]
                        val onClick = remember(car.regNumber) { { 
                            showSeeAllRecentSearches = false
                            viewModel.loadRecentSearch(car.regNumber) 
                        } }
                        AppCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            onClick() 
                        }) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).background(BgColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Text("🚗") }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(car.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${car.regNumber} · ${car.info}", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                                    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(car.pill1Title to car.pill1Color, car.pill2Title to car.pill2Color).forEach { (label, col) ->
                                            if (label.isNotEmpty()) {
                                                val (bg, tc) = when(col) {
                                                    "red" -> RedBg to DangerRed
                                                    "green" -> GreenBg to SuccessGreen
                                                    "amber" -> AmberBg to WarningAmber
                                                    else -> GrayBg to TextMuted
                                                }
                                                Box(modifier = Modifier.background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                    Text(label, fontSize = 11.sp, color = tc, fontWeight = FontWeight.ExtraBold)
                                                }
                                            }
                                        }
                                    }
                                }
                                ScoreRing(score = car.trustScore)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }
    }
    
    if (selectedCheckModal != null) {
        val check = selectedCheckModal!!
        AlertDialog(
            onDismissRequest = { selectedCheckModal = null },
            confirmButton = {
                TextButton(onClick = { selectedCheckModal = null }) {
                    Text("Got it", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(check.first, fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(check.second, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextPrimary)
                }
            },
            text = {
                Text(check.third, fontSize = 15.sp, color = TextMuted, lineHeight = 22.sp, fontWeight = FontWeight.Medium)
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = CardBg,
            titleContentColor = TextPrimary,
            textContentColor = TextMuted
        )
    }
    }
}

@Composable
fun StatItem(value: String, color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun CheckCard(icon: String, title: String, desc: String, bg: Color, onClick: () -> Unit = {}) {
    AppCard(modifier = Modifier.width(140.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(36.dp).background(bg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) { Text(icon) }
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(desc, fontSize = 13.sp, color = TextMuted, maxLines = 2, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StepItem(num: String, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(28.dp).background(PrimaryBlue, CircleShape), contentAlignment = Alignment.Center) {
            Text(num, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(8.dp))
        Text(text, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReportScreen(viewModel: CarVerifyViewModel) {
    val report by viewModel.activeReport.collectAsState()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor), contentAlignment = Alignment.Center) {
            Text("Search a vehicle first", color = TextMuted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        return
    }

    val r = report!!

    LazyColumn(modifier = Modifier.fillMaxSize().background(BgColor), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${r.carName} ${r.year}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${r.regNumber} · ${r.fuel} · ${r.odometer} km", fontSize = 13.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Medium)
                        
                        Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            r.flags.forEach { flag ->
                                val isRed = flag.contains("loan", true) || flag.contains("accident", true)
                                val isGreen = flag.contains("flood", true) || flag.contains("clean", true)
                                val bg = if (isRed) RedBg else if (isGreen) GreenBg else AmberBg
                                val tc = if (isRed) DangerRed else if (isGreen) SuccessGreen else WarningAmber
                                Box(modifier = Modifier.background(bg, RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(flag, fontSize = 12.sp, color = tc, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ScoreRing(score = r.trustScore, size = 64, strokeWidth = 5f, fontSize = 20)
                        Text("Trust score", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Score breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    val bd = r.scoreBreakdown
                    ScoreBarItem("History", bd.history)
                    ScoreBarItem("Condition", bd.condition)
                    ScoreBarItem("Documents", bd.documents)
                    ScoreBarItem("Mileage", bd.mileage)
                }
            }
        }

        item {
            Text("Verification results", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(vertical = 8.dp))
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(0.dp)) {
                    r.checks.forEachIndexed { index, check ->
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            val isPass = check.status == "pass"
                            val isWarn = check.status == "warn"
                            val iconStr = if (isPass) "✔️" else if (isWarn) "⚠️" else "❌"
                            val bg = if (isPass) GreenBg else if (isWarn) AmberBg else RedBg
                            Box(modifier = Modifier.size(32.dp).background(bg, CircleShape), contentAlignment = Alignment.Center) { Text(iconStr, fontSize = 14.sp) }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(check.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text(check.detail, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                            }
                        }
                        if (index < r.checks.size - 1) HorizontalDivider(color = BorderColor)
                    }
                }
            }
        }

        item {
            Button(
                onClick = { 
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    viewModel.navigateTo(AppScreen.Booking) 
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Book a physical inspection →", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun ScoreBarItem(label: String, pc: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        val col = if (pc >= 70) SuccessGreen else if (pc >= 50) WarningAmber else DangerRed
        Text(label, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.width(80.dp), fontWeight = FontWeight.SemiBold)
        Box(modifier = Modifier.weight(1f).height(8.dp).background(GrayBg, RoundedCornerShape(4.dp))) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(pc / 100f).background(col, RoundedCornerShape(4.dp)))
        }
        Spacer(Modifier.width(8.dp))
        Text("$pc%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = col)
    }
}

@Composable
fun CostEstimatorScreen(viewModel: CarVerifyViewModel) {
    val report by viewModel.activeReport.collectAsState()
    val yearFactor by viewModel.selectedYearFactor.collectAsState()
    val mileage by viewModel.mileageYearly.collectAsState()
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize().background(BgColor), contentAlignment = Alignment.Center) { Text("Search a vehicle first", color = TextMuted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        return
    }

    val div = if (yearFactor == 1) 4.2f else if (yearFactor == 3) 1.7f else 1f
    val milMod = mileage / 12000f

    val srv = ((72000 / div) * milMod).toInt()
    val tyr = ((28000 / div) * milMod).toInt()
    val rep = ((55000 / div) * milMod).toInt()
    val ins = (40000 / div).toInt()
    val total = srv + tyr + rep + ins

    LazyColumn(modifier = Modifier.fillMaxSize().background(BgColor), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("5-year cost forecast", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(report?.carName ?: "", fontSize = 14.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth().background(SurfaceColor, RoundedCornerShape(12.dp)).padding(4.dp)) {
                listOf(1, 3, 5).forEach { y ->
                    val sel = yearFactor == y
                    Box(modifier = Modifier.weight(1f).background(if (sel) PrimaryBlue else Color.Transparent, RoundedCornerShape(10.dp)).clickable { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        viewModel.setYearFactor(y) 
                    }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Text("$y year${if(y>1) "s" else ""}", fontSize = 14.sp, color = if (sel) Color.White else TextMuted, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CostBarItem("Routine servicing", srv, 72000, SuccessGreen)
                    CostBarItem("Tyres (×2 sets)", tyr, 28000, SuccessGreen)
                    CostBarItem("Likely repairs", rep, 55000, WarningAmber)
                    CostBarItem("Insurance", ins, 40000, PrimaryBlue)
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BorderColor)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total ownership cost", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("₹${String.format("%,d", total)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Adjust your annual mileage", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Slider(value = mileage.toFloat(), onValueChange = { viewModel.setMileageYearly(it.toInt()) }, valueRange = 5000f..20000f, steps = 14, colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue))
            Text("${String.format("%,d", mileage)} km/yr", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun CostBarItem(label: String, value: Int, max: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("₹${String.format("%,d", value)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp).background(GrayBg, RoundedCornerShape(4.dp))) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(minOf(value.toFloat() / max, 1f)).background(color, RoundedCornerShape(4.dp)))
        }
    }
}

@Composable
fun InspectorBookingScreen(viewModel: CarVerifyViewModel) {
    var selectedPlan by remember { mutableStateOf("Premium") }
    var showModal by remember { mutableStateOf(false) }
    var selectedInspector by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    LazyColumn(modifier = Modifier.fillMaxSize().background(BgColor), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Book an inspector", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text("Certified mechanic visits the car. Video report in 24 hrs.", fontSize = 14.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlanCard("Basic", "₹799", listOf("60-point check", "Report"), selectedPlan == "Basic", Modifier.weight(1f)) { selectedPlan = "Basic" }
                PlanCard("Premium", "₹1,499", listOf("150-point", "HD Video"), selectedPlan == "Premium", Modifier.weight(1f)) { selectedPlan = "Premium" }
            }
        }

        item { Text("Available near Vijayawada", fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }

        val inspectors = listOf(
            Triple("Ravi Kumar", "12 yrs · Maruti specialist", "Available today"),
            Triple("Srinivas Prasad", "8 yrs · All brands", "Available today")
        )
        items(inspectors.size) { i ->
            val inst = inspectors[i]
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).background(BlueBg, CircleShape), contentAlignment = Alignment.Center) { Text(inst.first.take(1), color = PrimaryBlue, fontWeight = FontWeight.ExtraBold) }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(inst.first, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(inst.second, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Text("4.9★", fontSize = 13.sp, color = WarningAmber, fontWeight = FontWeight.SemiBold)
                    }
                    Button(onClick = { 
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        selectedInspector = inst
                        showModal = true 
                    }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("Book", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = BlueBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ℹ️", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("User Instructions", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                    }
                    
                    Text("How to prepare for the mechanic's visit:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Ensure the vehicle is parked in a well-lit area.", fontSize = 13.sp, color = TextMuted)
                        Text("• Have the RC, Insurance, and Service records ready for verification.", fontSize = 13.sp, color = TextMuted)
                        Text("• Hand over the keys to allow interior and engine checks.", fontSize = 13.sp, color = TextMuted)
                    }
                    
                    HorizontalDivider(color = PrimaryBlue.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                    Text("Professional Communication", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• The mechanic will call your registered number before arrival.", fontSize = 13.sp, color = TextMuted)
                        Text("• Do not interrupt the mechanic during the inspection or video recording.", fontSize = 13.sp, color = TextMuted)
                        Text("• Contact details of the mechanic will be shared upon booking confirmation.", fontSize = 13.sp, color = TextMuted)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }

    if (showModal && selectedInspector != null) {
        val inspector = selectedInspector!!
        AlertDialog(
            onDismissRequest = { showModal = false },
            confirmButton = { Button(onClick = { 
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                showModal = false 
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) { Text("Got It", color = Color.White, fontWeight = FontWeight.Bold) } },
            title = { Text("Booking Confirmed 🔧", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Your inspector, ${inspector.first}, has been assigned and will evaluate the car. They have also received your contact details to coordinate.", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BlueBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Inspector Details", fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.ExtraBold)
                            Text("Name: ${inspector.first}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Phone: +91 98765 43210", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Important Guidelines:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("• Allow 45-60 minutes for the full inspection.", fontSize = 13.sp, color = TextMuted)
                        Text("• Please hand over the vehicle keys and registration documents when requested.", fontSize = 13.sp, color = TextMuted)
                        Text("• Do not interrupt the mechanic while they are recording the video report.", fontSize = 13.sp, color = TextMuted)
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            containerColor = SurfaceColor
        )
    }
}

@Composable
fun PlanCard(title: String, price: String, feats: List<String>, isSel: Boolean, mod: Modifier, onSel: () -> Unit) {
    val bc = if (isSel) PrimaryBlue else BorderColor
    val bw = if (isSel) 2.dp else 1.dp
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    AppCard(modifier = mod.border(bw, bc, RoundedCornerShape(14.dp)).clickable { 
        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
        onSel() 
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(price, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 4.dp))
            feats.forEach { Text("• $it", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium) }
        }
    }
}
