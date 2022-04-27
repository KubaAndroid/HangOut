package jw.adamiak.hangout.ui.add

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentMenuAddBinding
import jw.adamiak.hangout.ui.events.event_detail.EventDetailViewModel

@AndroidEntryPoint
class MenuAddFragment: Fragment(R.layout.fragment_menu_add) {
	private lateinit var binding: FragmentMenuAddBinding
	private lateinit var userEmail: String
	private lateinit var username: String
	private val viewModel: AddViewModel by viewModels()
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentMenuAddBinding.bind(view)

		viewModel.currentUserEmail.observe(viewLifecycleOwner) {
			userEmail = it
		}
		viewModel.currentUsername.observe(viewLifecycleOwner) {
			username = it ?: ""
		}


		binding.llAddEvent.setOnClickListener {
			findNavController().navigate(R.id.action_menuAddFragment_to_eventAddFragment)
		}
		binding.llAddOtherEvent.setOnClickListener {
			findNavController().navigate(R.id.action_menuAddFragment_to_addInfoFragment)
		}
		binding.llAddPolice.setOnClickListener {
			val newPin = MapObject("Police", PIN_TYPE.POLICE, viewModel.storedEmail)
			val bundle = bundleOf("event" to newPin)
			findNavController().navigate(R.id.action_menuAddFragment_to_mapAddFragment, bundle)
		}
		binding.llAddMyLocation.setOnClickListener {
			val newPin = MapObject(
				pinTitle = username,
				pinType = PIN_TYPE.USER,
				ownerEmail = userEmail)
			val bundle = bundleOf("event" to newPin)
			findNavController().navigate(R.id.action_menuAddFragment_to_mapAddFragment, bundle)
		}
	}
}