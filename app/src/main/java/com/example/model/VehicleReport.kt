package com.example.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VehicleReport(
    val carName: String = "Maruti Swift",
    val regNumber: String = "AP31AB1234",
    val year: Int = 2018,
    val variant: String = "VXI",
    val fuel: String = "Petrol",
    val odometer: Int = 58400,
    val trustScore: Int = 64,
    val owners: Int = 2,
    val city: String = "Vijayawada",
    val checks: List<ReportCheck> = listOf(
        ReportCheck("RTO Registration", "pass", "Valid until 2033"),
        ReportCheck("Theft/Blacklist", "pass", "No records found"),
        ReportCheck("Odometer", "pass", "Consistent with service logs"),
        ReportCheck("Accident history", "warn", "Found 1 minor repair record"),
        ReportCheck("Hypothecation", "fail", "Active loan with HDFC Bank"),
        ReportCheck("Flood damage", "pass", "No risk detected"),
        ReportCheck("Service record", "warn", "Missing 2 routine services")
    ),
    val flags: List<String> = listOf("Loan active", "Minor accident", "No flood"),
    val ownershipHistory: List<ReportOwner> = listOf(
        ReportOwner(1, "Vijayawada", 2018, 2021, 28000),
        ReportOwner(2, "Hyderabad", 2021, null, 58400)
    ),
    val scoreBreakdown: ReportScore = ReportScore(70, 65, 50, 80),
    val upcomingRepairs: List<ReportRepair> = listOf(
        ReportRepair("Timing belt", "soon", 8000, 12000),
        ReportRepair("Brake pads", "watch", 3500, 5000),
        ReportRepair("AC compressor", "watch", 15000, 22000)
    )
)

@JsonClass(generateAdapter = true)
data class ReportCheck(
    val label: String,
    val status: String,
    val detail: String
)

@JsonClass(generateAdapter = true)
data class ReportOwner(
    val ownerNum: Int,
    val city: String,
    val fromYear: Int,
    val toYear: Int?,
    val km: Int
)

@JsonClass(generateAdapter = true)
data class ReportScore(
    val history: Int,
    val condition: Int,
    val documents: Int,
    val mileage: Int
)

@JsonClass(generateAdapter = true)
data class ReportRepair(
    val part: String,
    val urgency: String,
    val estimateLow: Int,
    val estimateHigh: Int
)
