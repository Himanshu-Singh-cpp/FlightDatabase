// app/src/main/java/com/example/flightdatabase/data/FlightDao.kt
package com.example.flightdatabase.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert
    suspend fun insertFlight(flight: Flight): Long

    @Insert
    suspend fun insertFlights(flights: List<Flight>)

    @Query("SELECT * FROM flights")
    fun getAllFlights(): Flow<List<Flight>>

    @Query("SELECT * FROM flights WHERE flightNumber = :flightNumber")
    fun getFlightsByNumber(flightNumber: String): Flow<List<Flight>>

    @Query("SELECT AVG(actualArrivalTime - actualDepartureTime) FROM flights WHERE flightNumber = :flightNumber")
    suspend fun getAverageFlightDuration(flightNumber: String): Long

    @Query("SELECT flightNumber, AVG(actualArrivalTime - actualDepartureTime) as avgDuration FROM flights GROUP BY flightNumber")
    fun getAllFlightAverageDurations(): Flow<List<FlightDuration>>
}

data class FlightDuration(
    val flightNumber: String,
    val avgDuration: Long
)