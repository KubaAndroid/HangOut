package jw.adamiak.hangout.ui.profile.edit_profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.R
import jw.adamiak.hangout.databinding.FragmentProfileEditBinding
import jw.adamiak.hangout.ui.start.StartActivity

class EditProfileFragment: Fragment(R.layout.fragment_profile_edit) {
	private lateinit var auth: FirebaseAuth
	private lateinit var currentUser: FirebaseUser
	private lateinit var binding: FragmentProfileEditBinding

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentProfileEditBinding.bind(view)
		auth = Firebase.auth

		binding.pbEditUser.visibility = ProgressBar.INVISIBLE

		if(auth.currentUser != null){
			currentUser = auth.currentUser!!
			binding.etEditName.setText(currentUser.displayName)
			binding.etEditEmail.setText(currentUser.email)
		} else {
			findNavController().popBackStack()
		}

		binding.btnEditProfile.setOnClickListener {
			binding.btnEditProfile.isEnabled = false
			binding.pbEditUser.visibility = ProgressBar.VISIBLE

			val username = binding.etEditName.text.trim().toString()
			val email = binding.etEditEmail.text.trim().toString()
			val password = binding.etEditPassword.text.trim().toString()
			val passwordConfirm = binding.etEditPasswordConfirm.text.trim().toString()

			var isSuccess = true
			if(binding.etEditName.didTouchFocusSelect()){
				val profileUpdate = userProfileChangeRequest {
					displayName = username
				}
				currentUser.updateProfile(profileUpdate)
					.addOnFailureListener {
						isSuccess = false
					}
			}

			if(binding.etEditEmail.didTouchFocusSelect()){
				currentUser.updateEmail(email)
					.addOnFailureListener {
						isSuccess = false
					}
			}
			if(binding.etEditPassword.didTouchFocusSelect()){
				if(password.equals(passwordConfirm)){
					currentUser.updatePassword(password)
						.addOnFailureListener {
							isSuccess = false
						}
				}
			}

			if(isSuccess) {
				showSnackBarSuccess("Profile updated")
			} else {
				showSnackBar("Failed to update profile")
			}

			binding.btnEditProfile.isEnabled = true
			binding.pbEditUser.visibility = ProgressBar.INVISIBLE
		}

		binding.btnDeleteProfile.setOnClickListener {
			currentUser.delete()
				.addOnSuccessListener {
					showSnackBar("User deleted")
					val intent = Intent(requireContext(), StartActivity::class.java)
					startActivity(intent)
				}
				.addOnFailureListener {
					showSnackBar("Failed to delete the account")
				}
		}

	}

	private fun showSnackBarSuccess(message: String){
		val snack = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
		snack.setAction("OK") {
			try {
				findNavController().navigate(R.id.action_editProfileFragment_to_profileMenuFragment)
			} catch (e: Exception) {
				println("error: ${e.message}")
			}

		}
		snack.show()
	}

	private fun showSnackBar(message: String) {
		Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
	}

}