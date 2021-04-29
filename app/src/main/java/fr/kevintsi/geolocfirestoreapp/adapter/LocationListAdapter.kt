package fr.kevintsi.geolocfirestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import fr.kevintsi.geolocfirestoreapp.databinding.LocationItemBinding
import fr.kevintsi.geolocfirestoreapp.models.LocationModel


class LocationListAdapter(
    private val locationModelList: MutableList<LocationModel>?,
    private val onRubbishClick: (location: LocationModel) -> Unit?
) : RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>(), EventListener<QuerySnapshot> {
    private val mQuery: Query? = null
    private val mRegistration: ListenerRegistration? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationViewHolder {
        return LocationViewHolder(
            LocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        locationModelList?.get(position)?.let { holder.bind(it) }
    }

    inner class LocationViewHolder(private val binding: LocationItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ){
        fun bind(locationModel: LocationModel) {
            binding.location = locationModel
            binding.imgRubbish.setOnClickListener {
                onRubbishClick(locationModel)
            }
        }
    }

    override fun getItemCount(): Int = locationModelList?.size!!

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
// Handle errors
        // Handle errors
        if (error != null) {
            Log.w("LocationListAdapter:OnEvent", "onEvent:error", error)
            return
        }

        // Dispatch the event

        // Dispatch the event
        for (change in value?.documentChanges!!) {
            // Snapshot of the changed document
            val snapshot: DocumentSnapshot = change.document
            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    onDocumentAdded(change)
                }
                DocumentChange.Type.MODIFIED -> {
                    onDocumentModified(change)
                }
                DocumentChange.Type.REMOVED -> {
                    onDocumentRemoved(change)
                }
            }
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        locationModelList?.add(
            change.newIndex, LocationModel(
                change.document.get("id").toString(),
                change.document.get("longitude").toString().toDouble(),
                change.document.get("latitude").toString().toDouble()
            )
        )
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex === change.newIndex) {
            // Item changed but remained in same position
            locationModelList?.set(
                change.oldIndex, LocationModel(
                    change.document.get("id").toString(),
                    change.document.get("longitude").toString().toDouble(),
                    change.document.get("latitude").toString().toDouble()
                )
            )
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            locationModelList?.removeAt(change.oldIndex)
            locationModelList?.add(
                change.newIndex, LocationModel(
                    change.document.get("id").toString(),
                    change.document.get("longitude").toString().toDouble(),
                    change.document.get("latitude").toString().toDouble()
                )
            )
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        locationModelList?.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

}