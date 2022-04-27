package jw.adamiak.hangout.ui.start.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.databinding.FragmentLoginBinding
import jw.adamiak.hangout.ui.main_screen.MainMenuActivity
import jw.adamiak.hangout.ui.start.WelcomeViewModel
import kotlinx.coroutines.launch



@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
	private lateinit var binding: FragmentLoginBinding
	private lateinit var auth: FirebaseAuth

	private val viewModel: WelcomeViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentLoginBinding.bind(view)
		auth = Firebase.auth

		viewModel.isLoading.observe(viewLifecycleOwner) {
			if(it){
				binding.pbLogin.visibility =  ProgressBar.VISIBLE
			} else {
				binding.pbLogin.visibility =  ProgressBar.INVISIBLE
			}
		}

		viewModel.message.observe(viewLifecycleOwner) {
			showToast(it)
		}
		viewModel.changeScreen.observe(viewLifecycleOwner) {
			if(it) {
				moveToMain()
			}
		}

		viewModel.storedCurrentUser.observe(viewLifecycleOwner) {
			binding.etEmailLogin.setText(it?.email)
			binding.etLoginPassword.setText(it?.password)
		}

		binding.btnLoginConfirm.setOnClickListener {
			val email = binding.etEmailLogin.text.trim().toString()
			val password = binding.etLoginPassword.text.trim().toString()
			if (email.isNotEmpty() && password.isNotEmpty()){
				viewModel.loginUser(email, password)
			}
		}

		binding.btnLoginBack.setOnClickListener {
			findNavController().popBackStack()
		}
	}


	private fun moveToMain() {
		val intent = Intent(requireContext(), MainMenuActivity::class.java)
		startActivity(intent)
	}

	private fun showToast(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}

	override fun onStart() {
		super.onStart()
		val currentUser = auth.currentUser
		if(currentUser != null){
			val intent = Intent(requireContext(), MainMenuActivity::class.java)
			startActivity(intent)
		}
	}
}