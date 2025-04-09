// app/src/main/java/com/example/flightdatabase/data/FlightRepository.kt
package com.example.flightdatabase.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlightRepository(private val flightDao: FlightDao) {
    val allFlights: Flow<List<Flight>> = flightDao.getAllFlights()
    val flightAverageDurations: Flow<Map<String, Long>> = flightDao.getAllFlightAverageDurations()
        .map { durations -> durations.associate { it.flightNumber to it.avgDuration } }

    suspend fun insert(flight: Flight) {
        flightDao.insertFlight(flight)
    }

    suspend fun getAverageFlightDuration(flightNumber: String): Long {
        return flightDao.getAverageFlightDuration(flightNumber)
    }

    fun getFlightsByNumber(flightNumber: String): Flow<List<Flight>> {
        return flightDao.getFlightsByNumber(flightNumber)
    }
}