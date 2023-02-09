package com.example.weatherapp.fragments

import android.Manifest
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.DialogManager
import com.example.weatherapp.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.example.weatherapp.R
import com.example.weatherapp.adapters.VpAdapter
import com.example.weatherapp.adapters.WeatherModel
import com.example.weatherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "a5060863b1954de3877222723230601"
//створював файл через new/Fragments
class MainFragment : Fragment() {

    private lateinit var fLocationClient: FusedLocationProviderClient

            private val fList = listOf(
            //указываем в правильном порядке
            HoursFragment.newInstance(),
            DaysFragment.newInstance()
        )
    // список который сохраняет названия при слайде
        private val tList = listOf(
            "Hours",
            "Days"
        )

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    // создаем объект класса MainViewModel
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    // функции что выполняются один раз при запуске приложения
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
    }

    // если пользователь все таки включил GPS, то в приложении обновится информация
    // ибо изначально после включения GPS и возвращения в приложение информация не обновляется
    override fun onResume() {
        super.onResume()
        checkGPS()
    }

    //
    private fun init() = with(binding){

        // инициализируем переменную для получения месторасположения юзера
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter

        // используем для плавного переключения (слайда) между вкладками
        // id элементов tabLayout and vp берем с fragment_main, т.к. указали with (binding)
        TabLayoutMediator(tabLayout, vp){
                tab, pos -> tab.text = tList[pos]
        }.attach()

        // листенер по нажатию на кнопку (иконку обновления) обновляются данные
        iconRenew.setOnClickListener {
            // при нажатии на иконку обновления перекидывает в начало во вкладку HOURS
            tabLayout.selectTab(tabLayout.getTabAt(0))
            if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(activity, "Location permission denied. Allow it or find your city using search", Toast.LENGTH_LONG).show()
            }
            checkGPS()
        }

        // листенер по нажатию на кнопку (иконку поиска) вводим город прогноз которого хотим получить
        iconSearch.setOnClickListener {
            DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    // проверка name на null
                    name?.let { it1 -> requestWeatherData(it1) }
                }
            })
        }
    }

    private fun checkGPS() {
        if (isLocationEnabled()){
            getLocation()
        }
        else {
            DialogManager.locationSettingDialog(requireContext(), object : DialogManager.Listener{

                // переписываем OnClock (функц. интерфейса), то есть по нажатию на кнопку POSITIVE отправляем пользователя в настройки GPS
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
            )}
    }

    // функция проверяющая включен ли GPS
    private fun isLocationEnabled() : Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {

        // это самый простой вариант вывода сообщения о выключенном GPS.
        /*if (!isLocationEnabled()){
            Toast.makeText(requireContext(),"GPS disabled!", Toast.LENGTH_SHORT).show()
            return
        }*/
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // означает что не пропускает дальше
            return
        }
        // координаты что получили вводим в функцию requestWeatherData
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token).addOnCompleteListener{
            requestWeatherData("${it.result.latitude},${it.result.longitude}")
        }
    }

    private fun updateCurrentCard() = with (binding){

        // viewLifecycleOwner специальный класс, который знает жизненный цикл в данном случае фрагментов, что упрощает мне жизнь :)
        // с помощью метода observe ожидаем данные
        model.liveDataCurrent.observe(viewLifecycleOwner){
            val maxMinTemp = "H:${it.maxTemp}° L:${it.minTemp}°"
            dataTime.text = it.time
            name0fCity.text = it.city
            temperature.text = if(it.currentTemp.isEmpty()) { maxMinTemp } else "${it.currentTemp}°"
            conditionDescribe.text = it.condition
            highestAndLowest.text = if(it.currentTemp.isEmpty()) "" else maxMinTemp
            // используем библиотеку пикассо
            Picasso.get().load("https:" + it.imageUrl).into(conditionImage)
        }
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Location permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    // перевірка на функцію в файлі Extentions, якщо значення вже true то функція permissionListener не запускається
    private fun checkPermission(){
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))
            //регістріруєм callback в якому чекаємо на результат
            permissionListener()
        // запускаємо діалог в якому питаємо користувача чи дає він згоду
        pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestWeatherData(city: String) {
       val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
               API_KEY +
               "&q=" +
               city +
               "&days=7&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "Error: $error")
            },
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {

        // в mainObject хранится весь JSON файл
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem:WeatherModel){

        // создаем объект класса WeatherModel и передаем ему значения с файла JSON
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )
        // с помощью метода value отправляем данные
        model.liveDataCurrent.value = item
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel (
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
                    )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}