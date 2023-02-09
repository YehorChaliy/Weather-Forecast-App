package com.example.weatherapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//класс для вывода информации при переключении между вкладками hours and days

class VpAdapter(fa: FragmentActivity, private val list: List<Fragment>) : FragmentStateAdapter(fa) {
    // передаємо кількість вкладок (tabItems)
    override fun getItemCount(): Int {
        return list.size
    }

    //передаємо порядковий номер (Item) фрагменту, отримуємо цей фрагмент
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}