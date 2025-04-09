// app/src/main/java/com/example/flightdatabase/viewmodels/FlightViewModel.kt
package com.example.flightdatabase.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightdatabase.data.FlightDatabase
import com.example.flightdatabase.data.FlightRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FlightViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FlightRepository = FlightRepository(
        FlightDatabase.getDatabase(
            application,
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ).flightDao()
    )

    val allFlights = repository.allFlights
    val flightAverageDurations = repository.flightAverageDurations
}

class FlightViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}