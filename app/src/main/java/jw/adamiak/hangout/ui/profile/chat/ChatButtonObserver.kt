package jw.adamiak.hangout.ui.profile.chat

import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import jw.adamiak.hangout.R

class ChatButtonObserver(private val sendButton: ImageView): TextWatcher {
	override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

	override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
		if(charSequence.toString().trim().isNotEmpty()){
			sendButton.isEnabled = true
			sendButton.setImageResource(R.drawable.ic_baseline_send_24)
		} else {
			sendButton.isEnabled = false
			sendButton.setImageResource(R.drawable.ic_baseline_send_24_off)
		}
	}

	override fun afterTextChanged(p0: Editable?) {}
}