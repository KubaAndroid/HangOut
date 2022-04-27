package jw.adamiak.hangout.ui.events.event_detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentEventDetailsBinding
import jw.adamiak.hangout.utils.Helpers.timestampToDateString

@AndroidEntryPoint
class EventDetailFragment: Fragment(R.layout.fragment_event_details) {

	private lateinit var binding: FragmentEventDetailsBinding
	private lateinit var event: MapObject
	private val viewModel: EventDetailViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentEventDetailsBinding.bind(view)

		if(arguments != null){
			event = requireArguments().get("event") as MapObject
		} else {
			findNavController().popBackStack()
		}

		binding.tvChosenEventTitle.text = event.pinTitle
		binding.tvChosenEventDate.text = event.eventDay
		binding.tvEventDate.text = when(event.pinType) {
			PIN_TYPE.EVENT -> "${event.eventTime} ${event.eventDay}"
			else -> event.eventDay
		}

		binding.tvEventCommentary.text = event.comment
		binding.tvEventOrganizer.text = event.ownerEmail
		binding.tvEventTime.text = timestampToDateString(event.pinCreated)

		viewModel.storedCurrentUser.observe(viewLifecycleOwner) {
			if(it != null){
				binding.btnChosenEventEdit.isVisible = event.ownerEmail == it.email
			}
		}

		binding.btnChosenEventChat.setOnClickListener {
			val bundle = bundleOf("chatRoom" to "${event.uuidString}")
			findNavController().navigate(R.id.action_eventDetailFragment_to_chatFragment, bundle)
		}
		binding.btnChosenEventMap.setOnClickListener {
			val bundle = bundleOf("event" to event)
			findNavController().navigate(R.id.action_eventDetailFragment_to_chosenEventMapFragment, bundle)

		}
		binding.btnChosenEventEdit.setOnClickListener {
			val bundle = bundleOf("event" to event)
			findNavController().navigate(R.id.action_eventDetailFragment_to_eventEditFragment, bundle)
		}
	}

}