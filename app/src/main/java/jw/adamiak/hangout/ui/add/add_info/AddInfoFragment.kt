package jw.adamiak.hangout.ui.add.add_info

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
import jw.adamiak.hangout.databinding.FragmentInfoAddBinding
import jw.adamiak.hangout.ui.add.AddViewModel
import jw.adamiak.hangout.utils.Helpers.getCurrentDateTimeString
import jw.adamiak.hangout.utils.Helpers.getCurrentDayString
import jw.adamiak.hangout.utils.Helpers.getCurrentTimeString
import jw.adamiak.hangout.utils.Helpers.hideKeyboard
import java.util.*

@AndroidEntryPoint
class AddInfoFragment: Fragment(R.layout.fragment_info_add) {
	private lateinit var binding: FragmentInfoAddBinding
	private val viewModel: AddViewModel by viewModels()
	private lateinit var userEmail: String
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentInfoAddBinding.bind(view)

		viewModel.currentUserEmail.observe(viewLifecycleOwner){
			userEmail = it
		}

		binding.btnAddOtherFrag.setOnClickListener {
			hideKeyboard(requireActivity())
			val comment = binding.etAddOtherComment.text.trim().toString()
			var pinTitle: String
			val eventType = when(binding.rgEvent.checkedRadioButtonId) {
				R.id.rb_event_info -> {
					pinTitle = "INFO"
					PIN_TYPE.OTHER_INFO
				}
				R.id.rb_event_warning -> {
					pinTitle = "WARNING"
					PIN_TYPE.OTHER_WARNING
				}
				else -> {
					pinTitle = "INFO"
					PIN_TYPE.OTHER_INFO
				}
			}
			if(userEmail.isNullOrEmpty()){
				userEmail = viewModel.getUserEmailFromFirebase()
			}
			val newEvent = MapObject(
				pinTitle = pinTitle,
				pinType = eventType,
				ownerEmail =  userEmail,
				comment = comment,
				eventDay = getCurrentDayString(),
				eventTime = getCurrentTimeString(),
				uuidString = UUID.randomUUID().toString()
			)
			val bundle = bundleOf("event" to newEvent)
			findNavController().navigate(R.id.action_addInfoFragment_to_mapAddFragment, bundle)
		}

	}
}