package com.molo4ko.tempbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.molo4ko.tempbinding.databinding.ItemWeatherBinding


class WeatherAdapter(
    private val list: MutableList<WeatherCard>
) : RecyclerView.Adapter<WeatherAdapter.Holder>() {

    class Holder(val binding: ItemWeatherBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemWeatherBinding.inflate(inflater, parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.weather = list[position]
    }

    override fun getItemCount() = list.size

    fun add(item: WeatherCard) {
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}