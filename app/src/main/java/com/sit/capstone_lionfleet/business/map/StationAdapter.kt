package com.sit.capstone_lionfleet.business.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.sit.capstone_lionfleet.R
import com.sit.capstone_lionfleet.business.map.network.model.Vehicle
import com.sit.capstone_lionfleet.core.di.ImageService
import com.sit.capstone_lionfleet.core.di.PreferenceProvider

class StationAdapter constructor(
    private val data: List<Vehicle>,
    private val imageService: ImageService,
    private val clickListener: (Vehicle) -> Unit,

) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.view_vehicle_list_item, container, false)

        val vehicle = data[position]
        view.setOnClickListener {
            clickListener.invoke(vehicle)
        }
        val vehicleImage = view.findViewById<ImageView>(R.id.vehicleImage)
        val txtEngineType = view.findViewById<TextView>(R.id.textEngineType)
        val imgEngineType = view.findViewById<ImageView>(R.id.iconEngineType)
        val txtLicense = view.findViewById<TextView>(R.id.vehicleLicense)
        val txtModelBrand = view.findViewById<TextView>(R.id.vehicleModelBrand)
        val txtPricePerKm = view.findViewById<TextView>(R.id.textPricePerKm)
        val txtPricePerDay = view.findViewById<TextView>(R.id.textPricePerDay)
        val isAvailableFlag = view.findViewById<ImageView>(R.id.isAvailableFlag)
        val availableMsg = view.findViewById<TextView>(R.id.availableMsg)
        imageService.loadImage(vehicle.imageUrl, vehicleImage)
        if (vehicle.isDiesel) {
            txtEngineType.text = container.context.resources.getString(R.string.engineDisel)
            imgEngineType.setImageDrawable(container.context.getDrawable(R.drawable.ic_gasoline))
        }
        if(!vehicle.availability){
            isAvailableFlag.setImageDrawable(container.context.getDrawable(R.drawable.ic_wrong))
            availableMsg.text = "Currently Rented"
        }
        txtLicense.text = vehicle.plate
        txtModelBrand.text = "${vehicle.brand} ${vehicle.model}"
        txtPricePerKm.text = "${vehicle.costsPerHour} $/km"
        txtPricePerDay.text = "${vehicle.costsPerDay} $/day"

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
    override fun destroyItem(container: ViewGroup, position: Int, view : Any) = container.removeView( view as View)

}