package jw.adamiak.hangout.ui.profile.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import jw.adamiak.hangout.utils.DataStoreManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
	private val dataStore: DataStoreManager
) : ViewModel() {
	private val auth: FirebaseAuth = Firebase.auth

	private var _currentUserEmail = MutableLiveData<String>()
	val currentUserEmail: LiveData<String>
		get() = _currentUserEmail

	private var _currentUsername = MutableLiveData<String>()
	val currentUsername: LiveData<String>
		get() = _currentUsername

	private fun getUserData() {
		viewModelScope.launch {
			dataStore.getUserDataFlow.collectLatest {
				_currentUserEmail.value = it.email ?: ""
				_currentUsername.value = it.username ?: ""
				if (it.username.isNullOrEmpty()){
					println("it.username: ${it.username}")
					println("_currentUserEmail.value: ${_currentUserEmail.value}")
					println("_currentUsername.value: ${_currentUsername.value}")
					_currentUserEmail.value = getUserEmailFromFirebase()
					_currentUsername.value = getUserEmailFromFirebase()
				}
			}
		}
	}

	private fun getUserEmailFromFirebase(): String {
		return if (auth.currentUser != null) {
			try {
				auth.currentUser!!.email ?: ""
			} catch (e: Exception) {
				"Error fetching user data"
			}
		} else {
			"Error fetching user data"
		}
	}

	init {
		getUserData()
	}

}