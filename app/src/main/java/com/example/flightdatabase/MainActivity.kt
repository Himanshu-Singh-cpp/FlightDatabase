// app/src/main/java/com/example/flightdatabase/MainActivity.kt
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.flightdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.flightdatabase.ui.theme.FlightDatabaseTheme
import com.example.flightdatabase.viewmodels.FlightViewModel
import com.example.flightdatabase.viewmodels.FlightViewModelFactory
import com.example.flightdatabase.workers.FlightDataWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults

class MainActivity : ComponentActivity() {
    private lateinit var flightViewModel: FlightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup ViewModel
        val flightViewModelFactory = FlightViewModelFactory(application)
        flightViewModel = ViewModelProvider(this, flightViewModelFactory)[FlightViewModel::class.java]

        // Setup periodic work request for collecting flight data
        setupBackgroundWork()

        setContent {
            FlightDatabaseTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Flight Time Analysis") }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    FlightStatisticsScreen(
                        flightViewModel = flightViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun setupBackgroundWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FlightDataWorker>(
            1, TimeUnit.DAYS
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "FlightDataCollectionWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun FlightStatisticsScreen(flightViewModel: FlightViewModel, modifier: Modifier = Modifier) {
    val averageDurations by flightViewModel.flightAverageDurations.collectAsState(initial = emptyMap())
    val allFlights by flightViewModel.allFlights.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Average Flight Times",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (averageDurations.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(averageDurations.entries.toList()) { (flightNumber, avgDuration) ->
                    FlightAverageCard(
                        flightNumber = flightNumber,
                        averageDurationMinutes = TimeUnit.MILLISECONDS.toMinutes(avgDuration)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "All Flight Records (${allFlights.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(allFlights) { flight ->
                    FlightDetailCard(flight)
                }
            }
        }
    }
}

@Composable
fun FlightAverageCard(flightNumber: String, averageDurationMinutes: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Flight $flightNumber",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Average Duration: ${formatDuration(averageDurationMinutes)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FlightDetailCard(flight: com.example.flightdatabase.data.Flight) {
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = flight.flightNumber,
                    fontWeight = FontWeight.Bold
                )
                Text(text = dateFormat.format(Date(flight.date)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${flight.origin} → ${flight.destination}")
                Text(
                    text = "${formatDuration(flight.getActualDuration() / 60000)}",
                    fontWeight = FontWeight.Medium
                )
            }

            if (flight.getDelayMinutes() > 0) {
                Text(
                    text = "Delay: ${flight.getDelayMinutes()} min",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun formatDuration(minutes: Long): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return "${hours}h ${mins}min"
}