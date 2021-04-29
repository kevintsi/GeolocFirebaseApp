package fr.kevintsi.geolocfirestoreapp.ui.login

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.kevintsi.geolocfirestoreapp.R
import fr.kevintsi.geolocfirestoreapp.databinding.HomeFragmentBinding
import fr.kevintsi.geolocfirestoreapp.databinding.LoginFragmentBinding
import java.util.concurrent.Executor

class Login : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var executor : Executor
    private lateinit var biometricPrompt : BiometricPrompt
    private lateinit var promptInfo : BiometricPrompt.PromptInfo


    companion object {
        fun newInstance() = Login()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = LoginFragmentBinding.inflate(inflater)

        auth = Firebase.auth

        Log.d("LoginFragment:onCreateView", "Start onCreateView")

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT).show()
                        Log.d("LoginFragment:signInWithEmailAndPassword", "Logged in successfully")
                        val action = LoginDirections.actionLoginToHome2()
                        view?.findNavController()?.navigate(action)
                    } else {
                        Toast.makeText(requireContext(), "Logged in failed", Toast.LENGTH_SHORT).show()
                        Log.d("LoginFragment:signInWithEmailAndPassword", "Logged in failed")
                    }
                }
            } else {
                Toast.makeText(requireContext(), "You have to fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d("LoginFragment:onResume", "Already logged in")
            biometricAuthentication()
            val action = LoginDirections.actionLoginToHome2()
            view?.findNavController()?.navigate(action)
        }else {
            Log.d("LoginFragment:onResume", "Not already logged in")
        }

    }

    private fun biometricAuthentication() {

        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(context,
                    "Authentication succeeded", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context,
                    "Authentication failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context,
                    "Authentication error : $errString", Toast.LENGTH_SHORT).show()
            }
        })

        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("MY_APP_TAG", "App can authenticate using biometrics")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.d("MY_APP_TAG", "No biometric features available on this device")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.d("MY_APP_TAG", "Biometric features are currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG)
                }
                startActivityForResult(enrollIntent, 2)
            }
        }

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        Log.d("PromptInfo", "${promptInfo.allowedAuthenticators}")

        biometricPrompt.authenticate(promptInfo)
    }

}