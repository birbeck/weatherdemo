package com.birbeck.android.weatherdemo.ui.main

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.birbeck.android.weatherdemo.R

@BindingAdapter("weatherIcon")
fun setWeatherIcon(view: ImageView, icon: String?) {
    if (icon == null) return
    view.setImageResource(
            when (icon) {
                "clear-day" -> R.drawable.ic_clear_day
                "clear-night" -> R.drawable.ic_clear_night
                "rain" -> R.drawable.ic_rain
                "snow", "sleet" -> R.drawable.ic_snow
                "wind" -> R.drawable.ic_wind
                "cloudy", "fog" -> R.drawable.ic_cloudy
                "partly-cloudy-day" -> R.drawable.ic_partly_cloudy_day
                "partly-cloudy-night" -> R.drawable.ic_partly_cloudy_night
                else -> R.drawable.ic_unknown
            }
    )
}
