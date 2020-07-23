package com.ust.weatherforecastapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.ust.weatherforecastapp.interior.LocationPermissionNotGrantedException
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

lateinit var locationLatLong: List<Double>

private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 35

class MainActivity : AppCompatActivity(), DIAware {

    val TAG = "MainActivity"

    override val di by closestDI()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
        }
    }

    private lateinit var  navController: NavController

    lateinit var locationManager: LocationManager
    private var hasGPS = false
    private var hasNetwork = false
    private var locationGPS: Location? = null
    private var locationNetwork: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            locationLatLong = getLastLocation()
        }catch (e:LocationPermissionNotGrantedException){}

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)

        requestLocationPermission()

        if (hasLocationPermission()) {
            bindLocationManager()
        }else
            requestLocationPermission()

    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(): List<Double> {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasLocationPermission()) {
            if (hasGPS || hasNetwork) {
                if (hasGPS) {
                    Log.d(TAG, "hasGPS")
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        50000,
                        0F,
                        object : LocationListener {
                            override fun onLocationChanged(p0: Location) {
                                if (p0 != null) {
                                    locationGPS = p0
                                    Log.d(TAG, "locationGPS1: $locationGPS")
                                    Log.d(
                                        TAG, "locationGPS1: " + locationGPS!!.longitude + " | "
                                                + locationGPS!!.latitude
                                    )

                                }
                            }
                        })
                    val localGPSLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGPSLocation != null) {
                        locationGPS = localGPSLocation
                        Log.d(TAG, "locationGPS2: $locationGPS")
                        return listOf(
                            (locationGPS!!.latitude),
                            locationGPS!!.longitude
                        )
                    }
                }
                if (hasNetwork) {
                    Log.d(TAG, "hasNetwork")
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        50000,
                        0F,
                        object : LocationListener {
                            override fun onLocationChanged(p0: Location) {
                                if (p0 != null) {
                                    locationNetwork = p0
                                    Log.d(TAG, "locationNetwork1: $locationNetwork")
                                }
                            }
                        })
                    val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) {
                        locationNetwork = localNetworkLocation
                        Log.d(TAG, "locationNetwork2: $locationNetwork")
                        return listOf(
                            (locationNetwork!!.latitude),
                            locationNetwork!!.longitude
                        )
                    }
                }
                if (locationGPS != null && locationNetwork != null) {
                    if (locationGPS!!.accuracy >= locationNetwork!!.accuracy) {
                        Log.d(TAG, "NetworkLatitude: " + locationNetwork!!.latitude)
                        Log.d(TAG, "NetworkLongitude: " + locationNetwork!!.longitude)
                    } else {
                        Log.d(TAG, "GPSLatitude: " + locationGPS!!.latitude)
                        Log.d(TAG, "GPSLongitude: " + locationGPS!!.longitude)
                        return listOf(
                            (locationGPS!!.latitude),
                            locationGPS!!.longitude
                        )
                    }
                }
            } else {
                requestLocationPermission()
            }
        }else{
            requestLocationPermission()
            throw LocationPermissionNotGrantedException()
        }
        if (locationGPS==null) {
            requestLocationPermission()
        }
        return listOf((locationGPS!!.latitude),locationGPS!!.longitude)

    }
    
    fun updateUI (user: FirebaseUser?, view: View, resIdToNavigateTo:Int, mContext: Context?) {
        if (user != null) {
            Navigation.findNavController(view).navigate(resIdToNavigateTo)
        }else{
            val toast =
                Toast.makeText(mContext, "Log in or Create an Account.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSION_ACCESS_COARSE_LOCATION
        )
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindLocationManager() {
        LifecycleBoundLocationManager(
            this,
            fusedLocationProviderClient, locationCallback
        )
        getLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                bindLocationManager()
            else
                Toast.makeText(this, "Please, set location manually in settings", Toast.LENGTH_LONG).show()
        }
    }

//    fun hideSoftKeyboard (view: View?) {
//        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
//    }
}