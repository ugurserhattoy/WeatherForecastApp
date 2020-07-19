package com.ust.weatherforecastapp.forecast

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import com.ust.weatherforecastapp.R
import com.ust.weatherforecastapp.ScopedFragment
import com.ust.weatherforecastapp.data.remote.ConnectivityInterceptorImpl
import com.ust.weatherforecastapp.data.remote.RemoteWeatherService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forecast_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf


class ForecastFragment : ScopedFragment(), DIAware {
    override val di by closestDI()
    private val viewModelFactory: ForecastViewModelFactory by instance()


    val TAG = "ForecastFragment"
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

        navBar.visibility = View.INVISIBLE

        btn_logout.setOnClickListener {
            signOut()
            navBar.visibility = View.VISIBLE
        }

        ib_settings.setOnClickListener {
            Navigation.findNavController(this.view as View).navigate(R.id.to_settingsFragment)
        }

        bindUI()

//        val geocoder: Geocoder
//        val addresses: List<Address>
//        geocoder = Geocoder(this.context, Locale.getDefault())
//
//        addresses = geocoder.getFromLocation(
//            latitude,
//            longitude,
//            1
//        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//
//        val address: String = addresses[0]
//            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//        val city: String = addresses[0].getLocality()
//        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()
//        val postalCode: String = addresses[0].getPostalCode()
//        val knownName: String = addresses[0].getFeatureName()
    }



    private fun bindUI() = launch{
        val currentWeather = viewModel.weather.await()

        val currentWeatherWeather = viewModel.currentWeatherWeather.await()

        currentWeatherWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            updateWeatherConditionIcon("4x", it.icon)
        })

//        val weatherLocation = viewModel.weatherLocation.await()



//        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
//            if (location == null) return@Observer
//            val geocoder: Geocoder
//            val addresses: List<Address>
//            geocoder = Geocoder(context?.applicationContext, Locale.getDefault())
//
//            addresses = geocoder.getFromLocation(
//                location.lat,
//                location.lon,
//                1
//            )
//            updateLocation(addresses[0].getLocality())
//        })

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

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
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

}