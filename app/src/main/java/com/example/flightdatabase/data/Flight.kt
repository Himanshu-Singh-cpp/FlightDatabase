// app/src/main/java/com/example/flightdatabase/data/Flight.kt
package com.example.flightdatabase.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightNumber: String,
    val origin: String,
    val destination: String,
    val scheduledDepartureTime: Long, // Stored as timestamp
    val scheduledArrivalTime: Long,
    val actualDepartureTime: Long,
    val actualArrivalTime: Long,
    val date: Long // Timestamp of flight date
) {
    // Calculate flight duration including delays
    fun getActualDuration(): Long = actualArrivalTime - actualDepartureTime

    // Calculate delay in minutes
    fun getDelayMinutes(): Long =
        ((actualDepartureTime - scheduledDepartureTime) / 60000) +
        ((actualArrivalTime - scheduledArrivalTime) / 60000)
}