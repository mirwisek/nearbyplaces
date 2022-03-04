package com.adyen.android.assignment.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.FragmentPlaceItemBinding
import com.adyen.android.assignment.gone
import com.adyen.android.assignment.visible
import com.squareup.picasso.Picasso

class PlacesRecyclerViewAdapter(
    private val iconSize: String,
    private val onClick: ((Result) -> Unit)? = null
) : RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder>() {

    private var list: List<Result> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentPlaceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(values: List<Result>) {
        list = values
        // We could use DiffUtils but to avoid over complicating this project, I would stick to this.
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: FragmentPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(placeItem: Result) {
            with(binding) {
                tvName.text = placeItem.name
                if (placeItem.location.address == null) {
                    tvAddress.gone()
                } else {
                    tvAddress.visible()
                    tvAddress.text = placeItem.location.address
                }
                var distance: Float = placeItem.distance.toFloat()
                if(distance >= 1000) {
                    distance /= 1000F
                    tvDistance.text = "%.1f km away".format(distance)
                } else
                    tvDistance.text = "${placeItem.distance} m away"

                try {
                    val url = with(placeItem.categories[0].icon) {
                        prefix + iconSize + suffix
                    }
                    Picasso.get().load(url)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivThumbnail)
                } catch (e: IndexOutOfBoundsException) {
                    // Error caught, no reaction as we have put PlaceHolder Icon
                }
                root.setOnClickListener {
                    onClick?.invoke(placeItem)
                }
            }
        }
    }


}