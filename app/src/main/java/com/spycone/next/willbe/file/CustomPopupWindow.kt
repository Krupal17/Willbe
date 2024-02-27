package com.spycone.next.willbe.file

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.spycone.next.willbe.R

class CustomPopupWindow(private val context: Context) {

    private val popupWindow: PopupWindow = PopupWindow(context)

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.custom_popup_layout, null)

        popupWindow.contentView = contentView
        popupWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }

    fun show(anchorView: View) {
        val contentView = popupWindow.contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = contentView.measuredWidth

        // Set the height of the popup window to match the content
        val popupHeight = contentView.measuredHeight
        popupWindow.height = popupHeight

        // Calculate the x and y offsets to position the popup below the anchor view
        val offsetX = -(popupWidth - anchorView.width) / 2
        val offsetY = 0
        popupWindow.isClippingEnabled = false
        popupWindow.showAsDropDown(anchorView, offsetX, offsetY)
    }


    fun dismiss() {
        popupWindow.dismiss()
    }
}
