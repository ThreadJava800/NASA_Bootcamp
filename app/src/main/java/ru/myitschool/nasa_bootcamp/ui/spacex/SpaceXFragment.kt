package ru.myitschool.nasa_bootcamp.ui.spacex

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_asteroid_radar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xml.sax.ErrorHandler
import ru.myitschool.nasa_bootcamp.MainActivity
import ru.myitschool.nasa_bootcamp.R
import ru.myitschool.nasa_bootcamp.data.model.SxLaunchModel
import ru.myitschool.nasa_bootcamp.databinding.FragmentSpacexBinding
import ru.myitschool.nasa_bootcamp.ui.animation.animateIt
import ru.myitschool.nasa_bootcamp.utils.*
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class SpaceXFragment : Fragment() {

    private lateinit var spaceXLaunchAdapter: SpaceXLaunchAdapter
    private val launchesViewModel: SpaceXViewModel by viewModels<SpaceXViewModelImpl>()

    private var _binding: FragmentSpacexBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpacexBinding.inflate(inflater, container, false)
        spaceXLaunchAdapter = SpaceXLaunchAdapter()
        binding.launchesRecycle.adapter = spaceXLaunchAdapter
        binding.launchesRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.launchesRecycle.setHasFixedSize(false)


        DimensionsUtil.dpToPx(requireContext(), 5).let {
            DimensionsUtil.setMargins(
                binding.toolBar,
                it,
                DimensionsUtil.getStatusBarHeight(resources) + it,
                it,
                it
            )
        }


        launchesViewModel.getSpaceXLaunches().observe(viewLifecycleOwner) { data ->
            when (data) {
                is Data.Ok -> {
                    spaceXLaunchAdapter.submitList(data.data)
                    (activity as MainActivity).main_loading?.stopLoadingAnimation()
                }
                is Data.Error -> {
                    if (data.message == "noInternet"){
                        Toast.makeText(requireContext(),"no internet connection",Toast.LENGTH_SHORT).show()
                    }
                }
                is Data.Local -> {
                    spaceXLaunchAdapter.submitList(data.data)
                    (activity as MainActivity).main_loading?.stopLoadingAnimation()
                }
                Data.Loading -> {

                }
            }

        }

        launchesViewModel.getErrorHandler().observe(viewLifecycleOwner) { error ->
            if (error == Status.ERROR) {
                Log.d("LAUNCH_NOT_LOADED_TAG", "No internet connection")
                binding.launchesRecycle.visibility = View.GONE
                //binding.errorIcon.visibility = View.VISIBLE
                binding.explore.getBackground().setColorFilter(
                    resources.getColor(R.color.disabled_button),
                    PorterDuff.Mode.SRC_ATOP
                );
            } else if ((error == Status.LOADING)) {
                (activity as MainActivity).main_loading?.startLoadingAnimation()
                binding.launchesRecycle.visibility = View.GONE
                //binding.errorIcon.visibility = View.GONE
                binding.explore.getBackground().setColorFilter(
                    resources.getColor(R.color.disabled_button),
                    PorterDuff.Mode.SRC_ATOP
                );
            } else {
                binding.launchesRecycle.visibility = View.VISIBLE
                 binding.explore.getBackground().setColorFilter(resources.getColor(R.color.enabled_button),
                    PorterDuff.Mode.SRC_ATOP
                );
            }

            val animation = animateIt {
                animate(binding.spaceXLogo) animateTo {
                    topOfItsParent(marginDp = 35f)
                    leftOfItsParent(marginDp = 10f)
                    scale(0.8f, 0.8f)
                }


                animate(binding.explore) animateTo {
                    rightOfItsParent(marginDp = 20f)
                    sameCenterVerticalAs(binding.spaceXLogo)
                }

                animate(binding.background) animateTo {
                    height(
                        resources.getDimensionPixelOffset(R.dimen.height),
                        horizontalGravity = Gravity.LEFT, verticalGravity = Gravity.TOP
                    )
                }
            }

            binding.launchesRecycle.setOnScrollChangeListener { v, _, scrollY, _, _ ->
                val percent = scrollY * 1f
                animation.setPercent(percent)
            }

            val navController = findNavController()

            binding.explore.setOnClickListener {
                val action = SpaceXFragmentDirections.actionSpaceXFragmentToExploreFragment()
                navController.navigate(action)
            }

        }

        return binding.root

    }

    override fun onPause() {
        (activity as MainActivity).main_loading?.stopLoadingAnimation()
        super.onPause()
    }
}