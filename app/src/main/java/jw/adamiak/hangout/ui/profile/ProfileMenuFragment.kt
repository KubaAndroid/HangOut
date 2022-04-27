package jw.adamiak.hangout.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentMenuProfileBinding
import jw.adamiak.hangout.ui.start.StartActivity


class ProfileMenuFragment: Fragment(R.layout.fragment_menu_profile) {
	private lateinit var binding: FragmentMenuProfileBinding
	private lateinit var auth: FirebaseAuth
	private lateinit var username: String
	private lateinit var userEmail: String
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentMenuProfileBinding.bind(view)

		auth = Firebase.auth
		if(auth.currentUser != null){
			username = auth.currentUser?.displayName ?: ""
			userEmail = auth.currentUser?.email ?: ""
			binding.tvProfileUsername.text = username
			binding.tvProfileEmail.text = userEmail
		} else {
			val intent = Intent(requireContext(), StartActivity::class.java)
			startActivity(intent)
		}

		binding.llMyEvents.setOnClickListener {
			findNavController().navigate(R.id.action_profileMenuFragment_to_myEventsFragment)
		}

		binding.llAddMyLocation.setOnClickListener {
			val newPin = MapObject(
				pinTitle = username,
				pinType = PIN_TYPE.USER,
				ownerEmail = userEmail)
			val bundle = bundleOf("event" to newPin)
			findNavController().navigate(R.id.action_profileMenuFragment_to_mapAddFragment, bundle)
		}
		binding.llDefaultChat.setOnClickListener {
			findNavController().navigate(R.id.action_profileMenuFragment_to_chatFragment)
		}
		binding.llEditUser.setOnClickListener {
			findNavController().navigate(R.id.action_profileMenuFragment_to_editProfileFragment)
		}

	}
}