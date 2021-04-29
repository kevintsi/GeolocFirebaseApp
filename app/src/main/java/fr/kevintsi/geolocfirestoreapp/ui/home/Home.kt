package fr.kevintsi.geolocfirestoreapp.ui.home

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fr.kevintsi.geolocfirestoreapp.adapter.LocationListAdapter
import fr.kevintsi.geolocfirestoreapp.databinding.HomeFragmentBinding
import fr.kevintsi.geolocfirestoreapp.models.LocationModel

class Home : Fragment(), LocationListener {
    private lateinit var auth: FirebaseAuth

    companion object {
        fun newInstance() = Home()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment:onCreateView", "Start onCreateView")
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val binding  = HomeFragmentBinding.inflate(inflater)

        auth = Firebase.auth

        checkLocation()

        viewModel.updateLocationResult.observe(viewLifecycleOwner){
            Log.d("HomeFragment", "Inside updateLocationResult observe")
            if(it) {
                Toast.makeText(context, "Location added with success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Location not added", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.locationsSuccess.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Inside locationsSuccess observe")
            val adapter = LocationListAdapter(it) {
                viewModel.deleteLocation(it.id.toString(), auth.currentUser)
            }
            binding.recylerLocations.adapter = adapter
        }

        binding.logBtn.setOnClickListener {
            auth.signOut()
            val action = HomeDirections.actionHome2ToLogin()
            findNavController().navigate(action)
        }

        viewModel.deleteLocationResult.observe(viewLifecycleOwner) {
            Log.d("HomeFragment", "Inside deleteLocationResult observe")
            if(it) {
                Toast.makeText(context, "Location deleted with success", Toast.LENGTH_SHORT)
            } else {
                Toast.makeText(context, "Failed at deleting location", Toast.LENGTH_SHORT)
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("HomeFragment:onActivityCreated", "Start onActivityCreated")
        viewModel.getAllLocations(auth.currentUser)

    }

    override fun onStart() {
        super.onStart()
    }

    private fun checkLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0F, this)
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    override fun onLocationChanged(location: Location) {
        if(auth.currentUser != null) {
            Log.d("Home:onLocationChanged", "Longitude : ${location.longitude}, Latitude : ${location.latitude}")
            viewModel.updateLocation(LocationModel(null, location.longitude, location.latitude), auth.currentUser)
            viewModel.getAllLocations(auth.currentUser)
        }
    }
}