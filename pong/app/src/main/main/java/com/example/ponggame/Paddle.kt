package com.example.ponggame

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/**
 * Represents a paddle in the Pong game
 * Can be controlled by player or AI
 */
class Paddle(
    var x: Float,
    var y: Float,
    val width: Int,
    val height: Int,
    private val screenHeight: Int
) {
    
    // Rectangle representing the paddle's bounds
    private val rect = RectF()
    
    init {
        updateRect()
    }
    
    /**
     * Update the rectangle bounds based on current position
     */
    private fun updateRect() {
        rect.set(x, y, x + width, y + height)
    }
    
    /**
     * Move paddle up by specified speed
     */
    fun moveUp(speed: Float) {
        y -= speed
        
        // Prevent paddle from going off top of screen
        if (y < 0) {
            y = 0f
        }
        
        updateRect()
    }
    
    /**
     * Move paddle down by specified speed
     */
    fun moveDown(speed: Float) {
        y += speed
        
        // Prevent paddle from going off bottom of screen
        if (y + height > screenHeight) {
            y = (screenHeight - height).toFloat()
        }
        
        updateRect()
    }
    
    /**
     * Move paddle to specific Y position (for touch control)
     * Centers the paddle on the touch point
     */
    fun moveTo(targetY: Float) {
        y = targetY - height / 2
        
        // Keep paddle within screen bounds
        if (y < 0) {
            y = 0f
        }
        if (y + height > screenHeight) {
            y = (screenHeight - height).toFloat()
        }
        
        updateRect()
    }
    
    /**
     * Get the paddle's bounding rectangle
     */
    fun getRect(): RectF {
        return rect
    }
    
    /**
     * Draw the paddle on the canvas
     */
    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawRect(rect, paint)
    }
}