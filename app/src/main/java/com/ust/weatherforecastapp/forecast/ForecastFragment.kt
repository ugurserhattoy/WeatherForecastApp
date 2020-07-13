package com.ust.weatherforecastapp.forecast

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ust.weatherforecastapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forecast_fragment.*


class ForecastFragment : Fragment() {

    companion object {
        fun newInstance() =
            ForecastFragment()
        val TAG = "ForecastFragment"
    }

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
        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        // TODO: Use the ViewModel
        navBar.visibility = View.INVISIBLE

        btn_logout.setOnClickListener {
            signOut()
            navBar.visibility = View.VISIBLE
        }
    }

    private fun signOut() {
        Log.d(TAG, "signOut Started")
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        bottomNavigationView?.visibility = View.VISIBLE
        Navigation.findNavController(this.view as View).navigate(R.id.to_login_action)
    }

}