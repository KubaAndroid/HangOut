package jw.adamiak.hangout.ui.main_screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

class ExitFragment: Fragment() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val homeIntent = Intent(Intent.ACTION_MAIN)
		homeIntent.addCategory(Intent.CATEGORY_HOME)
		homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		startActivity(homeIntent)
	}
}