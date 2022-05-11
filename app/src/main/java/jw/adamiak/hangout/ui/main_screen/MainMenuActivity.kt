package jw.adamiak.hangout.ui.main_screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.ui.start.StartActivity
import jw.adamiak.hangout.utils.CHANNEL_ID
import jw.adamiak.hangout.utils.CHANNEL_NAME
import jw.adamiak.hangout.utils.Helpers.checkInternetConnection
import jw.adamiak.hangout.utils.Helpers.checkInternetConnectionAlert

@AndroidEntryPoint
class MainMenuActivity : AppCompatActivity() {
	private lateinit var auth: FirebaseAuth
	private lateinit var drawerLayout: DrawerLayout
	private lateinit var appBarConfiguration: AppBarConfiguration
	private var username: String = ""
	private var email: String = ""


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_menu)

		checkRequiredConnections()


		auth = Firebase.auth
		if (auth.currentUser == null) {
			startActivity(Intent(this, StartActivity::class.java))
			finish()
			return
		} else {
			username = auth.currentUser?.displayName ?: "username"
			email = auth.currentUser?.email ?: "email"
		}

		setupDrawer()
		setSupportActionBar(findViewById(R.id.toolbar))

		val navController: NavController = findNavController(R.id.nav_host_fragment_main)
		appBarConfiguration = AppBarConfiguration.Builder(
			R.id.menuAddFragment,
			R.id.mapEventsFragment,
			R.id.profileMenuFragment,
			R.id.logoutFragment,
			R.id.exitFragment)
			.setOpenableLayout(drawerLayout)
			.build()
		setupActionBarWithNavController(navController, appBarConfiguration)
		findViewById<NavigationView>(R.id.nav_view)
			.setupWithNavController(navController)
	}

	private fun checkRequiredConnections() {
		if(!checkInternetConnection(this)){
			checkInternetConnectionAlert(this)
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		return findNavController(R.id.nav_host_fragment_main).navigateUp(appBarConfiguration)
			|| super.onSupportNavigateUp()
	}

	private fun setupDrawer(){
		drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
			.apply {
				setStatusBarBackground(R.color.colorPrimaryDark2)
			}
		val navView = findViewById<NavigationView>(R.id.nav_view)
		val header = navView.getHeaderView(0)
		val usernameTv = header.findViewById<TextView>(R.id.tv_drawer_username)
		usernameTv.text = username
		val usernameEmail = header.findViewById<TextView>(R.id.tv_drawer_email)
		usernameEmail.text = email
	}


	override fun onBackPressed() {
		if(drawerLayout.isDrawerOpen(GravityCompat.START)){
			drawerLayout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onStart() {
		super.onStart()
		if (auth.currentUser == null) {
			startActivity(Intent(this, StartActivity::class.java))
			finish()
			return
		}
	}



}