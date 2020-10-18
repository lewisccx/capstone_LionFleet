package com.sit.capstone_lionfleet.business.map

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.alan.alansdk.button.AlanButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.mapbox.turf.TurfConversion
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.base.response.Resource
import com.sit.capstone_lionfleet.business.map.constants.IconIdConstants.INDIVIDUAL_SELECTED_STATION_ICON_IMAGE_ID
import com.sit.capstone_lionfleet.business.map.constants.IconIdConstants.INDIVIDUAL_STATION_ICON_IMAGE_ID
import com.sit.capstone_lionfleet.business.map.constants.LayerIdConstants.INDIVIDUAL_SELECTED_STATION_SYMBOL_LAYER_ID
import com.sit.capstone_lionfleet.business.map.constants.LayerIdConstants.INDIVIDUAL_STATION_SYMBOL_LAYER_ID
import com.sit.capstone_lionfleet.business.map.constants.PropertyConstants.Companion.MAPBOX_SG_LAT_LNG_COORDINATES
import com.sit.capstone_lionfleet.business.map.constants.PropertyConstants.Companion.STATION_ID
import com.sit.capstone_lionfleet.business.map.constants.SourceIdConstants.INDIVIDUAL_SELECTED_STATION_SOURCE_ID
import com.sit.capstone_lionfleet.business.map.constants.SourceIdConstants.INDIVIDUAL_STATION_SOURCE_ID
import com.sit.capstone_lionfleet.core.di.ImageService
import com.sit.capstone_lionfleet.core.extension.hide
import com.sit.capstone_lionfleet.core.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_station_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import javax.inject.Inject

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
    private lateinit var vehicleViewPager: ViewPager
    private lateinit var vehicleTabLayout: TabLayout
    private lateinit var fabSettings: FloatingActionButton
    private lateinit var fabLayers: FloatingActionButton
    private lateinit var fabLocation: FloatingActionButton
    private var fabSettingsClicked = false

    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView: MapView
    private var locationEngine: LocationEngine? = null
    private var selectedStationSymbolLayer: SymbolLayer? = null
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private var locationComponent: LocationComponent? = null
    private var directionApiClient: MapboxDirections? = null
    private var currentSelectedStationLatLng: LatLng? = null
    val STATION_ICON_ANIMATION_SPEED: Long = 300
    val ZOOM_LEVEL = 10.0
    val TILT_LEVEL = 20.0
    val MILLISECOND_SPEED = 1500
    private var mapStyleToggleIndex = 0
    private var stationSelected = false

    @Inject
    lateinit var imageService: ImageService
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
        //Set up station card
        initView(view)
        //Set up ViewPager
        initVehicleViewPager(view)
        //Set up Mapbox map
        mapView = view.findViewById(R.id.mapBoxMap)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    private fun initVehicleViewPager(view: View) {
        vehicleViewPager = view.findViewById(R.id.vehiclesViewPager)
        vehicleTabLayout = view.findViewById(R.id.vehicleTabLayout)
        vehicleTabLayout.setupWithViewPager(vehicleViewPager)
    }

    private fun initView(view: View) {
        view.findViewById<MaterialCardView>(R.id.station_card).visibility = View.INVISIBLE
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
            mapView.addOnDidFinishLoadingStyleListener(this@MapFragment)
            mapboxMap.addOnMapClickListener(this@MapFragment)

            customizeUISettings(mapboxMap)
            enableLocationComponent(mapboxMap)
            setUpMapData(style)
            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.stationVehiclesDataState.observe(viewLifecycleOwner, { dataState ->

            when (dataState) {
                is Resource.Success -> {
                    if (dataState.value.isEmpty()) {
                        station_no_vehicle_view.show()
                        vehicleViewPager.hide()
                        vehicleTabLayout.hide()
                    } else {
                        showStationInfoWindow()
                        vehicleViewPager.adapter = StationAdapter(dataState.value, imageService) {

                            //navigate to vehicle booking schedule fragment
                            Toast.makeText(requireContext(), it.id, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.something_went_wrong_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    station_vehicles_Loading.show()
                    station_no_vehicle_view.hide()
                    vehicleViewPager.hide()
                    vehicleTabLayout.hide()
                }
            }

        })
    }

    private fun setUpMapData(loadedMapStyle: Style) {
        addStationsToMap(loadedMapStyle)
        initSelectedStationIconLayer(loadedMapStyle)
    }

    private fun addStationsToMap(loadedMapStyle: Style) {
        loadedMapStyle.addImage(
            INDIVIDUAL_STATION_ICON_IMAGE_ID, BitmapFactory.decodeResource(
                requireContext().resources, R.drawable.pin_notselected
            )
        )

        val stationVectorSource = VectorSource(
            INDIVIDUAL_STATION_SOURCE_ID, "mapbox://lewisccx.ckdd59dam1ecn25mqksbyqifw-1vh7z"
        )
        Log.d(TAG, stationVectorSource.attribution)
        loadedMapStyle.addSource(stationVectorSource)
        val stationSymbolLayer = SymbolLayer(
            INDIVIDUAL_STATION_SYMBOL_LAYER_ID,
            INDIVIDUAL_STATION_SOURCE_ID
        ).withProperties(
            iconImage(INDIVIDUAL_STATION_ICON_IMAGE_ID),
            iconAllowOverlap(true),
            iconIgnorePlacement(true)
        )
        stationSymbolLayer.sourceLayer = INDIVIDUAL_STATION_SOURCE_ID
        loadedMapStyle.addLayer(stationSymbolLayer)
    }

    private fun initSelectedStationIconLayer(loadedMapStyle: Style) {
        val emptySource = FeatureCollection.fromFeatures(arrayOf())
        val selectedStationMarkerSource =
            GeoJsonSource(INDIVIDUAL_SELECTED_STATION_SOURCE_ID, emptySource)

        loadedMapStyle.addImage(
            INDIVIDUAL_SELECTED_STATION_ICON_IMAGE_ID,
            BitmapFactory.decodeResource(requireContext().resources, R.drawable.pin_selected)
        )
        loadedMapStyle.addSource(selectedStationMarkerSource)
        selectedStationSymbolLayer = SymbolLayer(
            INDIVIDUAL_SELECTED_STATION_SYMBOL_LAYER_ID, INDIVIDUAL_SELECTED_STATION_SOURCE_ID
        )
        selectedStationSymbolLayer?.withProperties(
            iconImage(INDIVIDUAL_SELECTED_STATION_ICON_IMAGE_ID),
            iconAllowOverlap(true),
            iconIgnorePlacement(true)

        )
        loadedMapStyle.addLayer(selectedStationSymbolLayer!!)
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

        locationComponent = mapboxMap.locationComponent
        locationComponent!!.activateLocationComponent(
            LocationComponentActivationOptions.builder(requireContext(), mapboxMap.style!!)
                .locationComponentOptions(customLocationComponentOptions)
                .useDefaultLocationEngine(true)
                .build()
        )

        locationComponent!!.isLocationComponentEnabled = true
        locationComponent!!.cameraMode = CameraMode.TRACKING_GPS
        locationComponent!!.renderMode = RenderMode.NORMAL
        val position = CameraPosition.Builder()
            .target(
                LatLng(
                    locationComponent!!.lastKnownLocation?.latitude!!,
                    locationComponent!!.lastKnownLocation?.longitude!!
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
            "${locationComponent!!.lastKnownLocation?.latitude} ${locationComponent!!.lastKnownLocation?.longitude}"
        )
    }

    override fun onMapClick(point: LatLng): Boolean {
        val stationList: List<Feature> = getRenderedFeatures(
            mapboxMap.projection.toScreenLocation(point), INDIVIDUAL_STATION_SYMBOL_LAYER_ID
        )
        selectedStationSymbolLayer = mapboxMap.style?.getLayer(
            INDIVIDUAL_SELECTED_STATION_SYMBOL_LAYER_ID
        ) as SymbolLayer


        if (stationList.isNotEmpty()) {
            setStationCardView()
            adjustAlanBtnPosition(true)
            evaluateClick(stationList)
            val selectedStationSymbolLayer =
                mapboxMap.style!!.getLayer(INDIVIDUAL_SELECTED_STATION_SYMBOL_LAYER_ID) as SymbolLayer?
            val selectedStation = mapboxMap.queryRenderedFeatures(
                mapboxMap.projection.toScreenLocation(point),
                INDIVIDUAL_SELECTED_STATION_SYMBOL_LAYER_ID
            )
            if (selectedStation.size > 0 && stationSelected) {
                return true
            }

            mapboxMap.style?.getSourceAs<GeoJsonSource>(
                INDIVIDUAL_SELECTED_STATION_SOURCE_ID
            )?.setGeoJson(
                FeatureCollection.fromFeatures(
                    arrayOf(Feature.fromGeometry(stationList[0].geometry()))
                )
            )
            if (stationSelected) {
                decreaseIconSize(selectedStationSymbolLayer);
            }
            if (stationList.isNotEmpty()) {
                increaseIconSize(selectedStationSymbolLayer);
            }
        } else {

            decreaseIconSize(selectedStationSymbolLayer);
            view?.findViewById<MaterialCardView>(R.id.station_card)?.visibility = View.GONE
            adjustAlanBtnPosition(false)
        }

        return true
    }

    private fun evaluateClick(stationList: List<Feature>) {
        val selectedStation: Feature = stationList[0]

        Log.d(TAG, "Before if check ${selectedStation.getStringProperty(STATION_ID)}")
        if (selectedStation.hasProperty(STATION_ID)) {
            Log.d(TAG, "After if check ${selectedStation.getStringProperty(STATION_ID)}")
            val selectedStationId = selectedStation.getStringProperty(STATION_ID)
            viewModel.setMapEvent(MapStateEvent.GetVehiclesByStationId, selectedStationId)
        } else {
            Toast.makeText(requireContext(), R.string.mapbox_dataset_error, Toast.LENGTH_SHORT)
                .show()
        }

        val selectedStationLocationPoint = selectedStation.geometry() as Point
        currentSelectedStationLatLng = LatLng(
            selectedStationLocationPoint.coordinates()[1],
            selectedStationLocationPoint.coordinates()[0]
        )
        getInformationFromDirectionsApi(currentSelectedStationLatLng!!);
        adjustCameraZoom()
    }

    private fun getInformationFromDirectionsApi(currentSelectedStationLatLng: LatLng) {
        directionApiClient = MapboxDirections.builder()
            .accessToken(requireContext().resources.getString(R.string.mapbox_access_token))
            .origin(getAppropriateOriginPoint())
            .destination(
                Point.fromLngLat(
                    currentSelectedStationLatLng.longitude,
                    currentSelectedStationLatLng.latitude
                )
            )
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_WALKING)
            .build()



        directionApiClient?.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                response: Response<DirectionsResponse>
            ) {
                if (response.body() == null) {
                    Toast.makeText(requireContext(), R.string.no_route_found, Toast.LENGTH_SHORT)
                        .show()
                } else if (response.body()!!.routes().size < 1) {
                    Toast.makeText(requireContext(), R.string.no_route_found, Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    //retrieve and the navigation route on the map
                    //drawNavigationPolylineRoute(response.body()!!.routes()[0])
                    setVehicleWalkToDistance(response.body());
                    setWalkToStationTime(response.body());
//                    getVehicleLocation(
//                        Point.fromLngLat(
//                            currentSelectedStationLatLng.longitude,
//                            currentSelectedStationLatLng.latitude
//                        )
//                    );
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    R.string.fail_to_retrieve_route_error,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun setWalkToStationTime(response: DirectionsResponse?) {
        val textWalkingTime = view?.findViewById<TextView>(R.id.walking_time_tv)
        if (response!!.routes()[0].duration() != null) {
            val decimalFormat =
                DecimalFormat(if (response.routes()[0].duration()!! / 60 >= 10) "00" else "0")

            textWalkingTime?.text = String.format(
                getString(R.string.walking_minutes), decimalFormat.format(
                    response.routes()[0].duration()!! / 60
                )
            )
        } else {
            textWalkingTime?.text = getString(R.string.travel_time_unknown)
        }
    }

    private fun setVehicleWalkToDistance(response: DirectionsResponse?) {
        val textWalkingDistance = view?.findViewById<TextView>(R.id.distance_num_tv)
        if (response!!.routes()[0].distance() != null) {
            textWalkingDistance?.text = " " + String.format(
                getString(R.string.walking_miles),
                DecimalFormat("0.0").format(
                    TurfConversion.convertLength(
                        response.routes()[0].distance()!!/1000,
                        "meters", "meters"
                    )
                )
            )
        }
    }

    private fun getAppropriateOriginPoint(): Point {
        return if (locationComponent != null) {
            Point.fromLngLat(
                mapboxMap.locationComponent.lastKnownLocation!!.longitude,
                mapboxMap.locationComponent.lastKnownLocation!!.latitude
            )
        } else {
            Point.fromLngLat(
                MAPBOX_SG_LAT_LNG_COORDINATES.longitude,
                MAPBOX_SG_LAT_LNG_COORDINATES.latitude
            )
        }
    }

    private fun showStationInfoWindow() {
        vehicleViewPager.show()
        vehicleTabLayout.show()
        station_vehicles_Loading.hide()
        station_no_vehicle_view.hide()
    }

    private fun adjustCameraZoom() {
        var zoom: Double = 14.0;
        if (mapboxMap.cameraPosition.zoom < 12) {
            zoom = 14.0
        } else {
            zoom = mapboxMap.cameraPosition.zoom
        }
        mapboxMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentSelectedStationLatLng!!.latitude,
                    currentSelectedStationLatLng!!.longitude
                ),
                zoom
            )
        )
    }

    private fun decreaseIconSize(symbolLayer: SymbolLayer?) {
        val symbolLayerIconAnimator = ValueAnimator()
        symbolLayerIconAnimator.setObjectValues(1.4f, 1f)

        symbolLayerIconAnimator.duration = STATION_ICON_ANIMATION_SPEED
        symbolLayerIconAnimator.addUpdateListener {
            symbolLayer?.setProperties(
                iconImage(INDIVIDUAL_STATION_ICON_IMAGE_ID),
                iconSize(it.animatedValue as Float)
            )
        }
        symbolLayerIconAnimator.start()
        stationSelected = false
    }

    private fun increaseIconSize(symbolLayer: SymbolLayer?) {
        val symbolLayerIconAnimator = ValueAnimator()
        symbolLayerIconAnimator.setObjectValues(1f, 1.4f)

        symbolLayerIconAnimator.duration = STATION_ICON_ANIMATION_SPEED
        symbolLayerIconAnimator.addUpdateListener {
            symbolLayer?.setProperties(
                iconImage(INDIVIDUAL_SELECTED_STATION_ICON_IMAGE_ID),
                iconSize(it.animatedValue as Float)
            )
        }
        symbolLayerIconAnimator.start()
        stationSelected = true
    }

    private fun adjustAlanBtnPosition(alanBtnAboveCardView: Boolean) {
        val alanButton = activity?.findViewById<AlanButton>(R.id.alan_button)
        val param = alanButton?.layoutParams as ViewGroup.MarginLayoutParams
        if (alanBtnAboveCardView) {
//            Log.d(TAG, "alanBtnAboveCardView")

            param.bottomMargin =
                view?.findViewById<MaterialCardView>(R.id.station_card)!!.height + 135
//            Log.d(TAG, "param bottomMargin ${param.bottomMargin}")
            alanButton.layoutParams = param
        } else {
            param.bottomMargin = 135
//            Log.d(TAG, "param bottomMargin ${param.bottomMargin}")
            alanButton.layoutParams = param
        }
    }

    private fun setStationCardView() {
        view?.findViewById<MaterialCardView>(R.id.station_card)?.visibility = View.VISIBLE
    }

    override fun onDidFinishLoadingStyle() {
        if (mapboxMap.style != null) {
            mapboxMap.getStyle { style ->
                setUpMapData(style)
            }
        }
    }

    private val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult) {
            val location: Location = result.lastLocation ?: return
            locationComponent!!.forceLocationUpdate(location)
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

    private fun getRenderedFeatures(screenPoint: PointF, layerId: String): List<Feature> {
        return mapboxMap.queryRenderedFeatures(
            screenPoint, layerId
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapboxMap.removeOnMapClickListener(this)
        mapView.removeOnDidFinishLoadingStyleListener(this)
        //handle directionApiClient

        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}