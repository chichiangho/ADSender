package com.chichiangho.common.base

import android.graphics.Color
import android.os.Build
import android.support.annotation.LayoutRes
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_title.*

abstract class BaseTitleActivity : BaseActivity() {

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)


        //        View.OnClickListener leftClickListener = new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                finish();
        //            }
        //        };
        //
        //        leftTv.setOnClickListener(leftClickListener);
        //        leftImg.setOnClickListener(leftClickListener);

        //设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
                val window = window
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT//calculateStatusColor(Color.WHITE, (int) alphaValue)
            }
            val rootParams = titleRoot.layoutParams
            rootParams.height = titleRoot.layoutParams.height + statusHeight
            titleRoot.layoutParams = rootParams

            val params = titleContent.layoutParams as LinearLayout.LayoutParams;
            params.setMargins(0, statusBarHeight, 0, 0)
            titleContent.layoutParams = params
        }
    }

    private var statusHeight: Int = 0

    private val statusBarHeight: Int
        get() {
            if (statusHeight == 0) {
                try {
                    val c = Class.forName("com.android.internal.R\$dimen")
                    val obj = c.newInstance()
                    val field = c.getField("status_bar_height")
                    val x = Integer.parseInt(field.get(obj).toString())
                    statusHeight = resources.getDimensionPixelSize(x)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
            return statusHeight
        }

    protected fun setTitleLeftClickListener(leftClickListener: View.OnClickListener) {
        leftTv.setOnClickListener(leftClickListener)
        leftImg.setOnClickListener(leftClickListener)
    }

    protected fun setTitleRightClickListener(rightClickListener: View.OnClickListener) {
        rightTv.setOnClickListener(rightClickListener)
        rightImg.setOnClickListener(rightClickListener)
    }

    protected fun hideTitle() {
        titleRoot.visibility = View.GONE
    }

    protected fun setTitleLeftTxt(leftTxt: String) {
        leftTv.visibility = View.VISIBLE
        leftTv.text = leftTxt
    }

    protected fun setTitleRightTxt(rightTxt: String) {
        rightTv.visibility = View.VISIBLE
        rightTv.text = rightTxt
    }

    protected fun setTitleRightImgRes(rightImgRes: Int) {
        rightImg.visibility = View.VISIBLE
        rightImg.setImageResource(rightImgRes)
    }

    protected fun setTitleBackground(bgRes: Int) {
        titleRoot.setBackgroundResource(bgRes)
    }

    protected fun setTitle(titleTxt: String) {
        titleTv.text = titleTxt
    }
}
