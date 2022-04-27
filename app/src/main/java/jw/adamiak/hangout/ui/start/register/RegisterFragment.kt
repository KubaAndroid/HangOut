package jw.adamiak.hangout.ui.start.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.databinding.FragmentRegisterBinding
import jw.adamiak.hangout.ui.main_screen.MainMenuActivity
import jw.adamiak.hangout.ui.start.WelcomeViewModel

@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {
	private lateinit var binding: FragmentRegisterBinding
	private val viewModel: WelcomeViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentRegisterBinding.bind(view)

		viewModel.changeScreen.observe(viewLifecycleOwner) {
			if(it) {
				moveToMain()
			}
		}
		viewModel.isLoading.observe(viewLifecycleOwner) {
			if(it){
				binding.pbRegister.visibility =  ProgressBar.VISIBLE
			} else {
				binding.pbRegister.visibility =  ProgressBar.INVISIBLE
			}
		}

		viewModel.message.observe(viewLifecycleOwner) {
			showToast(it)
		}

		binding.btnRegisterConfirm.setOnClickListener {
			val userName = binding.etUsernameRegister.text.trim()
			val email = binding.etEmailRegister.text.trim()
			val password = binding.etPasswordRegister.text.trim()
			val passwordConfirmed = binding.etPasswordRegister2.text.trim()

			if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
				&& passwordConfirmed.isNotEmpty() && password == passwordConfirmed) {
				viewModel.registerUser(email.toString(), password.toString(), userName.toString())
			} else {
				showToast("Please fill all fields")
			}
		}

		binding.btnRegisterBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}

	private fun showToast(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}

	private fun moveToMain() {
		val intent = Intent(requireContext(), MainMenuActivity::class.java)
		startActivity(intent)
	}
}