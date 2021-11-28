package com.akribase.timelineview

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import com.akribase.timelineview.databinding.EventViewBinding

internal class ExpandedEventDialog(
    context: Context,
    val width: Int,
    val event: EventI,
    @ColorInt val eventBg: Int
): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogBinding  = EventViewBinding.inflate(layoutInflater)
        setContentView(dialogBinding.root)
        dialogBinding.event = event
        dialogBinding.cardView.setCardBackgroundColor(eventBg)
        dialogBinding.constrained = false
    }

    override fun onStart() {
        super.onStart()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = (width * 0.8).toInt()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams
    }

}