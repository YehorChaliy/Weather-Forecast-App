package com.example.weatherapp.fragments

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.adapters.WeatherAdapter
import com.example.weatherapp.adapters.WeatherModel
import com.example.weatherapp.databinding.FragmentDaysBinding
import com.example.weatherapp.databinding.FragmentHoursBinding

class DaysFragment : Fragment(), WeatherAdapter.Listener {

    private lateinit var adapter: WeatherAdapter
    private lateinit var binding: FragmentDaysBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        // ожидаем передачу списка
        model.liveDataList.observe(viewLifecycleOwner) {
            // метод subList позволяет отображать указанные объекты, в нашем случае без первого
            adapter.submitList(it.subList(1, it.size))
        }
    }

    private fun init() = with(binding){

        //используем this@DaysFragment ибо ссылаемся на наш объект. Просто this ссылается на binding
        adapter = WeatherAdapter(this@DaysFragment)
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    // метод класса Listener
    override fun onClick(item: WeatherModel) {

        model.liveDataCurrent.value = item
    }
}