package jw.adamiak.hangout.ui.main_screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.ui.start.StartActivity

class LogoutFragment: Fragment() {
	private lateinit var auth: FirebaseAuth

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		auth = Firebase.auth
		try {
			auth.signOut().also {
				val intent = Intent(requireContext(), StartActivity::class.java)
				startActivity(intent)
			}
		} catch (e: Exception) {
			Toast.makeText(requireContext(), "error while logging out:\n${e.message}",
				Toast.LENGTH_LONG).show()
		}
	}


}