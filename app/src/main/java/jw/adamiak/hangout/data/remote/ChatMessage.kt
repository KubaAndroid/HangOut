package jw.adamiak.hangout.data.remote

import android.icu.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
	var text: String? = null,
	var username: String? = null,
	var time: Long = System.currentTimeMillis()
) {
	fun toTimeString(): String {
		val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
		val resultDate = Date(time)
		return sdf.format(resultDate)
	}
}
