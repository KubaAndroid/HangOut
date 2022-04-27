package jw.adamiak.hangout.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Panel.ACTION_INTERNET_CONNECTIVITY
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object Helpers {

	fun getCurrentDateTimeString(): String {
		val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
		val resultDate = Date(System.currentTimeMillis())
		return sdf.format(resultDate)
	}

	fun getCurrentDayString(): String {
		val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
		val resultDate = Date(System.currentTimeMillis())
		return sdf.format(resultDate)
	}
	fun getCurrentTimeString(): String {
		val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
		val resultDate = Date(System.currentTimeMillis())
		return sdf.format(resultDate)
	}

	fun timestampToDateString(timeInMillis: Long): String {
		val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
		val resultDate = Date(timeInMillis)
		return sdf.format(resultDate)
	}

	fun setMarkerType(pinType: PIN_TYPE): Int {
		return when(pinType) {
			PIN_TYPE.OTHER_INFO -> R.mipmap.ic_map_info
			PIN_TYPE.EVENT ->  R.mipmap.ic_map_event
			PIN_TYPE.FRIEND_EVENT ->  R.mipmap.ic_event_friends
			PIN_TYPE.POLICE ->  R.mipmap.ic_map_police
			PIN_TYPE.USER ->  R.mipmap.ic_map_friends
			PIN_TYPE.OTHER_WARNING ->  R.mipmap.ic_map_warning
		}
	}

	private fun requestPermission(activity: AppCompatActivity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				Manifest.permission.ACCESS_FINE_LOCATION
			)) {
		} else {
			ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				Constants.LOCATION_PERMISSION_REQUEST_CODE
			)
		}
	}

	@SuppressLint("ObsoleteSdkInt")
	fun checkInternetConnection(context: Context): Boolean {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val network = connectivityManager.activeNetwork ?: return false
			val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
			return when {
				activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
				activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
				else -> false
			}
		} else {
			@Suppress("DEPRECATION") val networkInfo =
				connectivityManager.activeNetworkInfo ?: return false
			@Suppress("DEPRECATION")
			return networkInfo.isConnected
		}
	}

	fun checkInternetConnectionAlert(context: Context) {
		val alertBuilder = AlertDialog.Builder(context, R.style.MyAlertErrorDialogStyle)
		alertBuilder.setCancelable(false)
		alertBuilder.setMessage(context.getString(R.string.str_wifi_alert_message))

		alertBuilder.setPositiveButton(context.getString(R.string.ok)) { dialogInterface, _ ->
			val wifiIntent = Intent(Settings.ACTION_SETTINGS)
			wifiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			context.startActivity(wifiIntent)
			dialogInterface.dismiss()
		}
		val alert = alertBuilder.create()
		alert.setTitle(R.string.str_wifi_alert_title)
		alert.show()
	}

	fun hideKeyboard(currentActivity: Activity){
		val imm = currentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		var view = currentActivity.currentFocus
		if(view == null){
			view = View(currentActivity)
		}
		imm.hideSoftInputFromWindow(view.windowToken, 0)
	}

}