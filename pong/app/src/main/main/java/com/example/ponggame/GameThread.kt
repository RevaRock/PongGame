package com.example.ponggame

import android.graphics.Canvas
import android.view.SurfaceHolder

/**
 * Game thread that runs the game loop
 * Handles updating game state and rendering at consistent frame rate
 */
class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {
    
    // Flag to control thread running state
    private var running = false
    
    // Target frames per second
    private val targetFPS = 60
    private val targetTime = (1000 / targetFPS).toLong()
    
    /**
     * Set whether the thread should be running
     */
    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }
    
    /**
     * Main game loop
     * Updates game state and renders at consistent frame rate
     */
    override fun run() {
        var canvas: Canvas?
        
        while (running) {
            // Record start time for frame timing
            val startTime = System.currentTimeMillis()
            
            canvas = null
            
            try {
                // Lock the canvas for drawing
                canvas = surfaceHolder.lockCanvas()
                
                synchronized(surfaceHolder) {
                    // Update game state
                    gameView.update()
                    
                    // Draw everything
                    canvas?.let {
                        gameView.draw(it)
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                
            } finally {
                // Always unlock and post the canvas
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            // Calculate how long the frame took
            val timeElapsed = System.currentTimeMillis() - startTime
            
            // Sleep to maintain consistent frame rate
            val waitTime = targetTime - timeElapsed
            if (waitTime > 0) {
                try {
                    sleep(waitTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}