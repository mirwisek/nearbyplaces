package com.adyen.android.assignment.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.FragmentPlaceItemBinding
import com.adyen.android.assignment.gone
import com.adyen.android.assignment.log
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

    fun updateList(values: List<Result>) {
        list = values
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
                tvDistance.text = "${placeItem.distance} meters away"

                try {
                    val url = with(placeItem.categories[0].icon) {
                        prefix + iconSize + suffix
                    }
                    Picasso.get().load(url)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivThumbnail)
                } catch (e: IndexOutOfBoundsException) {
                    log("No categories for this place item: ${placeItem.name}")
                }
                root.setOnClickListener {
                    onClick?.invoke(placeItem)
                }
            }
        }
    }


}