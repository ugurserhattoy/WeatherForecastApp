package com.ust.weatherforecastapp.forecast

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.ust.weatherforecastapp.R
import com.ust.weatherforecastapp.ScopedFragment
import com.ust.weatherforecastapp.locationLatLong
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forecast_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

lateinit var contextJ: Context

class ForecastFragment : ScopedFragment(), DIAware, OnMapReadyCallback {
    override val di by closestDI()
    private val viewModelFactory: ForecastViewModelFactory by instance()

    private val TAG = "ForecastFragment"
    private lateinit var viewModel: ForecastViewModel
    private lateinit var  navBar: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        return inflater.inflate(R.layout.forecast_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ForecastViewModel::class.java)

        contextJ = this.context as Context

        navBar.visibility = View.INVISIBLE

        btn_logout.setOnClickListener {
            signOut()
            navBar.visibility = View.VISIBLE
        }

        ib_settings.setOnClickListener {
            Navigation.findNavController(this.view as View).navigate(R.id.to_settingsFragment)
        }

        bindUI()

    }



    private fun bindUI() = launch{
        val currentWeather = viewModel.weather.await()
        val currentWeatherWeather = viewModel.currentWeatherWeather.await()
        val locationEntry = viewModel.locationEntry.await()

        currentWeatherWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            updateWeatherConditionIcon("4x", it.icon)
            updateWeatherDescription(it.description.toUpperCase())

        })

        locationEntry.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            updateLocation(it.name, it.lat, it.lon)
        })

        currentWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            updateDateToToday()
            updateHumidity(it.humidity)
            updateTemperatures(it.temp, it.feelsLike)
            updateVisibility(it.visibility)
            updateWindSpeed(it.windSpeed)

        })
    }

    private fun updateWeatherConditionIcon (multiplier: String = "4x", icon: String = "02d") {
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@$multiplier.png")
            .into(ic_weather_condition)
    }

    private fun updateWeatherDescription (description: String) {
        tv_weather_description.text = description
    }

    private fun updateLocation(location: String, lat: Double, lon:Double) {
        textView.text = location
        var callback = OnMapReadyCallback { googleMap ->
            val mLatLng = LatLng(lat, lon)
            googleMap.addMarker(MarkerOptions().position(mLatLng).title(location))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 10f))
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun updateDateToToday() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        tv_temperature.text = "$temperature°C"
        tv_feelsLike.text = "Feels Like ${feelsLike}°C"
    }

    private fun updateHumidity(humidity: Double) {
        tv_humidity.text = "Humidity: ${humidity}%"
    }

    private fun updateWindSpeed(windSpeed: Double) {
        tv_wind.text = "Wind Speed: ${windSpeed}m/s"
    }

    private fun updateVisibility(visibility: Int) {
        tv_visibility.text = "Visibility: ${visibility}m"
    }

    private fun signOut() {
        Log.d(TAG, "signOut Started")
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        bottomNavigationView?.visibility = View.VISIBLE
        Navigation.findNavController(this.view as View).navigate(R.id.to_login_action)
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("Not yet implemented")
    }

}