package jw.adamiak.hangout.ui.events.events_map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.databinding.FragmentMapEventsBinding
import jw.adamiak.hangout.utils.Constants
import jw.adamiak.hangout.utils.Helpers
import jw.adamiak.hangout.utils.Helpers.setMarkerType
import kotlinx.coroutines.launch


class MapEventsFragment: Fragment(R.layout.fragment_map_events), GoogleMap.OnInfoWindowLongClickListener {
	private lateinit var binding: FragmentMapEventsBinding
	private val viewModel: MapViewModel by viewModels()

	private lateinit var googleMap: GoogleMap
	private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
	private lateinit var db: FirebaseFirestore

	private var mapObjects = mutableListOf<MapObject>()

	override fun onResume() {
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
		super.onResume()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentMapEventsBinding.bind(view)
		db = Firebase.firestore

		viewModel.message.observe(viewLifecycleOwner) {
			showToast(it)
		}

		val map = childFragmentManager.findFragmentById(R.id.main_map_fragment) as SupportMapFragment?
		map?.getMapAsync(mapCallback)
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

	}

	private val mapCallback = OnMapReadyCallback {
		googleMap = it
		try {
			enableMyLocation(googleMap)
		} catch (e: Exception) {
			showToast("Error occurred:\n${e.message.toString()}")
		}

		lifecycleScope.launch {
			mapObjects = viewModel.getGeoPoints(googleMap)
		}

		googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
			override fun getInfoContents(marker: Marker): View? {
				val infoWindow = layoutInflater.inflate(R.layout.marker_info_window, null)
				val markerTitle = infoWindow.findViewById<TextView>(R.id.tv_pin_title)
				val markerComment = infoWindow.findViewById<TextView>(R.id.tv_pin_comment)
				markerTitle.text = marker.title
				markerComment.text = marker.snippet
				return infoWindow
			}
			override fun getInfoWindow(p0: Marker): View? {
				return null
			}
		})
		googleMap.setOnInfoWindowLongClickListener(this)
	}

	private fun enableMyLocation(mMap: GoogleMap) {
		if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED) {
			mMap.isMyLocationEnabled = true
			fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
				if (loc == null) {
					showToast("Cannot locate user")
				} else {
					mMap.animateCamera(
						CameraUpdateFactory
							.newCameraPosition(
								CameraPosition.builder()
									.target(LatLng(loc.latitude, loc.longitude))
									.zoom(16f)
									.bearing(0f)
									.tilt(45f)
									.build()))
				}
			}
		} else {
			requestPermission(activity as AppCompatActivity)
		}
	}

	private fun requestPermission(activity: AppCompatActivity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
					showToast("no permissions have been granted yet")
		} else {
			ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				Constants.LOCATION_PERMISSION_REQUEST_CODE)
		}
	}

	override fun onInfoWindowLongClick(marker: Marker) {
		val point = mapObjects.firstOrNull {
			it.lat ==  marker.position.latitude && it.lng == marker.position.longitude
		}
		if (point != null && point.pinType == PIN_TYPE.EVENT) {
			val bundle = bundleOf("event" to point)
			findNavController().navigate(R.id.action_mapEventsFragment_to_eventDetailFragment, bundle)
		} else if (point != null && (point.pinType == PIN_TYPE.OTHER_INFO
				|| point.pinType == PIN_TYPE.OTHER_WARNING || point.pinType == PIN_TYPE.POLICE
				|| point.pinType == PIN_TYPE.USER)) {
			showDeleteSnackbar(point, marker)
		}
	}

	private fun showDeleteSnackbar(pointToDelete: MapObject, marker: Marker) {
		val snack = Snackbar.make(requireView(), "", Snackbar.LENGTH_INDEFINITE)
		val customSnackView = layoutInflater.inflate(R.layout.custom_snackbar, null)
		snack.view.setBackgroundColor(Color.TRANSPARENT)
		val snackLayout = snack.view as Snackbar.SnackbarLayout
		snackLayout.setPadding(0, 0, 0, 0)
		val deletePoint = customSnackView.findViewById<TextView>(R.id.btn_snack_delete_yes)
		deletePoint.setOnClickListener {
			mapObjects.remove(pointToDelete)
			marker.remove()
			deleteEvent(pointToDelete)
			snack.dismiss()
		}
		val cancel = customSnackView.findViewById<TextView>(R.id.btn_snack_delete_cancel)
		cancel.setOnClickListener {
			snack.dismiss()
		}
		snackLayout.addView(customSnackView, 0)
		snack.show()
	}

	private fun deleteEvent(event: MapObject) {
		val currentEventRef = db.collection("geopoints").document(event.uuidString)
		currentEventRef.delete()
			.addOnSuccessListener {
				showToast("Pin deleted")
			}
			.addOnFailureListener {
				showToast("Error occurred")
			}
	}

	private fun showToast(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}

}


