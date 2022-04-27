package jw.adamiak.hangout.ui.add

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.utils.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
	private val dataStoreManager: DataStoreManager
) : ViewModel() {

	private val db: FirebaseFirestore = Firebase.firestore
	private val auth: FirebaseAuth = Firebase.auth

	private var _currentUsername = MutableLiveData<String>()
	val currentUsername: LiveData<String>
		get() = _currentUsername

	private var _currentUserEmail = MutableLiveData<String>()
	val currentUserEmail: LiveData<String>
		get() = _currentUserEmail

	private val storedCurrentUser = dataStoreManager.getUserDataFlow.asLiveData()
	val storedUsername = storedCurrentUser.value?.username ?: ""
	val storedEmail = storedCurrentUser.value?.email ?: ""

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	private var _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _event = MutableLiveData<MapObject>()
	val event: LiveData<MapObject>
		get() = _event

	fun saveGeoPoint(point: MapObject) {
		_isLoading.postValue(true)
		db.collection("geopoints")
			.add(point)
			.addOnSuccessListener {
				_message.postValue("GeoPoint saved")
				_isLoading.postValue(false)
			}
			.addOnFailureListener { e ->
				_message.postValue("ERROR: ${e.message.toString()}")
				_isLoading.postValue(false)
			}
	}

	private fun getUserDataFromDataStore() {
		viewModelScope.launch {
			if(storedEmail.isNullOrEmpty() || storedUsername.isNullOrEmpty()){
				dataStoreManager.getUserDataFlow.collectLatest {
					_currentUserEmail.value = it.email ?: ""
					_currentUsername.value = it.username ?: ""
					if (it.email.isNullOrEmpty() || it.username.isNullOrEmpty()){
						getUserDataFromFirebase()
					}
				}
			}
		}
	}

	fun getUserEmailFromFirebase(): String {
		return if (auth.currentUser != null) {
			try {
				auth.currentUser!!.email ?: "No current user"
			} catch (e: Exception) {
				"Error fetching user data"
			}
		} else {
			"Error fetching user data"
		}
	}

	private fun getUserDataFromFirebase(){
		if (auth.currentUser == null) {
			_currentUsername.value = ""
			_currentUserEmail.value = ""
		} else {
			_currentUsername.value = auth.currentUser?.displayName ?: ""
			_currentUserEmail.value = auth.currentUser?.email ?: ""
		}
	}



	init {
		storedCurrentUser
		getUserDataFromDataStore()
	}

}