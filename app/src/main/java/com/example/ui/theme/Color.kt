package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF38BDF8) // Lighter blue for dark mode visibility from screenshot
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
val DangerRed = Color(0xFFEF4444)

val SearchBarBg = Color(0xFF1E293B) // Or 0xFF2A2B3D
val CardBg = Color(0xFF161B22) // Or 0xFF171923

val BgColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF0B0F19) else Color(0xFFF7F8FA)

val SurfaceColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF111827) else Color(0xFFFFFFFF)

val TextPrimary: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF111827)

val TextMuted: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF374151)

val BorderColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE5E7EB)

val InactiveIcon: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF64748B) else Color(0xFF4B5563)

val BlueBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E3A8A) else Color(0xFFEFF6FF)

val RedBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF451A1E) else Color(0xFFFEF2F2)

val GreenBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFF0FDF4)

val AmberBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF451A03) else Color(0xFFFFF7ED)

val GrayBg: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFF3F4F6)