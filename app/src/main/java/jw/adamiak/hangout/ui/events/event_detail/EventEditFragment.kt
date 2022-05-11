package jw.adamiak.hangout.ui.events.event_detail

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentEventEditBinding
import jw.adamiak.hangout.ui.start.WelcomeViewModel
import java.util.*

@AndroidEntryPoint
class EventEditFragment: Fragment(R.layout.fragment_event_edit), DatePickerDialog.OnDateSetListener,
	TimePickerDialog.OnTimeSetListener {

	private val viewModel: EventDetailViewModel by viewModels()

	private lateinit var binding: FragmentEventEditBinding
	private lateinit var eventDateString: String
	private lateinit var eventTimeString: String
	private lateinit var event: MapObject
	private lateinit var db: FirebaseFirestore

	private lateinit var snack: Snackbar
	private var isSnackbarVisible: Boolean = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentEventEditBinding.bind(view)

		event = if(arguments != null) {
			requireArguments().get("event") as MapObject
		} else {
			findNavController().popBackStack()
			MapObject()
		}

		db = Firebase.firestore

		viewModel.isLoading.observe(viewLifecycleOwner) {
			binding.pbEditEvent.isVisible = it
		}
		viewModel.message.observe(viewLifecycleOwner) {
			showSnackbar(it)
		}

		viewModel.setEvent(event)

		viewModel.currentEvent.observe(viewLifecycleOwner) {
			binding.etEditEventName.setText(it.pinTitle)
			binding.etEditEventComment.setText(it.comment)
			binding.etEditEventDate.setText(it.eventDay)
			binding.etEditEventTime.setText(it.eventTime)
		}
		binding.etEditEventDate.setOnClickListener {
			showDatePicker()
		}
		binding.etEditEventTime.setOnClickListener {
			showTimePicker()
		}
		binding.btnEditEvent.setOnClickListener {
			event.pinTitle = binding.etEditEventName.text.toString()
			event.comment = binding.etEditEventComment.text.toString()
			event.eventDay = binding.etEditEventDate.text.toString()
			event.eventTime = binding.etEditEventTime.text.toString()

			viewModel.saveEditedEventToFirebase(event)
		}
		binding.btnDeleteEvent.setOnClickListener {
			viewModel.deleteEvent(event)
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

	override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
		binding.etEditEventDate.text = getString(R.string.date_set_string, day, month+1, year)
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
		binding.etEditEventTime.text = getString(R.string.time_set_string, hoursString, minutesString)
		eventTimeString = "$hoursString:$minutesString"
	}

	private fun showSnackbar(message: String) {
		isSnackbarVisible = true
		snack = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
		snack.setAction("OK") {
			try {
				snack.dismiss()
				isSnackbarVisible = false
				findNavController().navigate(R.id.action_eventEditFragment_to_mapEventsFragment)
			} catch (e: Exception) {
				snack.dismiss()
				println("error: ${e.message}")
			}
		}
		snack.show()
	}

	override fun onDestroy() {
		if(isSnackbarVisible) {
			snack.dismiss()
		}
		super.onDestroy()
	}

}