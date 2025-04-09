// app/src/main/java/com/example/flightdatabase/workers/FlightDataWorker.kt
package com.example.flightdatabase.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flightdatabase.data.Flight
import com.example.flightdatabase.data.FlightDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FlightDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = FlightDatabase.getDatabase(
                applicationContext,
                kotlinx.coroutines.CoroutineScope(Dispatchers.IO)
            )
            val flightDao = database.flightDao()

            // In a real app, this would fetch data from an API
            // Here we'll simulate by creating a new day's worth of flights
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -7) // Move to one week ago
            val dateTimestamp = calendar.timeInMillis

            // Generate 3 flights for today
            val flights = mutableListOf<Flight>()

            // Flight 1: New York to Los Angeles
            val flight1DepartureTime = dateTimestamp + TimeUnit.HOURS.toMillis(8)
            val flight1ScheduledArrivalTime = flight1DepartureTime + TimeUnit.HOURS.toMillis(6)
            val flight1Delay = TimeUnit.MINUTES.toMillis((0..45).random().toLong())
            val flight1ActualDepartureTime = flight1DepartureTime + flight1Delay
            val flight1ActualArrivalTime = flight1ScheduledArrivalTime + flight1Delay +
                    TimeUnit.MINUTES.toMillis((0..30).random().toLong())

            flights.add(Flight(
                flightNumber = "NY101",
                origin = "New York",
                destination = "Los Angeles",
                scheduledDepartureTime = flight1DepartureTime,
                scheduledArrivalTime = flight1ScheduledArrivalTime,
                actualDepartureTime = flight1ActualDepartureTime,
                actualArrivalTime = flight1ActualArrivalTime,
                date = dateTimestamp
            ))

            // Add the other two flights similarly...

            // Save to database
            flightDao.insertFlights(flights)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}