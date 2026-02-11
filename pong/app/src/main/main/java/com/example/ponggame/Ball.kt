package com.example.ponggame

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.abs
import kotlin.random.Random

/**
 * Represents the ball in the Pong game
 * Handles movement, collisions, and bouncing
 */
class Ball(
    var x: Float,
    var y: Float,
    val radius: Int,
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    
    // Ball velocity
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    
    // Base speed of the ball
    private val baseSpeed = 15f
    
    init {
        // Set initial random direction
        resetVelocity()
    }
    
    /**
     * Reset ball velocity with random direction
     */
    private fun resetVelocity() {
        // Random direction: left or right
        val direction = if (Random.nextBoolean()) 1 else -1
        velocityX = baseSpeed * direction
        
        // Random vertical velocity
        velocityY = Random.nextFloat() * 10 - 5 // Range: -5 to 5
    }
    
    /**
     * Update ball position based on velocity
     */
    fun update() {
        x += velocityX
        y += velocityY
        
        // Bounce off top and bottom walls
        if (y - radius < 0) {
            y = radius.toFloat()
            velocityY = -velocityY
        } else if (y + radius > screenHeight) {
            y = (screenHeight - radius).toFloat()
            velocityY = -velocityY
        }
    }
    
    /**
     * Check if ball is colliding with a paddle
     */
    fun isCollidingWith(paddle: Paddle): Boolean {
        val paddleRect = paddle.getRect()
        
        // Check if ball overlaps with paddle rectangle
        return x + radius > paddleRect.left &&
               x - radius < paddleRect.right &&
               y + radius > paddleRect.top &&
               y - radius < paddleRect.bottom
    }
    
    /**
     * Handle ball bouncing off a paddle
     * Adds spin based on where the ball hits the paddle
     */
    fun bounceOffPaddle(paddle: Paddle) {
        // Reverse horizontal direction
        velocityX = -velocityX
        
        // Add spin based on where ball hit the paddle
        // Hit near top = ball goes up, hit near bottom = ball goes down
        val paddleCenter = paddle.y + paddle.height / 2
        val hitOffset = y - paddleCenter
        val normalizedOffset = hitOffset / (paddle.height / 2) // Range: -1 to 1
        
        // Adjust vertical velocity based on hit position
        velocityY += normalizedOffset * 5
        
        // Increase speed slightly with each hit (up to a maximum)
        val speedIncrease = 1.1f
        val maxSpeed = 25f
        
        if (abs(velocityX) < maxSpeed) {
            velocityX *= speedIncrease
        }
        
        // Move ball away from paddle to prevent multiple collisions
        if (velocityX > 0) {
            x = paddle.x + paddle.width + radius
        } else {
            x = paddle.x - radius
        }
    }
    
    /**
     * Reset ball to center position with new random velocity
     */
    fun reset(centerX: Float, centerY: Float) {
        x = centerX
        y = centerY
        resetVelocity()
    }
    
    /**
     * Draw the ball on the canvas
     */
    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(x, y, radius.toFloat(), paint)
    }
}