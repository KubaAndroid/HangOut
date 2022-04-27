package jw.adamiak.hangout.ui.events.events_map

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.utils.Constants

class ChosenEventMapFragment: Fragment(R.layout.fragment_map_events), GoogleMap.OnInfoWindowLongClickListener {
	private lateinit var googleMap: GoogleMap
	private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
	private lateinit var selectedEvent: MapObject

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if(arguments != null){
			selectedEvent = requireArguments().get("event") as MapObject
		} else {
			findNavController().popBackStack()
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
				mMap.animateCamera(
					CameraUpdateFactory
						.newCameraPosition(
							CameraPosition.builder()
								.target(LatLng(selectedEvent.lat, selectedEvent.lng))
								.zoom(14f)
								.bearing(0f)
								.tilt(45f)
								.build()))

				mMap.addMarker(
					MarkerOptions()
						.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_event))
						.position(LatLng(selectedEvent.lat, selectedEvent.lng))
						.title(selectedEvent.pinTitle)
						.snippet(selectedEvent.comment)
				)
			}
		} else {
			requestPermission(activity as AppCompatActivity)
		}
	}

	private fun showToast(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}

	private fun requestPermission(activity: AppCompatActivity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
		} else {
			ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				Constants.LOCATION_PERMISSION_REQUEST_CODE)
		}
	}

	override fun onInfoWindowLongClick(marker: Marker) {
		val bundle = bundleOf("event" to selectedEvent)
		findNavController().navigate(R.id.action_chosenEventMapFragment_to_eventDetailFragment, bundle)
	}

	override fun onResume() {
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
		super.onResume()
	}

}