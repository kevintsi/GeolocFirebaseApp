package fr.kevintsi.geolocfirestoreapp.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import fr.kevintsi.geolocfirestoreapp.adapter.LocationListAdapter
import fr.kevintsi.geolocfirestoreapp.models.LocationModel


class HomeViewModel : ViewModel(){
    var updateLocationResult = MutableLiveData<Boolean>()
    var locationsSuccess = MutableLiveData<MutableList<LocationModel>?>()
    var deleteLocationResult = MutableLiveData<Boolean>()
    var db = FirebaseFirestore.getInstance()

    fun updateLocation(location: LocationModel, user: FirebaseUser) {
        val newUser = HashMap<String, String>()
        val newLocation = HashMap<String, GeoPoint>()

        newUser["Uid"] = user.uid
        newUser["email"] = user.email

        newLocation["coordinate"] = GeoPoint(location.latitude, location.longitude)

        db.collection("users").document(user.uid).set(newUser).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                db.document("users/${user.uid}").collection("locations").add(newLocation).addOnCompleteListener { task ->
                    updateLocationResult.value = task.isSuccessful
                }
            } else {
                updateLocationResult.value = false
            }
        }
    }

    fun getAllLocations(user: FirebaseUser) {
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
                val list = mutableListOf<LocationModel>()
                Log.d("HomeViewModel:getAllLocations", "Listen success. $value")
                if (value != null) {
                    Log.d("HomeViewModel:getAllLocations", "Documents -> ${value.documents.size}")
                    for (doc in value.documents) {
                        Log.d("HomeViewModel:getAllLocations", "${doc.data}")
                        val coordinate : GeoPoint  = doc.data?.get("coordinate") as GeoPoint
                        list.add(LocationModel(doc.id, longitude = coordinate.longitude,
                            latitude = coordinate.latitude))
                    }
                }

                    Log.d("HomeViewModel:getAllLocations", "Number of elements in list temporary -> ${list}")
                    locationsSuccess.postValue(list)
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