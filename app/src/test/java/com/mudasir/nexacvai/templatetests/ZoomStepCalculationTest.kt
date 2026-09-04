package com.mudasir.nexacvai.templatetests

import com.mudasir.nexacvai.presentation.ui.components.MAX_ZOOM_LEVEL
import com.mudasir.nexacvai.presentation.ui.components.MIN_ZOOM_LEVEL
import com.mudasir.nexacvai.presentation.ui.components.calculateNextZoomIn
import com.mudasir.nexacvai.presentation.ui.components.calculateNextZoomOut
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomStepCalculationTest {

    @Test
    fun `rapid zoom in from 100 percent steps cleanly to 500 percent`() {
        var zoom = 1.0f
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(2.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(3.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(4.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(5.0f, zoom, 0.001f)
        
        // Clamped at MAX_ZOOM_LEVEL
        zoom = calculateNextZoomIn(zoom)
        assertEquals(MAX_ZOOM_LEVEL, zoom, 0.001f)
    }

    @Test
    fun `rapid zoom out from 500 percent steps cleanly to 50 percent`() {
        var zoom = 5.0f
        
        zoom = calculateNextZoomOut(zoom)
        assertEquals(4.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomOut(zoom)
        assertEquals(3.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomOut(zoom)
        assertEquals(2.0f, zoom, 0.001f)
        
        zoom = calculateNextZoomOut(zoom)
        assertEquals(1.0f, zoom, 0.001f)
        
        // Drops from 100% to 50%
        zoom = calculateNextZoomOut(zoom)
        assertEquals(MIN_ZOOM_LEVEL, zoom, 0.001f)
        
        // Clamped at MIN_ZOOM_LEVEL
        zoom = calculateNextZoomOut(zoom)
        assertEquals(MIN_ZOOM_LEVEL, zoom, 0.001f)
    }

    @Test
    fun `zoom in from minimum 50 percent snaps to 100 percent`() {
        val zoom = calculateNextZoomIn(0.5f)
        assertEquals(1.0f, zoom, 0.001f)
    }

    @Test
    fun `landscape zoom stepping starting at 150 percent`() {
        var zoom = 1.5f
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(2.5f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(3.5f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(4.5f, zoom, 0.001f)
        
        zoom = calculateNextZoomIn(zoom)
        assertEquals(5.0f, zoom, 0.001f)
    }

    @Test
    fun `landscape zoom out stepping from 150 percent snaps cleanly to 100 percent`() {
        val zoom = calculateNextZoomOut(1.5f)
        assertEquals(1.0f, zoom, 0.001f)
        
        val minZoom = calculateNextZoomOut(zoom)
        assertEquals(0.5f, minZoom, 0.001f)
    }

    @Test
    fun `zoom in and out after arbitrary pinch snaps to clean half or whole steps`() {
        // User pinches to 1.83f and presses zoom in
        val steppedIn = calculateNextZoomIn(1.83f)
        assertEquals(3.0f, steppedIn, 0.001f)
        
        // User pinches to 1.83f and presses zoom out
        val steppedOut = calculateNextZoomOut(1.83f)
        assertEquals(1.0f, steppedOut, 0.001f)
        
        // User pinches to 2.15f and presses zoom in
        val steppedInFrom215 = calculateNextZoomIn(2.15f)
        assertEquals(3.0f, steppedInFrom215, 0.001f)
    }
}
