package jw.adamiak.hangout.ui.profile.my_events

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.databinding.FragmentListMyEventsBinding

@AndroidEntryPoint
class MyEventsFragment: Fragment(R.layout.fragment_list_my_events),
	MyEventsAdapter.OnEventClickListener {

	private val viewModel: MyEventsViewModel by viewModels()
	private lateinit var linearLayoutManager: LinearLayoutManager
	private lateinit var eventsAdapter: MyEventsAdapter
	private lateinit var binding: FragmentListMyEventsBinding

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentListMyEventsBinding.bind(view)

		initUI()
		setupObservers()
	}

	private fun initUI(){
		eventsAdapter = MyEventsAdapter(this)
		linearLayoutManager = LinearLayoutManager(activity)
		binding.rvEventsList.layoutManager = linearLayoutManager
		binding.rvEventsList.adapter = eventsAdapter
	}

	private fun setupObservers(){
		viewModel.events.observe(viewLifecycleOwner) {
			eventsAdapter.setItems(it)
		}
		viewModel.isLoading.observe(viewLifecycleOwner) {
			binding.pbMyEvents.isVisible = it
		}
	}


	override fun onEventClicked(event: MapObject) {
		val bundle = bundleOf("event" to event)
		findNavController().navigate(R.id.action_myEventsFragment_to_eventDetailFragment, bundle)
	}

}