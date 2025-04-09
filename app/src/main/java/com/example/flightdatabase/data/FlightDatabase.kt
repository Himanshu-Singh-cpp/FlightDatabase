// app/src/main/java/com/example/flightdatabase/data/FlightDatabase.kt
package com.example.flightdatabase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Database(entities = [Flight::class], version = 1, exportSchema = false)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile
        private var INSTANCE: FlightDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java,
                    "flight_database"
                )
                .addCallback(FlightDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class FlightDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.flightDao())
                    }
                }
            }

            private suspend fun populateDatabase(flightDao: FlightDao) {
                // Hardcode flight data for one week (7 days x 3 flights per day)
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -7)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                val flights = mutableListOf<Flight>()

                // Create sample flights for one week
                for (day in 0..6) {
                    calendar.add(Calendar.DAY_OF_MONTH, if (day > 0) 1 else 0)
                    val dateTimestamp = calendar.timeInMillis

                    // Flight 1: New York to Los Angeles (morning)
                    val flight1DepartureTime = dateTimestamp + TimeUnit.HOURS.toMillis(8)
                    val flight1ScheduledArrivalTime = flight1DepartureTime + TimeUnit.HOURS.toMillis(6)
                    // Add random delay between 0-45 minutes
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

                    // Flight 2: Chicago to Miami (afternoon)
                    val flight2DepartureTime = dateTimestamp + TimeUnit.HOURS.toMillis(13)
                    val flight2ScheduledArrivalTime = flight2DepartureTime + TimeUnit.HOURS.toMillis(3)
                    val flight2Delay = TimeUnit.MINUTES.toMillis((0..60).random().toLong())
                    val flight2ActualDepartureTime = flight2DepartureTime + flight2Delay
                    val flight2ActualArrivalTime = flight2ScheduledArrivalTime + flight2Delay +
                                                  TimeUnit.MINUTES.toMillis((0..20).random().toLong())

                    flights.add(Flight(
                        flightNumber = "CH202",
                        origin = "Chicago",
                        destination = "Miami",
                        scheduledDepartureTime = flight2DepartureTime,
                        scheduledArrivalTime = flight2ScheduledArrivalTime,
                        actualDepartureTime = flight2ActualDepartureTime,
                        actualArrivalTime = flight2ActualArrivalTime,
                        date = dateTimestamp
                    ))

                    // Flight 3: San Francisco to Seattle (evening)
                    val flight3DepartureTime = dateTimestamp + TimeUnit.HOURS.toMillis(18)
                    val flight3ScheduledArrivalTime = flight3DepartureTime + TimeUnit.HOURS.toMillis(2)
                    val flight3Delay = TimeUnit.MINUTES.toMillis((0..30).random().toLong())
                    val flight3ActualDepartureTime = flight3DepartureTime + flight3Delay
                    val flight3ActualArrivalTime = flight3ScheduledArrivalTime + flight3Delay +
                                                  TimeUnit.MINUTES.toMillis((0..15).random().toLong())

                    flights.add(Flight(
                        flightNumber = "SF303",
                        origin = "San Francisco",
                        destination = "Seattle",
                        scheduledDepartureTime = flight3DepartureTime,
                        scheduledArrivalTime = flight3ScheduledArrivalTime,
                        actualDepartureTime = flight3ActualDepartureTime,
                        actualArrivalTime = flight3ActualArrivalTime,
                        date = dateTimestamp
                    ))
                }

                flightDao.insertFlights(flights)
            }
        }
    }
}