package com.chichiangho.common.base

import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.changhong.common.R
import com.chichiangho.common.extentions.parseColor
import kotlinx.android.synthetic.main.activity_tab.*
import java.util.*

abstract class BaseTabActivity : BaseTitleActivity() {

    private lateinit var mAdapter: MyAdapter
    private lateinit var mTab: LinearLayout

    private val mList = ArrayList<Holder>(4)
    private var mTabBgColor = R.color.white
    private var mTabHeight = R.dimen.tab_height
    private var mTextSize = R.dimen.size_middle
    private var mTextNormal = R.color.tab_text_normal
    private var mTextChecked = R.color.tab_text_checked
    private lateinit var cur: Holder
    private var needDivider: Boolean = false
    private var mNeedUnderHint: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        mAdapter = MyAdapter(supportFragmentManager)
        view_pager.offscreenPageLimit = 3
        view_pager.adapter = mAdapter
        view_pager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageSelected(arg0: Int) {
                onViewPagerSelected(arg0)
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }

    protected open fun onViewPagerSelected(arg0: Int) {
        cur.tv.setTextColor(parseColor(mTextNormal))
        cur.iv.setImageResource(cur.imgNormal)
        if (mNeedUnderHint)
            cur.under.visibility = View.GONE

        cur = mList[arg0]
        cur.tv.setTextColor(resources.getColor(mTextChecked))
        cur.iv.setImageResource(cur.imgSelected)
        if (mNeedUnderHint)
            cur.under.visibility = View.VISIBLE
    }

    fun prepare(@DimenRes tabHeight: Int, @DimenRes textSize: Int, @ColorRes textNormalColor: Int, @ColorRes textCheckedColor: Int, @ColorRes bgColor: Int, needUnderHint: Boolean): BaseTabActivity {
        mTabHeight = tabHeight
        mTextSize = textSize
        mTextNormal = textNormalColor
        mTextChecked = textCheckedColor
        mTabBgColor = bgColor
        mNeedUnderHint = needUnderHint
        return this
    }

    @JvmOverloads
    fun addTab(fragment: Fragment, tabTextRes: Int, imgNormal: Int = 0, imgSelected: Int = 0): BaseTabActivity {
        val holder = Holder(LayoutInflater.from(this).inflate(R.layout.layout_tab_item, null), fragment, imgNormal, imgSelected)
        mList.add(holder)
        holder.tv.setText(tabTextRes)
        holder.view.setOnClickListener { v ->
            mList.indices.filter { v === mList[it].view }.forEach { view_pager.currentItem = it }
        }
        return this
    }

    fun setNeedDivider(needDivider: Boolean): BaseTabActivity {
        this.needDivider = needDivider
        return this
    }

    fun commit() {
        if (mList[0].iv.visibility == View.GONE) {//if no img,which means tab at top
            mTab = top_layout
            top_divider.visibility = View.VISIBLE
        } else {
            mTab = bottom_layout
            bottom_divider.visibility = View.VISIBLE
        }
        mTab.setBackgroundColor(parseColor(mTabBgColor))
        if (mList.size > 1)
            mTab.visibility = View.VISIBLE

        for (i in mList.indices) {
            val tabItem = mList[i].view

            val radioParams = RelativeLayout.LayoutParams(Device.screenWidth / mList.size, resources.getDimension(mTabHeight).toInt())
            tabItem.layoutParams = radioParams

            mTab.addView(tabItem)

            if (needDivider) {
                val divider = ImageView(baseContext)
                val params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.divider_width).toInt(), (resources.getDimension(mTabHeight) * 2 / 3).toInt())
                params.gravity = Gravity.CENTER
                divider.layoutParams = params
                divider.setBackgroundResource(R.color.divider_tab)
                mTab.addView(divider)
            }
            if (mNeedUnderHint) {
                val params = RelativeLayout.LayoutParams(Device.screenWidth / mList.size / 2, resources.getDimension(R.dimen.tab_divider_height).toInt())
                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                mList[i].under.layoutParams = params
                mList[i].under.setBackgroundResource(mTextChecked)
            } else {
                mList[i].under.visibility = View.GONE
            }
        }

        mAdapter.notifyDataSetChanged()
        cur = mList[0]
        onViewPagerSelected(0)
    }

    private inner class MyAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(arg0: Int): Fragment = mList[arg0].fragment

        override fun getCount(): Int = mList.size
    }

    private inner class Holder internal constructor(val view: View, val fragment: Fragment, val imgNormal: Int, val imgSelected: Int) {
        val tv: TextView = view.findViewById(R.id.text)
        val iv: ImageView = view.findViewById(R.id.image)
        var under: View = view.findViewById(R.id.under_hint)

        init {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(mTextSize).toFloat())

            if (imgNormal == 0 || imgSelected == 0)
                iv.visibility = View.GONE
            else if (imgNormal != 0)
                iv.setImageResource(imgNormal)
        }
    }
}
