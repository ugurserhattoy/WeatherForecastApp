package com.ust.weatherforecastapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ust.weatherforecastapp.MainActivity
import com.ust.weatherforecastapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
        private const val RC_SIGN_IN = 9001
        val TAG = "LoginFragment"
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var  googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initGoogleSignInClient()
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
        btn_googleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        btn_signInWithEmail.setOnClickListener {
            loginWithEmailAndPassword(it)
        }
        // TODO: Create a Repo layer over Firebase Realtime Database when you got time!
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        MainActivity().updateUI(currentUser, this.view as View, R.id.to_forecast_fragment, this.context)
    }

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity().applicationContext, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginWithEmailAndPassword (view: View) {
//        MainActivity().hideSoftKeyboard(view)
        Log.d(TAG, "Email Login Starts")
        firebaseAuthLoginWithEmailAndPassword(et_emailSignIn.text.toString(), et_passwordSignIn.text.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    MainActivity().updateUI(user, this.view as View, R.id.to_forecast_fragment, this.context)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(mainLoginLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    MainActivity().updateUI(null, this.view as View, R.id.to_forecast_fragment, this.context)
                }

                // ...
            }
    }

    private fun firebaseAuthLoginWithEmailAndPassword(email:String, password: String){

        Log.d(TAG, "signIn:$email")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                MainActivity().updateUI(user, this.view as View, R.id.to_forecast_fragment, this.context)
            }else{
                Log.w(TAG, "signInWithEmail:failure!",it.exception)
                MainActivity().updateUI(null, this.view as View, R.id.to_forecast_fragment, this.context)
            }
        }
    }

//    fun updateUI(user: FirebaseUser?){
//        if (user != null) {
//            Navigation.findNavController(this.view as View).navigate(R.id.to_forecast_fragment)
//        }else{
//            val toast = makeText(this.context, "Log in or Create an Account.", LENGTH_SHORT)
//            toast.setGravity(Gravity.CENTER, 0, 0)
//            toast.show()
//        }
//    }

}