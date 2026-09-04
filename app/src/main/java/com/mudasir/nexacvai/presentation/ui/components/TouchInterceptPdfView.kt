package com.mudasir.nexacvai.presentation.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import com.github.barteksc.pdfviewer.PDFView

@SuppressLint("ViewConstructor")
internal class TouchInterceptPdfView(
    context: Context,
    var initialZoom: Float
) : PDFView(context, null) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (zoom > (initialZoom + 0.05f)) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // Multi-touch detected (2 or more fingers -> pinch to zoom).
                // Block parent pagers from intercepting this gesture!
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (ev.pointerCount >= 2 || zoom > (initialZoom + 0.05f)) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
