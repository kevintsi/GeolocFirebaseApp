package fr.kevintsi.geolocfirestoreapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import fr.kevintsi.geolocfirestoreapp.databinding.LocationItemBinding
import fr.kevintsi.geolocfirestoreapp.models.LocationModel


class LocationListAdapter(
    var locationModelList: MutableList<LocationModel>?,
    val onRubbishClick: (location: LocationModel) -> Unit?
) : RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>() {
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

}