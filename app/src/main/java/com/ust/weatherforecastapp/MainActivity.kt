package com.ust.weatherforecastapp

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var  navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
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

    override fun onBackPressed() {}

//    fun hideSoftKeyboard (view: View?) {
//        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
//    }
}