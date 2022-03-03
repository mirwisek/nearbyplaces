package com.adyen.android.assignment.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.FragmentMapsBinding
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.toast
import com.adyen.android.assignment.viewmodels.PlacesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnPlaceItemClickForMapListener {

    private val viewModel: PlacesViewModel by activityViewModels()

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null
    private val markers = hashMapOf<String, Marker>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mapListener = this
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    when (state) {
                        PlaceState.Idle -> {}
                        PlaceState.Loading -> {}
                        is PlaceState.Result -> {
                            state.places?.forEach { result ->
                                drawMarkerOnMap(result)
                            }
                        }
                        is PlaceState.Error -> {
                            toast("Error: ${state.error}")
                        }
                    }
                }
            }
        }
    }

    private fun drawMarkerOnMap(result: Result) {
        if(map == null) return
        val loc = result.geocodes.main
        val marker = map!!.addMarker(
            MarkerOptions()
                .position(LatLng(loc.latitude, loc.longitude))
                .title(result.name)
        )
        if (marker != null)
            markers[result.id] = marker
    }

    override fun onDetach() {
        (requireActivity() as MainActivity).mapListener = null
        super.onDetach()
    }

    // :) Avoid memory leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun showOnMap(placeItem: Result) {
        map!!.let { gMap ->
            // Clear everything on Map
            gMap.clear()
            drawMarkerOnMap(placeItem)
            val loc = placeItem.geocodes.main
            val target = CameraPosition.Builder()
                .target(LatLng(loc.latitude, loc.longitude))
                .zoom(16f)
                .build()
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(target))
        }
    }

}