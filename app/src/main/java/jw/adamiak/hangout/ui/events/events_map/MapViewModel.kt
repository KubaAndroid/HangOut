package jw.adamiak.hangout.ui.events.events_map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.utils.Helpers.setMarkerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel: ViewModel() {
	private val db: FirebaseFirestore = Firebase.firestore

	private var _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	suspend fun getGeoPoints(map: GoogleMap): MutableList<MapObject>  =
		withContext(Dispatchers.IO) {
			val geopoints = mutableListOf<MapObject>()
			db.collection("geopoints")
				.get()
				.addOnSuccessListener { result ->
					for (geoPoint in result) {
						val point = geoPoint.toObject<MapObject>()
						point.uuidString = geoPoint.id
						geopoints.add(point)
						val pinIcon = setMarkerType(point.pinType)
						addMarkerToMap(map, point, pinIcon)
					}
				}
				.addOnFailureListener { e ->
					_message.value = e.message.toString()
				}
			geopoints
		}


	private fun addMarkerToMap(map: GoogleMap, point: MapObject, pinIcon: Int) {
		map.addMarker(
			MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(pinIcon))
				.position(LatLng(point.lat, point.lng))
				.title(point.pinTitle)
				.snippet(point.comment)
		)
	}


}