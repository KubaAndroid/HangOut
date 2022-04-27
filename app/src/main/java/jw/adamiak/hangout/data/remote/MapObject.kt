package jw.adamiak.hangout.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

enum class PIN_TYPE {
	EVENT,
	FRIEND_EVENT,
	POLICE,
	USER,
	OTHER_WARNING,
	OTHER_INFO
}

@Parcelize
data class MapObject(
	var pinTitle: String = "info",
	var pinType: PIN_TYPE = PIN_TYPE.OTHER_INFO,
	val ownerEmail: String? = "",
	var lat: Double = 0.0,
	var lng: Double = 0.0,
	var comment: String = "",
	var eventDay: String = "",
	var eventTime: String = "",
	var pinCreated: Long = System.currentTimeMillis(),
	var uuidString: String = ""
): Parcelable

