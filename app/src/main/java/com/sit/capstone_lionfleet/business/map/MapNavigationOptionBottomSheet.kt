package com.sit.capstone_lionfleet.business.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import com.sit.capstone_lionfleet.R
import kotlinx.android.synthetic.main.bottom_sheet_navigation_option.view.*
import org.json.JSONArray
import org.json.JSONObject

class MapNavigationOptionBottomSheet : BottomSheetDialogFragment() {
    val TAG = "MapNavigationOptionBottomSheet"
    private val REQUEST_CODE = 5678
    private val CURRENT_USER_LOCATION_KEY = "CURRENT_USER_LOCATION_KEY"
    private lateinit var currentUserLocationLatLng: LatLng
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUserLocationLatLng =
            requireArguments().getParcelable<LatLng>(CURRENT_USER_LOCATION_KEY)!!

        return inflater.inflate(R.layout.bottom_sheet_navigation_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initView(view)
    }

    private fun initView(view: View) {
        view.tvPlacePicker.setOnClickListener {
            goToPickerActivity()
        }
        view.tvPlaceSearch.setOnClickListener {
            Toast.makeText(requireContext(), "tvPlaceSearch", Toast.LENGTH_SHORT).show()
        }
        view.tvFreeDriving.setOnClickListener {
            Toast.makeText(requireContext(), "tvFreeDriving", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getTheme(): Int {
        return R.style.MaterialBottomSheetDialog
    }

    private fun goToPickerActivity() {
        startActivityForResult(
            PlacePicker.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(
                    PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(currentUserLocationLatLng).zoom(16.0).build()
                        )
                        .build()
                )
                .build(requireActivity()),
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            // Show the button and set the OnClickListener()
            findNavController().navigate(R.id.navigation_map)
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Retrieve the information from the selected location's CarmenFeature
            val carmenFeature = PlacePicker.getPlace(data)

            // Set the TextView text to the entire CarmenFeature. The CarmenFeature
            // also be parsed through to grab and display certain information such as
            // its placeName, text, or coordinates.
            if (carmenFeature != null) {
                Log.d(TAG, carmenFeature.toJson())
                val geometryObject = JSONObject(carmenFeature.toJson()).getString("geometry")
                val coordinateObject = JSONObject(geometryObject).getString("coordinates")
                val resultLatitude = JSONArray(coordinateObject)[0].toString().toDouble()
                val resultLongitude = JSONArray(coordinateObject)[1].toString().toDouble()
                val resultLocationLatLng = LatLng(resultLongitude, resultLatitude)
                val intent = Intent(requireActivity(), MapboxNavigationActivity::class.java)

                //navigation fragment
//                val bundle = Bundle()
//                bundle.putParcelable("DESTINATION_LATLNG_KEY_BUNDLE", resultLocationLatLng)
//                findNavController().navigate(R.id.mapboxNavigationFragment, bundle)

                //navigation activity
                intent.putExtra("DESTINATION_LATLNG_KEY_EXTRA",resultLocationLatLng)
                intent.putExtra("USER_LOCATION_LATLNG_KEY_EXTRA",currentUserLocationLatLng )
                startActivity(intent)
            }
        }
    }
}