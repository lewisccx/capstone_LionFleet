package com.sit.capstone_lionfleet.business.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.directions.v5.models.VoiceInstructions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.telemetry.events.FeedbackEvent
import com.mapbox.navigation.core.trip.session.*
import com.mapbox.navigation.ui.NavigationButton
import com.mapbox.navigation.ui.NavigationConstants
import com.mapbox.navigation.ui.SoundButton
import com.mapbox.navigation.ui.camera.DynamicCamera
import com.mapbox.navigation.ui.camera.NavigationCamera
import com.mapbox.navigation.ui.feedback.FeedbackBottomSheet
import com.mapbox.navigation.ui.feedback.FeedbackBottomSheetListener
import com.mapbox.navigation.ui.feedback.FeedbackItem
import com.mapbox.navigation.ui.internal.utils.BitmapEncodeOptions
import com.mapbox.navigation.ui.internal.utils.ViewUtils
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import com.mapbox.navigation.ui.voice.NavigationSpeechPlayer
import com.mapbox.navigation.ui.voice.SpeechPlayerProvider
import com.mapbox.navigation.ui.voice.VoiceInstructionLoader
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.core.extension.toPoint
import com.sit.capstone_lionfleet.utils.PRIMARY_ROUTE_BUNDLE_KEY
import com.sit.capstone_lionfleet.utils.getRouteFromBundle
import com.sit.capstone_lionfleet.utils.showFeedbackSentSnackBar
import kotlinx.android.synthetic.main.activity_mapbox_navigation.*
import okhttp3.Cache
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * This activity shows how to set up a basic turn-by-turn
 * navigation experience with many of the the Navigation SDK's
 * [MapboxNavigation] class' capabilities.
 */
class MapboxNavigationActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    VoiceInstructionsObserver,
    FeedbackBottomSheetListener {
    private val TAG = "SimpleMapboxNavigationKt"
    private val VOICE_INSTRUCTION_CACHE =
        "voice-instruction-cache"

    private val startTimeInMillis = 5000L
    private val countdownInterval = 10L
    private val ONE_HUNDRED_MILLISECONDS = 100
    private val PRIMARY_ROUTE_IDENTIFIER = "undefined"
    private val ALTERNATIVE_ROUTE_0 = "alternativeRoute0"
    private val ALTERNATIVE_ROUTE_1 = "alternativeRoute1"
    private var isFirstNavigation = true
    private val maxProgress = startTimeInMillis / countdownInterval
    private val locationEngineCallback = MyLocationEngineCallback(this)

    private var mapboxMap: MapboxMap? = null
    private var locationComponent: LocationComponent? = null
    private var symbolManager: SymbolManager? = null
    private var fasterRoutes: List<DirectionsRoute> = emptyList()
    private var originalRoute: DirectionsRoute? = null
    private var resultDestinationLatLng: LatLng? = null
    private var currentUserLatLng: LatLng? = null
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var localLocationEngine: LocationEngine
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var navigationMapboxMap: NavigationMapboxMap
    private lateinit var speechPlayer: NavigationSpeechPlayer
    private val mapboxReplayer = MapboxReplayer()

    //private var tripSessionStatus: TripSessionState? =null
    private var instructionSoundButton: NavigationButton? = null
    private var feedbackButton: NavigationButton? = null
    private var feedbackItem: FeedbackItem? = null
    private var feedbackEncodedScreenShot: String? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapbox_navigation)



        resultDestinationLatLng = intent.getParcelableExtra("DESTINATION_LATLNG_KEY_EXTRA")
        currentUserLatLng = intent.getParcelableExtra("USER_LOCATION_LATLNG_KEY_EXTRA")
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        localLocationEngine = LocationEngineProvider.getBestLocationEngine(applicationContext)

        val mapboxNavigationOptions = MapboxNavigation
            .defaultNavigationOptionsBuilder(this, getString(R.string.mapbox_access_token))
        mapboxNavigation = getMapboxNavigation(mapboxNavigationOptions)


        initViews()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(15.0))



        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            locationComponent = mapboxMap.locationComponent.apply {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(this@MapboxNavigationActivity, style)
                        .useDefaultLocationEngine(false)
                        .build()
                )
                cameraMode = CameraMode.TRACKING
                isLocationComponentEnabled = true
            }


            symbolManager = SymbolManager(mapView, mapboxMap, style)
            style.addImage("marker", IconFactory.getInstance(this).defaultMarker().bitmap)

            navigationMapboxMap = NavigationMapboxMap(
                mapView,
                mapboxMap,
                this,
                true
            )
            navigationMapboxMap.setCamera(DynamicCamera(mapboxMap))
            navigationMapboxMap.addProgressChangeListener(mapboxNavigation)
            navigationMapboxMap.setOnRouteSelectionChangeListener { route ->
                mapboxNavigation.setRoutes(
                    mapboxNavigation.getRoutes().toMutableList().apply {
                        remove(route)
                        add(0, route)

                    }
                )
            }
            initNavigationButton()

            when (originalRoute) {
                null -> {

                    Snackbar
                        .make(
                            navigationMapView,
                            R.string.msg_click_to_show_route,
                            LENGTH_SHORT
                        )
                        .show()
                }
                else -> restoreNavigation()
            }
        }

        //mapboxMap.addOnMapClickListener { click ->

        when (isFirstNavigation) {
            true -> {
                vibrate()
                findRoute(currentUserLatLng?.toPoint(), resultDestinationLatLng?.toPoint())
                isFirstNavigation = false
            }
            false -> {
                locationComponent?.lastKnownLocation?.let { location ->
                    mapboxMap.addOnMapClickListener { click ->
                        vibrate()
                        findRoute(location.toPoint(), click.toPoint())
                        false
                    }
                }
            }
        }
        routeLoading.visibility = VISIBLE
        Log.d(TAG, "requestRoutes")

        //     false
        // }

        initializeSpeechPlayer()
    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    ONE_HUNDRED_MILLISECONDS.toLong(),
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(ONE_HUNDRED_MILLISECONDS.toLong())
        }
    }

    private fun findRoute(origin: Point?, destination: Point?) {
        val routeOptions = RouteOptions.builder()
            .baseUrl(RouteUrl.BASE_URL)
            .user(RouteUrl.PROFILE_DEFAULT_USER)
            .profile(RouteUrl.PROFILE_DRIVING_TRAFFIC)
            .geometries(RouteUrl.GEOMETRY_POLYLINE6)
            .requestUuid("")
            .accessToken(getString(R.string.mapbox_access_token))
            .coordinates(listOf(origin, destination))
            .alternatives(true)
            .build()
        symbolManager?.deleteAll()
        symbolManager?.create(
            SymbolOptions()
                .withIconImage("marker")
                .withGeometry(destination!!)
        )
        mapboxNavigation.requestRoutes(
            routeOptions,
            routesReqCallback
        )
    }

    private fun initializeSpeechPlayer() {
        val cache = Cache(File(application.cacheDir, VOICE_INSTRUCTION_CACHE), 10 * 1024 * 1024)
        val voiceInstructionLoader =
            VoiceInstructionLoader(application, Mapbox.getAccessToken(), cache)
        val speechPlayerProvider =
            SpeechPlayerProvider(application, Locale.US.language, true, voiceInstructionLoader)
        speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
    }

    private fun initViews() {
        instructionView.visibility = GONE
        feedbackButton = instructionView.retrieveFeedbackButton().apply {
            hide()
            addOnClickListener {
                feedbackItem = null
                feedbackEncodedScreenShot = null
                supportFragmentManager.let {
                    mapboxMap?.snapshot(this@MapboxNavigationActivity::encodeSnapshot)
                    FeedbackBottomSheet.newInstance(
                        this@MapboxNavigationActivity,
                        NavigationConstants.FEEDBACK_BOTTOM_SHEET_DURATION
                    )
                        .show(it, FeedbackBottomSheet.TAG)
                }
            }
        }

        instructionSoundButton = instructionView.retrieveSoundButton().apply {
            hide()
            addOnClickListener {
                val soundButton = instructionSoundButton
                if (soundButton is SoundButton) {
                    speechPlayer.isMuted = soundButton.toggleMute()
                }
            }
        }
    }

    private fun encodeSnapshot(snapshot: Bitmap) {
        screenshotView.visibility = VISIBLE
        screenshotView.setImageBitmap(snapshot)
        mapView.visibility = View.INVISIBLE
        feedbackEncodedScreenShot = ViewUtils.encodeView(
            ViewUtils.captureView(mapView),
            BitmapEncodeOptions.Builder()
                .width(400).compressQuality(40).build()
        )
        screenshotView.visibility = View.INVISIBLE
        mapView.visibility = VISIBLE

        sendFeedback()
    }

    private fun sendFeedback() {

        val feedback = feedbackItem
        val screenShot = feedbackEncodedScreenShot
        if (feedback != null && !screenShot.isNullOrEmpty()) {
            mapboxNavigation?.postUserFeedback(
                feedback.feedbackType,
                feedback.description,
                FeedbackEvent.UI,
                screenShot,
                feedback.feedbackSubType.toTypedArray()
            )
            showFeedbackSentSnackBar(context = this, view = mapView)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initNavigationButton() {
        startNavigation.setOnClickListener {
            updateCameraOnNavigationStateChange(true)
            mapboxNavigation.registerVoiceInstructionsObserver(this)
            mapboxNavigation.startTripSession()
            val routes = mapboxNavigation.getRoutes()
            if (routes.isNotEmpty()) {
                initDynamicCamera(routes[0])
                updateViews(TripSessionState.STARTED)
            }
            navigationMapboxMap.showAlternativeRoutes(false)
        }
    }

    private fun startLocationUpdates() {
        val request = LocationEngineRequest.Builder(1000L)
            .setFastestInterval(500L)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .build()
        try {
            localLocationEngine.requestLocationUpdates(
                request,
                locationEngineCallback,
                Looper.getMainLooper()
            )
            if (originalRoute == null) {
                localLocationEngine.getLastLocation(locationEngineCallback)
            }
        } catch (exception: SecurityException) {
            Timber.e(exception)
        }
    }

    private fun stopLocationUpdates() {
        localLocationEngine.removeLocationUpdates(locationEngineCallback)
    }

    private val routeProgressObserver = object : RouteProgressObserver {
        override fun onRouteProgressChanged(routeProgress: RouteProgress) {
            Timber.d("route progress %s", routeProgress.toString())
            instructionView.updateDistanceWith(routeProgress)
            navigationMapboxMap.onNewRouteProgress(routeProgress)
        }
    }

//    private val eHorizonObserver = object : EHorizonObserver {
//        override fun onElectronicHorizonUpdated(horizon: EHorizon, type: String) {
//            Timber.d("DEBUG EH horizon=$horizon")
//            Timber.d("DEBUG EH type=$type")
//        }
//
//        override fun onPositionUpdated(position: EHorizonPosition) {
//            Timber.d("DEBUG EH position=$position")
//        }
//    }

    private val routesObserver = object : RoutesObserver {
        override fun onRoutesChanged(routes: List<DirectionsRoute>) {
            navigationMapboxMap.drawRoutes(routes)
            if (routes.isEmpty()) {
                Toast.makeText(this@MapboxNavigationActivity, "Empty routes", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (mapboxNavigation.getTripSessionState() == TripSessionState.STARTED) {
                    initDynamicCamera(routes[0])
                }
                routeLoading.visibility = View.INVISIBLE
            }
            Timber.d("route changed %s", routes.toString())
        }
    }

//    private val fasterRouteSelectionTimer: CountDownTimer =
//        object : CountDownTimer(startTimeInMillis, countdownInterval) {
//            override fun onTick(millisUntilFinished: Long) {
//                Timber.d("FASTER_ROUTE: millisUntilFinished $millisUntilFinished")
//                fasterRouteAcceptProgress.progress =
//                    (maxProgress - millisUntilFinished / countdownInterval).toInt()
//            }
//
//            override fun onFinish() {
//                Timber.d("FASTER_ROUTE: finished")
//                this@SimpleMapboxNavigationKt.fasterRoutes = emptyList()
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            }
//        }
//
//    private val fasterRouteObserver = object : FasterRouteObserver {
//        override fun onFasterRoute(
//            currentRoute: DirectionsRoute,
//            alternatives: List<DirectionsRoute>,
//            isAlternativeFaster: Boolean
//        ) {
//            if (isAlternativeFaster) {
//                this@SimpleMapboxNavigationKt.fasterRoutes = alternatives
//                fasterRouteSelectionTimer.start()
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        }
//    }

    private val routesReqCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
//            if(isFirstNavigation){
//                startNavigation.visibility = VISIBLE
//            }
            if (routes.isNotEmpty() && mapboxNavigation.getTripSessionState() == TripSessionState.STOPPED) {
                originalRoute = routes[0]

                startNavigation.visibility = VISIBLE
            }
            navigationMapboxMap.drawRoutes(routes)
            routeLoading.visibility = View.INVISIBLE
            Timber.d("route request success %s", routes.toString())
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            symbolManager?.deleteAll()
            Timber.e("route request failure %s", throwable.toString())
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            symbolManager?.deleteAll()
            Timber.d("route request canceled")
        }
    }

    private val tripSessionStateObserver = object : TripSessionStateObserver {
        override fun onSessionStateChanged(tripSessionState: TripSessionState) {
            when (tripSessionState) {
                TripSessionState.STARTED -> {
                    updateViews(TripSessionState.STARTED)
                    stopLocationUpdates()
                    startNavigation.visibility = GONE
                }
                TripSessionState.STOPPED -> {
                    updateViews(TripSessionState.STOPPED)

                    startLocationUpdates()
                    //startNavigation.visibility = VISIBLE
                    updateCameraOnNavigationStateChange(false)
                }
            }
        }
    }
    private val bannerInstructionObserver = object : BannerInstructionsObserver {
        override fun onNewBannerInstructions(bannerInstructions: BannerInstructions) {
            instructionView.updateBannerInstructionsWith(bannerInstructions)
        }
    }

    private fun updateCameraOnNavigationStateChange(
        navigationStarted: Boolean
    ) {
        if (::navigationMapboxMap.isInitialized) {
            navigationMapboxMap.apply {
                if (navigationStarted) {
                    updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
                    updateLocationLayerRenderMode(RenderMode.GPS)
                } else {
                    symbolManager?.deleteAll()
                    updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE)
                    updateLocationLayerRenderMode(RenderMode.COMPASS)
                }
            }
        }
    }

    private fun updateViews(tripSessionState: TripSessionState) {
        when (tripSessionState) {
            TripSessionState.STARTED -> {
                instructionView.visibility = VISIBLE
                feedbackButton?.show()
                instructionSoundButton?.show()
            }
            TripSessionState.STOPPED -> {

                instructionView.visibility = GONE
                feedbackButton?.hide()
                instructionSoundButton?.hide()
            }
        }
    }

    private fun initDynamicCamera(route: DirectionsRoute) {
        if (::navigationMapboxMap.isInitialized) {
            navigationMapboxMap.startCamera(route)
        }
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView.onStart()



        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerTripSessionStateObserver(tripSessionStateObserver)
        mapboxNavigation.registerBannerInstructionsObserver(bannerInstructionObserver)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()

        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterTripSessionStateObserver(tripSessionStateObserver)
        mapboxNavigation.detachFasterRouteObserver()
        stopLocationUpdates()

        if (mapboxNavigation.getRoutes().isEmpty() &&
            mapboxNavigation.getTripSessionState() == TripSessionState.STARTED
        ) {
            // use this to kill the service and hide the notification when going into the background in the Free Drive state,
            // but also ensure to restart Free Drive when coming back from background by using the channel
            mapboxNavigation.unregisterVoiceInstructionsObserver(this)
            mapboxNavigation.stopTripSession()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()

        mapboxReplayer.finish()
        mapboxNavigation.unregisterVoiceInstructionsObserver(this)
        mapboxNavigation.stopTripSession()
        mapboxNavigation.onDestroy()


        speechPlayer.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)

        // This is not the most efficient way to preserve the route on a device rotation.
        // This is here to demonstrate that this event needs to be handled in order to
        // redraw the route line after a rotation.
        originalRoute?.let {
            outState.putString(PRIMARY_ROUTE_BUNDLE_KEY, it.toJson())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        originalRoute = getRouteFromBundle(savedInstanceState)
    }

    override fun onNewVoiceInstructions(voiceInstructions: VoiceInstructions) {
        speechPlayer.play(voiceInstructions)
    }

    private class MyLocationEngineCallback(activity: MapboxNavigationActivity) :
        LocationEngineCallback<LocationEngineResult> {

        private val activityRef = WeakReference(activity)

        override fun onSuccess(result: LocationEngineResult?) {
            result?.locations?.firstOrNull()?.let {
                activityRef.get()?.locationComponent?.forceLocationUpdate(it)
            }
        }

        override fun onFailure(exception: Exception) {
        }
    }

    private fun getMapboxNavigation(optionsBuilder: NavigationOptions.Builder): MapboxNavigation {

        return MapboxNavigation(optionsBuilder.build())
    }

//    private fun shouldSimulateRoute(): Boolean {
//        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
//            .getBoolean(this.getString(R.string.simulate_route_key), false)
//    }
//
//    private fun shouldRecordHistory(): Boolean {
//        return PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
//            .getBoolean(this.getString(R.string.nav_native_history_collect_key), false)
//    }

    @SuppressLint("MissingPermission")
    private fun restoreNavigation() {
        originalRoute?.let {
            mapboxNavigation.setRoutes(listOf(it))
            navigationMapboxMap.addProgressChangeListener(mapboxNavigation)
            navigationMapboxMap.startCamera(mapboxNavigation.getRoutes()[0])
            updateCameraOnNavigationStateChange(true)
            mapboxNavigation.startTripSession()
        }
    }

    override fun onFeedbackSelected(feedbackItem: FeedbackItem?) {
        feedbackItem?.let { feedback ->
            this.feedbackItem = feedback
            sendFeedback()
        }
    }

    override fun onFeedbackDismissed() {
        TODO("Not yet implemented")
    }
}
