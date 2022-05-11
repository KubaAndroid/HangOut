package jw.adamiak.hangout.ui.add.event_add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentEventAddBinding
import jw.adamiak.hangout.ui.add.AddViewModel
import jw.adamiak.hangout.utils.Helpers.hideKeyboard
import java.util.*

@AndroidEntryPoint
class EventAddFragment: Fragment(R.layout.fragment_event_add),
	DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private val viewModel: AddViewModel by viewModels()

	private lateinit var binding: FragmentEventAddBinding
	private lateinit var userEmail: String
	private var eventDateString: String = ""
	private var eventTimeString: String = ""

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentEventAddBinding.bind(view)

		viewModel.currentUserEmail.observe(viewLifecycleOwner){
			userEmail = it
		}

		binding.tvEventDatePicker.setOnClickListener {
			showDatePicker()
		}
		binding.tvEventTimePicker.setOnClickListener {
			showTimePicker()
		}
		binding.btnAddEventFrag.setOnClickListener {
			if(userEmail.isNullOrEmpty()){
				userEmail = viewModel.getUserEmailFromFirebase()
			}
			hideKeyboard(requireActivity())
			val eventName = binding.etEventName.text.trim().toString()
			val eventCommentary = binding.etAddEventComment.text.trim().toString()
			if (eventName.isNotEmpty() && eventDateString.isNotEmpty() && eventTimeString.isNotEmpty()) {
				val mapObject = MapObject(
					pinTitle = eventName,
					pinType = PIN_TYPE.EVENT,
					ownerEmail = userEmail,
					comment = eventCommentary,
					eventDay = eventDateString,
					eventTime = eventTimeString)

				val bundle = bundleOf("event" to mapObject)
				findNavController().navigate(R.id.action_eventAddFragment_to_mapAddFragment, bundle)
			}
		}
	}

	private fun showDatePicker() {
		val calendar = Calendar.getInstance()
		val year = calendar[Calendar.YEAR]
		val month = calendar[Calendar.MONTH]
		val day = calendar[Calendar.DAY_OF_MONTH]

		val picker = DatePickerDialog(requireContext(), R.style.DatePickerCustom, this, year, month, day)
		picker.datePicker.minDate = System.currentTimeMillis()
		picker.show()
	}

	private fun showTimePicker() {
		TimePickerDialog(requireContext(), R.style.DatePickerCustom, this, 0,
			0, true)
			.show()
	}

	override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
		binding.tvEventDatePicker.text = getString(R.string.date_set_string, day, month+1, year)
		eventDateString = "$day.${month + 1}.${year}"
	}

	override fun onTimeSet(timePicker: TimePicker?, hour: Int, minutes: Int) {
		val minutesString = when (minutes) {
			0 -> "00"
			in 1..9 -> "0$minutes"
			else -> "$minutes"
		}
		val hoursString = when(hour) {
			0 -> "00"
			in 1..9 -> "0$hour"
			else -> "$hour"
		}

		binding.tvEventTimePicker.text = getString(R.string.time_set_string, hoursString, minutesString)
		eventTimeString = "$hoursString:$minutesString"
	}
}