package com.chichiangho.common.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.changhong.common.R
import kotlinx.android.synthetic.main.layout_title.view.*


class TitleLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.title_bar)
        val leftImgRes = typedArray.getResourceId(R.styleable.title_bar_left_img, 0)
        val leftTvText = typedArray.getString(R.styleable.title_bar_left_text)
        val rightImgRes = typedArray.getResourceId(R.styleable.title_bar_right_img, 0)
        val rightTvText = typedArray.getString(R.styleable.title_bar_right_text)
        val titleText = typedArray.getString(R.styleable.title_bar_title)
        val bgRes = typedArray.getResourceId(R.styleable.title_bar_titleBackground, 0)
        typedArray.recycle()

        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_title, null, false)

        if (leftImgRes != 0) {
            leftImg.visibility = View.VISIBLE
            leftImg.setBackgroundResource(leftImgRes)
        }
        if (leftTvText != null) {
            leftTv.visibility = View.VISIBLE
            leftTv.text = leftTvText
        }
        if (rightImgRes != 0) {
            rightImg.visibility = View.VISIBLE
            rightImg.setBackgroundResource(rightImgRes)
        }
        if (rightTvText != null) {
            rightTv.visibility = View.VISIBLE
            rightTv.text = rightTvText
        }
        if (titleText != null) {
            titleTv.text = titleText
        }

        if (bgRes != 0) {
            titleContent.setBackgroundColor(bgRes)
        }

        addView(rootView, 0)
    }
}