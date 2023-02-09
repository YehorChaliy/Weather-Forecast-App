package com.example.weatherapp

import java.util.concurrent.locks.Condition

data class DayItem(
    val city: String,
    val country: String,
    val time: String,
    val condition: String,
    val imageUrl: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val hour: String
)
