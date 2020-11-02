package com.sit.capstone_lionfleet.business.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.directions.v5.models.VoiceInstructions
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.base.internal.extensions.applyDefaultParams
import com.mapbox.navigation.base.internal.extensions.coordinates
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
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
import com.mapbox.navigation.ui.instruction.NavigationAlertView
import com.mapbox.navigation.ui.internal.utils.ViewUtils
import com.mapbox.navigation.ui.map.NavigationMapboxMap
import com.mapbox.navigation.ui.map.OnWayNameChangedListener
import com.mapbox.navigation.ui.summary.SummaryBottomSheet
import com.mapbox.navigation.ui.voice.NavigationSpeechPlayer
import com.mapbox.navigation.ui.voice.SpeechPlayerProvider
import com.mapbox.navigation.ui.voice.VoiceInstructionLoader
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.core.extension.toPoint
import com.sit.capstone_lionfleet.utils.showFeedbackSentSnackBar
import kotlinx.android.synthetic.main.fragment_mapbox_navigation.*
import okhttp3.Cache
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MapboxNavigationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapboxNavigationFragment : Fragment(R.layout.fragment_mapbox_navigation), OnMapReadyCallback,
    FeedbackBottomSheetListener, OnWayNameChangedListener {
    val TAG = "MapboxNavigationFragment"
    private val routeOverviewPadding by lazy {
        buildRouteOverviewPadding()
    }

    companion object {
        const val VOICE_INSTRUCTION_CACHE = "voice-instruction-cache"

            fun newInstance(latLng: LatLng): MapboxNavigationFragment {
                val fragment = MapboxNavigationFragment()
                val args = Bundle()
                args.putParcelable("DESTINATION_LATLNG_KEY_BUNDLE", latLng)
                fragment.arguments =args
                return fragment
            }

    }

    private lateinit var mapboxNavigation: MapboxNavigation
    private var navigationMapboxMap: NavigationMapboxMap? = null
    private var mapboxMap: MapboxMap? = null
    private var directionRoute: DirectionsRoute? = null
    private lateinit var destination: LatLng

    private lateinit var speechPlayer: NavigationSpeechPlayer
    private lateinit var summaryBehavior: BottomSheetBehavior<SummaryBottomSheet>
    private lateinit var routeOverviewButton: ImageButton
    private lateinit var feedbackButton: NavigationButton
    private lateinit var instructionSoundButton: NavigationButton
    private lateinit var alertView: NavigationAlertView
    private val mapboxReplayer = MapboxReplayer()
    private var feedbackItem: FeedbackItem? = null
    private var feedbackEncodedScreenShot: String? = null
    private var locationComponent: LocationComponent? = null
    private lateinit var cancelBtn: AppCompatImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            destination = it.getParcelable<LatLng>("DESTINATION_LATLNG_KEY_BUNDLE")!!

        }
        Log.d(TAG, "$destination")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapbox_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initNavigation()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        initializeSpeechPlayer()

        initListeners()
    }

    @SuppressLint("MissingPermission")
    private fun initListeners() {
        if (mapboxNavigation.getRoutes().isNotEmpty()) {
            updateCameraOnNavigationStateChange(true)
            navigationMapboxMap?.startCamera(mapboxNavigation.getRoutes()[0])

            mapboxNavigation.startTripSession()
        }
    }

    private fun initializeSpeechPlayer() {
        val cache = Cache(
            File(
                requireContext().cacheDir,
                VOICE_INSTRUCTION_CACHE
            ),
            10 * 1024 * 1024
        )
        val voiceInstructionLoader =
            VoiceInstructionLoader(requireContext(), Mapbox.getAccessToken(), cache)
        val speechPlayerProvider =
            SpeechPlayerProvider(
                requireContext(),
                Locale.US.language,
                true,
                voiceInstructionLoader
            )
        speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
    }
    @SuppressLint("MissingPermission")
    private fun initNavigation() {
        mapboxNavigation = MapboxNavigation(
            MapboxNavigation.defaultNavigationOptionsBuilder(
                requireContext(),
                getString(R.string.mapbox_access_token)
            )
                .locationEngine(getLocationEngine())
                .build()
        )
        mapboxNavigation.apply {
            registerTripSessionStateObserver(tripSessionStateObserver)
            registerRouteProgressObserver(routeProgressObserver)
            registerBannerInstructionsObserver(bannerInstructionObserver)
            registerVoiceInstructionsObserver(voiceInstructionsObserver)
        }



    }

    private val routeProgressObserver = object : RouteProgressObserver {
        override fun onRouteProgressChanged(routeProgress: RouteProgress) {
            instructionView.updateDistanceWith(routeProgress)
            summaryBottomSheet.update(routeProgress)
        }
    }

    private val bannerInstructionObserver = object : BannerInstructionsObserver {
        override fun onNewBannerInstructions(bannerInstructions: BannerInstructions) {
            instructionView.updateBannerInstructionsWith(bannerInstructions)
        }
    }
    private val voiceInstructionsObserver = object : VoiceInstructionsObserver {
        override fun onNewVoiceInstructions(voiceInstructions: VoiceInstructions) {
            speechPlayer.play(voiceInstructions)
        }
    }
    private val tripSessionStateObserver = object : TripSessionStateObserver {
        override fun onSessionStateChanged(tripSessionState: TripSessionState) {
            when (tripSessionState) {
                TripSessionState.STARTED -> {
                    updateViews(TripSessionState.STARTED)

                    navigationMapboxMap
                        ?.addOnWayNameChangedListener(this@MapboxNavigationFragment)
                    navigationMapboxMap?.updateWaynameQueryMap(true)
                }
                TripSessionState.STOPPED -> {
                    updateViews(TripSessionState.STOPPED)

                    if (mapboxNavigation.getRoutes().isNotEmpty()) {
                        navigationMapboxMap?.hideRoute()
                    }

                    navigationMapboxMap
                        ?.removeOnWayNameChangedListener(this@MapboxNavigationFragment)
                    navigationMapboxMap?.updateWaynameQueryMap(false)

                    updateCameraOnNavigationStateChange(false)
                }

            }
        }
    }
    private val cameraTrackingChangedListener = object : OnCameraTrackingChangedListener {
        override fun onCameraTrackingChanged(currentMode: Int) {
        }

        override fun onCameraTrackingDismissed() {
            if (mapboxNavigation.getTripSessionState() == TripSessionState.STARTED) {
                summaryBehavior.isHideable = true
                summaryBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                hideWayNameView()
            }
        }
    }
    private fun updateViews(tripSessionState: TripSessionState) {
        when (tripSessionState) {
            TripSessionState.STARTED -> {
                //startNavigation.visibility = View.GONE

                summaryBottomSheet.visibility = View.VISIBLE
                recenterBtn.hide()

                instructionView.visibility = View.VISIBLE
                feedbackButton.show()
                instructionSoundButton.show()
                showLogoAndAttribution()
            }
            TripSessionState.STOPPED -> {
                //startNavigation.visibility = View.VISIBLE
                //startNavigation.isEnabled = false

                summaryBottomSheet.visibility = View.GONE
                recenterBtn.hide()
                hideWayNameView()

                instructionView.visibility = View.GONE
                feedbackButton.hide()
                instructionSoundButton.hide()
            }
        }
    }

    private fun showLogoAndAttribution() {
        summaryBottomSheet.viewTreeObserver.addOnGlobalLayoutListener(
            object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    navigationMapboxMap?.retrieveMap()?.uiSettings?.apply {
                        val bottomMargin = summaryBottomSheet.measuredHeight
                        setLogoMargins(
                            logoMarginLeft,
                            logoMarginTop,
                            logoMarginRight,
                            bottomMargin
                        )
                        setAttributionMargins(
                            attributionMarginLeft,
                            attributionMarginTop,
                            attributionMarginRight,
                            bottomMargin
                        )
                    }
                    summaryBottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
    }

    private fun getLocationEngine(): LocationEngine {
        return LocationEngineProvider.getBestLocationEngine(requireActivity())
    }

    @SuppressLint("MissingPermission")
    private fun initViews() {




        summaryBottomSheet.visibility = View.GONE
        summaryBehavior = BottomSheetBehavior.from(summaryBottomSheet).apply {
            isHideable = false
            addBottomSheetCallback(bottomSheetCallback)
        }

        routeOverviewButton = requireView().findViewById(R.id.routeOverviewBtn)
        routeOverviewButton.setOnClickListener {
            navigationMapboxMap?.showRouteOverview(routeOverviewPadding)
            recenterBtn.show()
        }

        cancelBtn = requireView().findViewById(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            mapboxNavigation.stopTripSession()
        }

        recenterBtn.apply {
            hide()
            addOnClickListener {
                recenterBtn.hide()
                summaryBehavior.isHideable = false
                summaryBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                showWayNameView()
                navigationMapboxMap?.resetPadding()
                navigationMapboxMap
                    ?.resetCameraPositionWith(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
            }
        }
        wayNameView.apply {
            visibility = View.GONE
        }

        feedbackButton = instructionView.retrieveFeedbackButton().apply {
            hide()
            addOnClickListener {
                showFeedbackBottomSheet()
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

        alertView = instructionView.retrieveAlertView().apply {
            hide()
        }

    }

    private fun showFeedbackBottomSheet() {
        feedbackItem = null
        feedbackEncodedScreenShot = null
        requireFragmentManager().let {
            mapboxMap?.snapshot(this::encodeSnapshot)
            FeedbackBottomSheet.newInstance(
                this,
                NavigationConstants.FEEDBACK_BOTTOM_SHEET_DURATION
            )
                .show(it, FeedbackBottomSheet.TAG)
        }
    }

    private fun encodeSnapshot(snapshot: Bitmap) {
        screenshotView.visibility = View.VISIBLE
        screenshotView.setImageBitmap(snapshot)
        mapView.visibility = View.INVISIBLE
        feedbackEncodedScreenShot = ViewUtils.encodeView(ViewUtils.captureView(mapView))
        screenshotView.visibility = View.INVISIBLE
        mapView.visibility = View.VISIBLE

        sendFeedback()
    }

    private fun sendFeedback() {
        val feedback = feedbackItem
        val screenShot = feedbackEncodedScreenShot
        if (feedback != null && !screenShot.isNullOrEmpty()) {
            mapboxNavigation.postUserFeedback(
                feedback.feedbackType,
                feedback.description,
                FeedbackEvent.UI,
                screenShot,
                feedback.feedbackSubType.toTypedArray()
            )
            showFeedbackSentSnackBar(
                context = requireContext(),
                view = if (summaryBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    recenterBtn
                } else {
                    summaryBottomSheet
                },
                setAnchorView = true
            )
        }
    }

    private fun showWayNameView() {
        wayNameView.updateVisibility(!wayNameView.retrieveWayNameText().isNullOrEmpty())
    }

    private fun hideWayNameView() {
        wayNameView.updateVisibility(false)
    }

    private fun updateCameraOnNavigationStateChange(navigationStarted: Boolean) {
        navigationMapboxMap?.apply {
            if (navigationStarted) {
                updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS)
                updateLocationLayerRenderMode(RenderMode.GPS)
            } else {
                updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE)
                updateLocationLayerRenderMode(RenderMode.COMPASS)
            }
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (summaryBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                recenterBtn.show()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.moveCamera(CameraUpdateFactory.zoomTo(15.0))

        locationComponent?.lastKnownLocation?.let { originLocation ->
            mapboxNavigation.requestRoutes(
                RouteOptions.builder().applyDefaultParams()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .coordinates(originLocation.toPoint(), null, destination.toPoint())
                    .alternatives(true)
                    .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                    .build(),
                routesReqCallback
            )
        }
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            locationComponent = mapboxMap.locationComponent.apply {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(
                        requireContext(),
                        style
                    ).build()
                )
                cameraMode = CameraMode.TRACKING
                isLocationComponentEnabled = true
            }

            navigationMapboxMap = NavigationMapboxMap(mapView, mapboxMap, this, true).apply {
                addOnCameraTrackingChangedListener(cameraTrackingChangedListener)
                addProgressChangeListener(mapboxNavigation)
                setCamera(DynamicCamera(mapboxMap))
            }

//            if (shouldSimulateRoute()) {
//                mapboxNavigation
//                    .registerRouteProgressObserver(ReplayProgressObserver(mapboxReplayer))
//                mapboxReplayer.pushRealLocation(requireContext(), 0.0)
//                mapboxReplayer.play()
//            }
            mapboxNavigation
                .navigationOptions
                .locationEngine
                .getLastLocation(locationListenerCallback)

            directionRoute?.let {
                navigationMapboxMap?.drawRoute(it)
                mapboxNavigation.setRoutes(listOf(it))

            }

//            if (directionRoute == null) {
//                Snackbar.make(
//                    requireView(),
//                    R.string.msg_long_press_map_to_place_waypoint,
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    override fun onFeedbackSelected(feedbackItem: FeedbackItem?) {
        feedbackItem?.let { feedback ->
            this.feedbackItem = feedback
            sendFeedback()
        }
    }

    override fun onFeedbackDismissed() {
       //do nothing
    }

    override fun onWayNameChanged(wayName: String) {
        wayNameView.updateWayNameText(wayName)
        if (summaryBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            hideWayNameView()
        } else {
            showWayNameView()
        }
    }

    private val locationListenerCallback = MyLocationEngineCallback(this)
    private class MyLocationEngineCallback(fragment: MapboxNavigationFragment) :
        LocationEngineCallback<LocationEngineResult> {

        private val fragmentRef = WeakReference(fragment)

        override fun onSuccess(result: LocationEngineResult) {
            result.locations.firstOrNull()?.let { location ->
                Timber.d("location engine callback -> onSuccess location:%s", location)
                fragmentRef.get()?.locationComponent?.forceLocationUpdate(location)
            }
        }

        override fun onFailure(exception: Exception) {
            Timber.e("location engine callback -> onFailure(%s)", exception.localizedMessage)
        }
    }
    private val routesReqCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            Timber.d("route request success %s", routes.toString())
            if (routes.isNotEmpty()) {
                directionRoute = routes[0]
                navigationMapboxMap?.drawRoute(routes[0])

            }
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            Timber.e("route request failure %s", throwable.toString())
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            Timber.d("route request canceled")
        }
    }
    private fun buildRouteOverviewPadding(): IntArray {
        val leftRightPadding =
            resources
                .getDimension(
                    com.mapbox.navigation.ui.R.dimen.mapbox_route_overview_left_right_padding
                )
                .toInt()
        val paddingBuffer =
            resources
                .getDimension(
                    com.mapbox.navigation.ui.R.dimen.mapbox_route_overview_buffer_padding
                )
                .toInt()
        val instructionHeight = (
                resources
                    .getDimension(
                        com.mapbox.navigation.ui.R.dimen.mapbox_instruction_content_height
                    ) +
                        paddingBuffer
                )
            .toInt()
        val summaryHeight =
            resources
                .getDimension(com.mapbox.navigation.ui.R.dimen.mapbox_summary_bottom_sheet_height)
                .toInt()
        return intArrayOf(leftRightPadding, instructionHeight, leftRightPadding, summaryHeight)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()

        mapboxReplayer.finish()
        mapboxNavigation.unregisterTripSessionStateObserver(tripSessionStateObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterBannerInstructionsObserver(bannerInstructionObserver)
        mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)

        mapboxNavigation.stopTripSession()
        mapboxNavigation.onDestroy()

        speechPlayer.onDestroy()
    }
}