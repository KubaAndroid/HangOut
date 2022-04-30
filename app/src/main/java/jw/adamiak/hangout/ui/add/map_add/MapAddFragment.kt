package jw.adamiak.hangout.ui.add.map_add

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import jw.adamiak.hangout.R
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.databinding.FragmentMapAddBinding
import jw.adamiak.hangout.ui.add.AddViewModel
import jw.adamiak.hangout.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapAddFragment: Fragment(R.layout.fragment_map_add),
	GoogleMap.OnMyLocationClickListener,
	GoogleMap.OnMyLocationButtonClickListener,
	GoogleMap.OnCameraIdleListener {

	private val viewModel: AddViewModel by viewModels()

	private lateinit var binding: FragmentMapAddBinding
	private lateinit var googleMap: GoogleMap
	private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
	private lateinit var midPoint: LatLng
	private lateinit var db: FirebaseFirestore

	private lateinit var event: MapObject

	private lateinit var snack: Snackbar
	private var isSnackbarVisible: Boolean = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FragmentMapAddBinding.bind(view)

		db = Firebase.firestore

		event = if(arguments != null){
			requireArguments().get("event") as MapObject
		} else {
			findNavController().popBackStack()
			MapObject()
		}

		binding.btnMapAdd.setOnClickListener {
//			binding.btnMapAdd.isActivated = false
			binding.btnMapAdd.hide()
			savePoint()
		}

		viewModel.message.observe(viewLifecycleOwner) {
			lifecycleScope.launch {
				showSnackBar(it)
			}
		}

		viewModel.isLoading.observe(viewLifecycleOwner) {
			lifecycleScope.launch {
				binding.pbAddMap.isVisible = it
			}
		}

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
		val map = childFragmentManager.findFragmentById(R.id.map_add_fragment) as SupportMapFragment?
		map?.getMapAsync(mapCallback)

	}

	private val mapCallback = OnMapReadyCallback {
		googleMap = it
		googleMap.setOnMyLocationClickListener(this)
		enableMyLocation(googleMap)
		googleMap.setOnCameraIdleListener {
			midPoint = googleMap.cameraPosition.target
		}
	}

	private fun savePoint() {
		binding.btnMapAdd.isEnabled = false
		googleMap.addMarker(
			MarkerOptions()
				.position(midPoint)
				.title(event.pinTitle)
		)

		event.pinCreated = System.currentTimeMillis()
		event.lat = midPoint.latitude
		event.lng = midPoint.longitude

		lifecycleScope.launch {
			viewModel.saveGeoPoint(event)
		}
	}


	private fun showSnackBar(message: String){
		isSnackbarVisible = true
		snack = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
		snack.setAction("OK") {
			try {
				findNavController().navigate(R.id.action_mapAddFragment_to_menuAddFragment)
				snack.dismiss()
				isSnackbarVisible = false
			} catch (e: Exception) {
				snack.dismiss()
				println("error: ${e.message}")
			}
		}
		snack.show()
	}

	override fun onDestroy() {
		if(isSnackbarVisible) {
			snack.dismiss()
		}
		super.onDestroy()
	}

	private fun enableMyLocation(mMap: GoogleMap) {
		if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED
		) {
			googleMap.isMyLocationEnabled = true
			// app crashes when no location, even though callback is supposedly called on success?
			fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
				loc?.let {
					mMap.animateCamera(CameraUpdateFactory
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
			requestPermission(requireActivity() as AppCompatActivity)
		}
	}

	private fun requestPermission(activity: AppCompatActivity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
		} else {
			ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
				LOCATION_PERMISSION_REQUEST_CODE)
		}
	}

	override fun onMyLocationClick(location: Location) {
		val position = CameraPosition.builder()
			.target(LatLng(location.latitude, location.longitude))
			.zoom(18f)
			.build()
		with(googleMap) {
			moveCamera(CameraUpdateFactory.newCameraPosition(position))
		}
	}

	override fun onMyLocationButtonClick(): Boolean {
		return false
	}

	override fun onCameraIdle() {
		midPoint = googleMap.cameraPosition.target
	}
}