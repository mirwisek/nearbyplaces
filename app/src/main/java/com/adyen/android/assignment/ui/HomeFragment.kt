package com.adyen.android.assignment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.adyen.android.assignment.viewmodels.PlacesViewModel
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.databinding.HomeFragmentBinding
import com.adyen.android.assignment.gone
import com.adyen.android.assignment.log
import com.adyen.android.assignment.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var category: Int? = null
    private val viewModel: PlacesViewModel by activityViewModels()
    private lateinit var binding: HomeFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            category = args.getInt(KEY_CATEGORY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure that we don't use resources in the background
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                val categoryPlace = getString(category!!)
                viewModel.state.collect { state ->
                    when(state) {
                        PlaceState.Idle -> {}
                        PlaceState.Loading -> {
                            binding.progress.visible()
                            binding.textHint.gone()
                        }
                        is PlaceState.Result -> {
                            binding.progress.gone()
                            binding.textHint.visible()
                            val t = StringBuilder()
                            viewModel.getFilteredPlaces(categoryPlace).forEach {
                                log("Received Data:: ${it.name} and ${it.categories[0].id}")
                                t.append(it.name)
                                t.append("\n")
                            }
                            binding.textHint.text = t.toString()
                        }
                        is PlaceState.Error -> {
                            with(binding) {
                                progress.gone()
                                textHint.visible()
                                textHint.text = "Error: ${state.error}"
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_CATEGORY = "category"

        fun newInstance(targetCategory: Int): HomeFragment {
            val args = Bundle().apply {
                putInt(KEY_CATEGORY, targetCategory)
            }

            return HomeFragment().apply {
                arguments = args
            }
        }
    }
}