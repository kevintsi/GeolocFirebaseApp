package fr.kevintsi.geolocfirestoreapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.kevintsi.geolocfirestoreapp.databinding.ActivityMainBinding
import fr.kevintsi.geolocfirestoreapp.ui.login.LoginDirections

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.d("MainActivity:onCreate", "Start onCreate")
        auth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
    }
}