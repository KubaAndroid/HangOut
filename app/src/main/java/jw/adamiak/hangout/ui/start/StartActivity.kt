package jw.adamiak.hangout.ui.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.utils.Helpers.checkInternetConnection
import jw.adamiak.hangout.utils.Helpers.checkInternetConnectionAlert

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_start)

		val navigation = Navigation.findNavController(this, R.id.nav_host_fragment_start)

	}
}