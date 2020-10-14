package com.sit.capstone_lionfleet.business.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.sit.capstone_lionfleet.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MapFragment : Fragment(), OnMapReadyCallback,
    MapboxMap.OnMapClickListener, MapView.OnDidFinishLoadingStyleListener {

    val TAG = "MapFragment"

    private val viewModel: MapViewModel by viewModels()
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)
    }
    private lateinit var fabSettings: FloatingActionButton
    private lateinit var fabLayers: FloatingActionButton
    private lateinit var fabLocation: FloatingActionButton
    private var fabSettingsClicked = false
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView: MapView
    private var locationEngine: LocationEngine? = null
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val locationComponent: LocationComponent? = null
    private val directionApiClient: MapboxDirections? = null
    private val currentSelectedStationLatLng: LatLng? = null
    val ZOOM_LEVEL = 10.0
    val TILT_LEVEL = 20.0
    val MILLISECOND_SPEED = 1500
    private var mapStyleToggleIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!deviceHasInternetConnection()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.mapbox_error_no_connectivity),
                Toast.LENGTH_SHORT
            ).show()
        }
        //Set up expandable fab
        initFabs(view)
        //Set up Mapbox map
        mapView = view.findViewById(R.id.mapBoxMap)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun initFabs(view: View) {
        fabSettings = view.findViewById(R.id.fab_settings)
        fabLayers = view.findViewById(R.id.fab_map_layers)
        fabLocation = view.findViewById(R.id.fab_my_location)

        fabSettings.setOnClickListener {
            onSettingsBtnClick()
        }
        fabLayers.setOnClickListener {
            if (mapboxMap.style != null) {
                mapStyleToggle(mapboxMap)
            }

        }
        fabLocation.setOnClickListener {
            enableLocationComponent(mapboxMap)
        }
    }

    private fun mapStyleToggle(mapboxMap: MapboxMap) {
        when (mapStyleToggleIndex) {
            0 -> {
                mapboxMap.setStyle(Style.LIGHT)
                mapStyleToggleIndex++
            }
            1 -> {
                mapboxMap.setStyle(Style.DARK)
                mapStyleToggleIndex++
            }
            2 -> {
                mapboxMap.setStyle(Style.TRAFFIC_DAY)
                mapStyleToggleIndex++
            }
            3 -> {
                mapboxMap.setStyle(Style.TRAFFIC_NIGHT)
                mapStyleToggleIndex++
            }
            4 -> {
                mapboxMap.setStyle(Style.MAPBOX_STREETS)
                mapStyleToggleIndex = 0
            }
        }
    }

    private fun onSettingsBtnClick() {
        setVisibility(fabSettingsClicked)
        setAnimation(fabSettingsClicked)
        fabSettingsClicked = !fabSettingsClicked
    }

    private fun setVisibility(click: Boolean) {
        if (!click) {
            fabLayers.visibility = View.VISIBLE
            fabLocation.visibility = View.VISIBLE
        } else {
            fabLayers.visibility = View.GONE
            fabLocation.visibility = View.GONE
        }
    }

    private fun setAnimation(click: Boolean) {
        if (!click) {
            fabLayers.startAnimation(fromBottom)
            fabLocation.startAnimation(fromBottom)
            fabSettings.startAnimation(rotateOpen)
        } else {
            fabLayers.startAnimation(toBottom)
            fabLocation.startAnimation(toBottom)
            fabSettings.startAnimation(rotateClose)
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            customizeUISettings(mapboxMap)
            enableLocationComponent(mapboxMap)

        }
    }

    private fun customizeUISettings(mapboxMap: MapboxMap) {
        val uiSettings = mapboxMap.uiSettings

        uiSettings.isCompassEnabled = false
        uiSettings.isLogoEnabled = false
        uiSettings.isAttributionEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(mapboxMap: MapboxMap) {

        val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
            .pulseEnabled(true)
            .pulseColor(resources.getColor(R.color.primary_transparent_color))
            .pulseAlpha(.4f)
            .pulseInterpolator(BounceInterpolator())
            .build()

        val locationComponent = mapboxMap.locationComponent
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(requireContext(), mapboxMap.style!!)
                .locationComponentOptions(customLocationComponentOptions)
                .useDefaultLocationEngine(true)
                .build()
        )

        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING_GPS
        locationComponent.renderMode = RenderMode.NORMAL
        val position = CameraPosition.Builder()
            .target(
                LatLng(
                    locationComponent.lastKnownLocation?.latitude!!,
                    locationComponent.lastKnownLocation?.longitude!!
                )
            )
            .zoom(ZOOM_LEVEL)
            .tilt(TILT_LEVEL)
            .build()
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(position),
            MILLISECOND_SPEED
        );
        initLocationEngine()
        Log.d(
            TAG,
            "${locationComponent.lastKnownLocation?.latitude} ${locationComponent.lastKnownLocation?.longitude}"
        )
    }

    override fun onMapClick(point: LatLng): Boolean {
        return true
    }

    override fun onDidFinishLoadingStyle() {
    }

    private val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult) {
            val location: Location = result.lastLocation ?: return
            mapboxMap.locationComponent.forceLocationUpdate(location)

        }

        override fun onFailure(e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())

        val locationEngineRequest = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine!!.requestLocationUpdates(
            locationEngineRequest,
            locationEngineCallback,
            getMainLooper()
        )
        locationEngine!!.getLastLocation(locationEngineCallback)
    }

    private fun deviceHasInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        return activeNetwork != null
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}