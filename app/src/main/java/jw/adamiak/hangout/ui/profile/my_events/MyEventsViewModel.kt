package jw.adamiak.hangout.ui.profile.my_events

import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyEventsViewModel @Inject constructor(
	private val dataStoreManager: DataStoreManager
) : ViewModel(){

	private val auth: FirebaseAuth = Firebase.auth
	private val db: FirebaseFirestore = Firebase.firestore

	private var _userEmail = MutableLiveData<String>()
	val userEmail: LiveData<String>
		get() = _userEmail

	private var _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	lateinit var currentUserEmail: String

	private var _events = MutableLiveData<MutableList<MapObject>>()
	val events: LiveData<MutableList<MapObject>>
		get() = _events

	private fun getMyEvents() {
		viewModelScope.launch {
			_isLoading.postValue(true)
			dataStoreManager.getUserEmail().collect {
				_userEmail.postValue(it)
				currentUserEmail = it ?: ""
				getGeoPoints(currentUserEmail)
			}
		}
	}

	private fun getGeoPoints(filter: String) {
		viewModelScope.launch {
			val geopoints = mutableListOf<MapObject>()
			db.collection("geopoints")
				.whereEqualTo("ownerEmail", filter)
				.get()
				.addOnSuccessListener { result ->
					for (geoPoint in result) {
						val point = geoPoint.toObject<MapObject>()
						point.uuidString = geoPoint.id
						geopoints.add(point)
					}
					_isLoading.postValue(false)
					_events.value = geopoints
				}
				.addOnFailureListener { e ->
					_message.value = e.message.toString()
					_isLoading.postValue(false)
				}
		}
	}

	init {
		getMyEvents()
	}

}