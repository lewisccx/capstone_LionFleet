package com.sit.capstone_cityfleet.appintro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.sit.capstone_lionfleet.R

class IntroVIewPagerAdapter(
    private val mContext: Context,
    private val mListScreen: List<IntroItem>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.intro_item_layout, null)

        val imgSlide =
            layoutScreen.findViewById<ImageView>(R.id.intro_img)
        val title = layoutScreen.findViewById<TextView>(R.id.intro_title)
        val description = layoutScreen.findViewById<TextView>(R.id.intro_description)

        title.setText(mListScreen!![position].title)
        description.setText(mListScreen!![position].description)
        imgSlide.setImageResource(mListScreen!![position].icon)

        container.addView(layoutScreen)

        return layoutScreen
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return mListScreen!!.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}