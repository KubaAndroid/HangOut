package jw.adamiak.hangout.ui.events.events_map

import android.app.Application
import android.app.NotificationManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import jw.adamiak.hangout.data.remote.MapObject
import jw.adamiak.hangout.data.remote.PIN_TYPE
import jw.adamiak.hangout.utils.Helpers.setMarkerType
import jw.adamiak.hangout.utils.NOTIFICATION_RADIUS
import jw.adamiak.hangout.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val app: Application): AndroidViewModel(app) {
	private val db: FirebaseFirestore = Firebase.firestore

	private var _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean>
		get() = _isLoading

	private var _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	private val _userLocation = MutableLiveData<Location>()
//	val userLocation: LiveData<Location>
//		get() = _userLocation

	fun setUserLocation(location: Location){
		_userLocation.postValue(location)
	}

	private val notificationsManager = ContextCompat.getSystemService(app,
		NotificationManager::class.java) as NotificationManager


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
						if (point.pinType == PIN_TYPE.POLICE){
							checkEventsNearby(point)
						}

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

	private fun checkEventsNearby(mapObject: MapObject) {
		_userLocation.value?.let {
			var distance = floatArrayOf(0F, 0F)
			Location.distanceBetween(it.latitude, it.longitude, mapObject.lat, mapObject.lng, distance)
			if (distance[0] < NOTIFICATION_RADIUS) {
				notificationsManager.sendNotification(
					"${mapObject.pinTitle} in ${NOTIFICATION_RADIUS}m radius", app)
			}
		}

	}

}