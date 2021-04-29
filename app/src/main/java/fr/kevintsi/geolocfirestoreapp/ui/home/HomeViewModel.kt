package fr.kevintsi.geolocfirestoreapp.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import fr.kevintsi.geolocfirestoreapp.models.LocationModel


class HomeViewModel : ViewModel() {
    var updateLocationResult = MutableLiveData<Boolean>()
    var locationsSuccess = MutableLiveData<MutableList<LocationModel>?>()
    var deleteLocationResult = MutableLiveData<Boolean>()
    var db = FirebaseFirestore.getInstance()

    fun updateLocation(location: LocationModel, user: FirebaseUser) {
        val user_ = HashMap<String, String>()
        val location_ = HashMap<String, Double>()

        user_.put("Uid", user.uid)
        user_.put("Display name", user.displayName)

        location_.put("longitude", location.longitude)
        location_.put("latitude", location.latitude)

        db.collection("users").document(user.uid).set(user_).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                db.document("users/${user.uid}").collection("locations").add(location_).addOnCompleteListener { task ->
                    updateLocationResult.value = task.isSuccessful
                }
            } else {
                updateLocationResult.value = false
            }
        }
    }

    fun getAllLocations(user : FirebaseUser) {
        locationsSuccess.value = mutableListOf()
        val list = mutableListOf<LocationModel>()
        /*db.collection("users")
            .document(user.uid)
            .collection("locations")
            .get()
            .addOnSuccessListener {
                if(it.documents.size != 0) {
                    for (document in it){
                        Log.d("HomeViewModel:getAllLocations", "${document.data["longitude"]}")
                        Log.d("HomeViewModel:getAllLocations", "$list")
                        list.add(LocationModel(document.id, longitude = document.data["longitude"].toString().toDouble(),
                            latitude = document.data["latitude"].toString().toDouble()))
                    }
                }

                locationsSuccess.value = list
        }*/

        db.collection("users")
        .document(user.uid)
        .collection("locations").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("HomeViewModel:getAllLocations", "Listen failed.", error)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    list.add(LocationModel(doc.id, longitude = doc.data["longitude"].toString().toDouble(),
                        latitude = doc.data["latitude"].toString().toDouble()))
                    }
                    locationsSuccess.value = list
                }
    }

    fun deleteLocation(documentId : String, user : FirebaseUser) {
        Log.d("HomeViewModel:deleteLocation", "Start deletelocation for document $documentId")
        db.collection("users")
        .document(user.uid)
        .collection("locations")
        .document(documentId)
        .delete().addOnCompleteListener {
        Log.d("HomeViewModel:deleteLocation", "Complete")
        deleteLocationResult.value = it.isSuccessful
        }
    }
}