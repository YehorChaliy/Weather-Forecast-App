package com.example.weatherapp.fragments

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

// функція на перевірку чи дав юзер згоду на передачу геоданних... у return отримуємо спочатку значення p через checkSelfPermission
// і якщо отримане значення дорівнює 0(PERMISSION_GRANTED), повертається true, в іншому випадку false

fun Fragment.isPermissionGranted(p: String): Boolean {
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) == PackageManager.PERMISSION_GRANTED
}