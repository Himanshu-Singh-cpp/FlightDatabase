# Flight Time Analysis App

A modern Android application that tracks and analyzes flight performance data, including actual flight durations and delays. This app demonstrates the use of Room database, WorkManager for background tasks, and Jetpack Compose for UI.

## Features

- **Flight Database**: Stores flight information with actual arrival/departure times and calculates delays
- **Background Data Collection**: Automatically collects flight data daily
- **Average Flight Time Analysis**: Calculates and displays average flight durations
- **Flight Details View**: Shows complete information about individual flights with delay metrics

## Architecture

The app follows MVVM architecture with the following components:

### Data Layer
- `Flight` - Entity representing flight information
- `FlightDao` - Data access interface for the Room database
- `FlightDatabase` - Room database implementation that pre-populates with one week of flight data
- `FlightRepository` - Repository that abstracts data operations

### Background Processing
- `FlightDataWorker` - WorkManager job that runs daily to collect new flight data

### Presentation Layer
- `FlightViewModel` - ViewModel providing flight data to the UI
- `MainActivity` - Main activity with Compose UI implementation

## Implementation Details

### Database Schema
The database stores flight information including:
- Flight number, origin, and destination
- Scheduled departure and arrival times
- Actual departure and arrival times (accounting for delays)
- Flight date

### Performance Analytics
The app calculates:
- Actual flight duration based on real departure/arrival times
- Departure and arrival delays
- Average flight duration for each route

### Background Jobs
- WorkManager runs a daily task to collect new flight data
- Pre-populated with one week of historical data (3 flights per day)
- Each flight simulates realistic delays between 0-60 minutes

### UI Components
- Summary cards showing average flight times by route
- Detailed flight cards with delay information
- Clean material design with appropriate color coding for delays

## Requirements

- Android Studio Meerkat (2024.3.1) or newer
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 35
- Kotlin 1.9.0+

## Libraries Used

- Jetpack Compose for UI
- Room for database operations
- WorkManager for background processing
- Kotlin Coroutines for asynchronous operations
- KSP for Room annotation processing

## Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on an emulator or physical device

The app will automatically populate the database with sample flight data on first launch and schedule a daily background job to collect new flight data.
