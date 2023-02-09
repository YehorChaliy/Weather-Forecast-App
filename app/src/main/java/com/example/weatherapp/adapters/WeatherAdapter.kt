package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

//ListAdapter класс упрощающий работу с recycleView
//подключаем Listener, обработчик событий
class WeatherAdapter(val listener: Listener?): ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    //класс холдер содержит ссылки на элементы за экраном, что пролистывается.
    //переменные view содержат значения каждого cardView
    class Holder(view: View, val listener: Listener?) : RecyclerView.ViewHolder(view){
        // в binding передаем список из елементов view
        val binding = ListItemBinding.bind(view)
        // переменную делаем глобальной и изначально она может быть null
        var itemTemp: WeatherModel? = null


        init {
            itemView.setOnClickListener {
                // если itemTemp не равен null, запускается функция и выдает it1, т.е. ItemTemp но уже не пустой
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherModel) = with(binding) {
            itemTemp = item
            data.text = item.time
            condition.text = item.condition
            temperatureForcastType.text = if(item.currentTemp.isEmpty()) { "H:${item.maxTemp}° / L:${item.minTemp}°" }
            else "${item.currentTemp.toFloat().toInt().toString()}°"
            Picasso.get().load("https:" + item.imageUrl).into(imageForecast)
        }
    }

    //класс проверяющий елементы на совпадение, а так же cardView
    class Comparator : DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    //функция запускается столько сколько елементов в списке и создает holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    //прописыввем как холдер будет заполняться
    // логика такая: приходит холдер с return (в начале на нулевой позиции, проверяется через елемент position порядковый номер ListItemBinding)
    // и заполняется на своей позиции теми елементами, что прописани в функции bind
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: WeatherModel)
    }
}