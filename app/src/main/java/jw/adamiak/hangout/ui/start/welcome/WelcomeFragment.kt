package jw.adamiak.hangout.ui.start.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.R
import jw.adamiak.hangout.databinding.FragmentWelcomeBinding
import jw.adamiak.hangout.ui.main_screen.MainMenuActivity
import jw.adamiak.hangout.utils.Helpers

class WelcomeFragment: Fragment(R.layout.fragment_welcome) {

	private lateinit var binding: FragmentWelcomeBinding
	private lateinit var auth: FirebaseAuth

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentWelcomeBinding.bind(view)

		val isUserConnected = Helpers.checkInternetConnection(requireContext())
		if (!isUserConnected){
			Helpers.checkInternetConnectionAlert(requireContext())
		}

		checkAuth()

		binding.btnLogin.setOnClickListener {
			findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
		}
		binding.btnRegister.setOnClickListener {
			findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
		}
		binding.btnExit.setOnClickListener {
			requireActivity().finish()
		}
		binding.tvAbout.setOnClickListener {
			findNavController().navigate(R.id.action_welcomeFragment_to_aboutFragment)
		}

	}

	private fun checkAuth() {
		auth = Firebase.auth
		try {
			val currentUser = auth.currentUser
			if (currentUser != null){
				val intent = Intent(requireContext(), MainMenuActivity::class.java)
				startActivity(intent)
			}
		} catch (e: Exception) {
			Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
		}
	}

}