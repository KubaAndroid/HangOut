package jw.adamiak.hangout.ui.start

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import jw.adamiak.hangout.ui.main_screen.MainMenuActivity
import jw.adamiak.hangout.utils.DataStoreManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val dataStoreManager: DataStoreManager)
	: ViewModel(){

	private val auth = Firebase.auth

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	private var _isLoading = MutableLiveData<Boolean>(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _changeScreen = MutableLiveData<Boolean>(false)
	val changeScreen: LiveData<Boolean>
		get() = _changeScreen

	val storedCurrentUser = dataStoreManager.getUserDataFlow.asLiveData()
	val storedUsername = storedCurrentUser.value?.username ?: ""
	val storedEmail = storedCurrentUser.value
	val storedPassword = storedCurrentUser.value?.password

	private fun saveUsername(username: String) {
		viewModelScope.launch {
			dataStoreManager.saveUsername(username)
		}
	}

	private fun saveEmail(email: String) {
		viewModelScope.launch {
			dataStoreManager.saveEmail(email)
		}
	}

	private fun savePassword(password: String) {
		viewModelScope.launch {
			dataStoreManager.savePassword(password)
		}
	}

	fun loginUser(email: String, password: String) {
		try {
			auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					if(task.isSuccessful) {
						saveEmail(email)
						savePassword(password)
						_isLoading.postValue(false)
						_message.postValue("Success")
						_changeScreen.postValue(true)
					} else {
						_isLoading.postValue(false)
						_message.postValue("Error: ${task.exception?.message}")
					}
				}
		} catch (e: Exception) {
			_isLoading.postValue(false)
			_message.postValue("ERROR: ${e.message.toString()}")
		}
	}

	fun registerUser(email: String, password: String, userName: String){
		_isLoading.postValue(true)
		auth.createUserWithEmailAndPassword(email, password)
			.addOnCompleteListener(){ task ->
				if(task.isSuccessful) {
					updateUserProfileWithUsername(email, password, userName)
//					_message.postValue("Success")
//					_isLoading.postValue(false)
				} else {
					_message.postValue("ERROR: ${task.exception?.message}")
					_isLoading.postValue(false)
				}
			}
	}

	private fun updateUserProfileWithUsername(email: String, password: String, userName: String) {
		val currentUser = auth.currentUser
		if(currentUser != null) {
			val profileUpdates = userProfileChangeRequest {
				displayName = userName
			}
			currentUser.updateProfile(profileUpdates).addOnSuccessListener {
				savePassword(password)
				saveEmail(email)
				saveUsername(userName)
				_changeScreen.postValue(true)
			}

		}
	}

}