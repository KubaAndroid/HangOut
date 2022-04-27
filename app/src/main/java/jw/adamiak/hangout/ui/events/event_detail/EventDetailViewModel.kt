package jw.adamiak.hangout.ui.events.event_detail

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.utils.DataStoreManager
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(private val dataStoreManager: DataStoreManager):
	ViewModel(){

	private val db: FirebaseFirestore = Firebase.firestore
	private val auth: FirebaseAuth = Firebase.auth

	private fun checkCurrentUserData(){
		if (auth.currentUser == null) {
			_currentUsername.value = "user"
			_currentUserEmail.value = "user@user"
		} else {
			_currentUsername.value = auth.currentUser?.displayName ?: "user"
			_currentUserEmail.value = auth.currentUser?.email ?: "user@user"
		}
	}

	val storedCurrentUser = dataStoreManager.getUserDataFlow.asLiveData()
	val storedUsername = storedCurrentUser.value?.username ?: ""
	val storedEmail = storedCurrentUser.value?.email ?: "no@email"

	private var _currentUserEmail = MutableLiveData<String>()
	val currentUserEmail: LiveData<String>
		get() = _currentUserEmail

	private var _currentUsername = MutableLiveData<String>()
	val currentUsername: LiveData<String>
		get() = _currentUsername

	private var _currentEventTitle = MutableLiveData<String>()
	val currentEventTitle: LiveData<String>
		get() = _currentEventTitle

	private var _currentEventComment = MutableLiveData<String>()
	val currentEventComment: LiveData<String>
		get() = _currentEventComment

	private var _currentEventDay = MutableLiveData<String>()
	val currentEventDay: LiveData<String>
		get() = _currentEventDay

	private var _currentEventTime = MutableLiveData<String>()
	val currentEventTime: LiveData<String>
		get() = _currentEventTime

	private var _currentEvent = MutableLiveData<MapObject>()
	val currentEvent: LiveData<MapObject>
		get() = _currentEvent

	private var _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	fun setEvent(event: MapObject) {
		_currentEvent.value = event
	}


	fun saveEditedEventToFirebase(event: MapObject) {
		_isLoading.value = true
		setEvent(event)
		val currentEventRef = db.collection("geopoints").document(event.uuidString)
		val updates = hashMapOf<String, Any>(
			"pinTitle" to event.pinTitle,
			"comment" to event.comment,
			"eventDay" to event.eventDay,
			"eventTime" to event.eventTime
		)

		currentEventRef.update(updates)
			.addOnSuccessListener {
				_message.postValue("Event updated")
				_isLoading.value = false
			}
			.addOnFailureListener {
				_message.postValue("Event failed to update")
				_isLoading.value = false
			}
	}

	fun deleteEvent(event: MapObject) {
		_isLoading.value = true
		val currentEventRef = db.collection("geopoints").document(event.uuidString)
		currentEventRef
			.delete()
			.addOnSuccessListener {
				_message.postValue("Event deleted")
				_isLoading.value = false
			}
			.addOnFailureListener {
				_message.postValue("Error occurred")
				_isLoading.value = false
			}
	}

	init {
		checkCurrentUserData()
	}
}