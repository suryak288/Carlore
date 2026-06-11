package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.model.VehicleReport
import com.example.network.GeminiApiClient
import com.example.data.AppDatabase
import com.example.data.RecentSearchEntity
import com.example.data.SearchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen { Login, Search, Report, CostEstimator, Booking }

sealed interface VehicleState {
    object Idle : VehicleState
    object Loading : VehicleState
    data class Success(val report: VehicleReport) : VehicleState
    data class Error(val message: String) : VehicleState
}

class CarVerifyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = SearchRepository(db.recentSearchDao())

    val recentSearchesFlow: StateFlow<List<RecentSearchEntity>> = repository.recentSearches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentScreen = MutableStateFlow(AppScreen.Login)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _regInput = MutableStateFlow("")
    val regInput: StateFlow<String> = _regInput.asStateFlow()

    private val _vehicleState = MutableStateFlow<VehicleState>(VehicleState.Idle)
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _activeReport = MutableStateFlow<VehicleReport?>(null)
    val activeReport: StateFlow<VehicleReport?> = _activeReport.asStateFlow()

    private val _selectedYearFactor = MutableStateFlow(5)
    val selectedYearFactor: StateFlow<Int> = _selectedYearFactor.asStateFlow()

    private val _mileageYearly = MutableStateFlow(10000)
    val mileageYearly: StateFlow<Int> = _mileageYearly.asStateFlow()

    fun navigateTo(screen: AppScreen) { _currentScreen.value = screen }
    fun onRegInputChange(newValue: String) { _regInput.value = newValue }
    fun setYearFactor(factor: Int) { _selectedYearFactor.value = factor }
    fun setMileageYearly(mileage: Int) { _mileageYearly.value = mileage }

    private fun saveReportToRecent(report: VehicleReport) {
        viewModelScope.launch {
            val p1Title = report.flags.getOrNull(0) ?: ""
            val p1Color = if (report.trustScore < 60) "red" else "green" // Simplified for now
            val p2Title = report.flags.getOrNull(1) ?: ""
            val p2Color = if (report.trustScore < 75) "amber" else "green"

            val entity = RecentSearchEntity(
                regNumber = report.regNumber,
                title = "${report.carName} ${report.variant} ${report.year}",
                info = "${String.format("%,d", report.odometer)} km • ${report.fuel}",
                pill1Title = p1Title,
                pill1Color = p1Color,
                pill2Title = p2Title,
                pill2Color = p2Color,
                trustScore = report.trustScore
            )
            repository.addSearch(entity)
        }
    }

    fun checkVehicleNow(regNumber: String) {
        if (regNumber.isBlank()) {
            _vehicleState.value = VehicleState.Error("Enter a registration number")
            return
        }
        viewModelScope.launch {
            _vehicleState.value = VehicleState.Loading
            
            // Artificial delay for UX
            delay(2000)
            
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback mock
                val fallback = VehicleReport(regNumber = regNumber.uppercase())
                _activeReport.value = fallback
                _vehicleState.value = VehicleState.Success(fallback)
                saveReportToRecent(fallback)
                navigateTo(AppScreen.Report)
                return@launch
            }

            val report = GeminiApiClient.fetchVehicleReport(regNumber, apiKey)
            if (report != null) {
                _activeReport.value = report
                _vehicleState.value = VehicleState.Success(report)
                saveReportToRecent(report)
                navigateTo(AppScreen.Report)
            } else {
                _vehicleState.value = VehicleState.Error("We couldn't fetch this vehicle's data. Try again or check the registration number.")
            }
        }
    }

    fun loadRecentSearch(regNumber: String) {
        _regInput.value = regNumber
        checkVehicleNow(regNumber)
    }
}
