package com.ust.weatherforecastapp.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.ust.weatherforecastapp.MainActivity
import com.ust.weatherforecastapp.R
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() =
            RegisterFragment()
        val TAG = "RegisterFragment"
    }

    private lateinit var viewModel: RegisterViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        // TODO: Use the ViewModel
        val currentUser = auth.currentUser
        btn_registerWithEmail.setOnClickListener {
            registerToFirebaseAuthThrough(et_emailRegister.text.toString(), et_passwordRegister.text.toString(), it)
        }
    }

    private fun registerToFirebaseAuthThrough(email:String, password:String, view: View) {
        Log.d(TAG, "registering Starts")
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                MainActivity().updateUI(user, view, R.id.to_forecast_fragment, this.context)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", it.exception)
                Toast.makeText(
                    this.context, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                MainActivity().updateUI(null, view, R.id.to_forecast_fragment, this.context)
            }
        }
    }
}