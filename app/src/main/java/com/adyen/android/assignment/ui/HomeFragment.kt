package com.adyen.android.assignment.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.HomeFragmentBinding
import com.adyen.android.assignment.gone
import com.adyen.android.assignment.log
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.ui.adapters.PlacesRecyclerViewAdapter
import com.adyen.android.assignment.utils.ImageUtils
import com.adyen.android.assignment.viewmodels.PlacesViewModel
import com.adyen.android.assignment.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val viewModel: PlacesViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesRecyclerViewAdapter

    private var _binding: HomeFragmentBinding? = null
    // The assertion is made because during usage inside onCreateView it's not null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity

        placesAdapter =
            PlacesRecyclerViewAdapter(ImageUtils.getIconSize(requireContext())) { placeClicked ->
                activity.onPlaceClicked(placeClicked)
            }
        with(binding.list) {
            adapter = placesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        // Ensure that we don't use resources in the background
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    when (state) {
                        PlaceState.Idle -> {}
                        PlaceState.Loading -> {
                            with(binding) {
                                progress.visible()
                                textHint.gone()
                            }
                        }
                        is PlaceState.Result -> renderPlacesResult(state.places)
                        is PlaceState.Error -> renderError(state.error)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderError(error: Throwable?) {
        with(binding) {
            progress.gone()
            textHint.visible()
            textHint.text = "Error: $error"
        }
    }

    private fun renderPlacesResult(places: List<Result>?) {
        with(binding) {
            progress.gone()
            places?.let { list ->
                if (list.isEmpty()) {
                    textHint.text = getString(R.string.empty_list_message)
                    textHint.visible()
                } else {
                    textHint.gone()
                    list.forEach { result ->
                        val l = result.geocodes.main

                        log("R: ${result.name} :: ${l.latitude}, ${l.longitude} => ${result.location.address}")
                    }
                    placesAdapter.updateList(list)
                }
            }
        }
    }

    // :) Avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}