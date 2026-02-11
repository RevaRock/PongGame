package com.example.ponggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Custom SurfaceView that handles all game rendering, logic, and user input
 * This is the main game screen where all game elements are drawn
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    
    // Game thread for running the game loop
    private var gameThread: GameThread
    
    // Game objects
    private lateinit var playerPaddle: Paddle
    private lateinit var aiPaddle: Paddle
    private lateinit var ball: Ball
    
    // Paint objects for drawing
    private val paint = Paint()
    private val textPaint = Paint()
    
    // Score tracking
    private var playerScore = 0
    private var aiScore = 0
    private val winningScore = 5
    
    // Game state
    private var gameOver = false
    private var screenWidth = 0
    private var screenHeight = 0
    
    init {
        // Add callback to surface holder
        holder.addCallback(this)
        
        // Initialize paint for game objects
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        
        // Initialize paint for text (score and game over)
        textPaint.color = Color.WHITE
        textPaint.textSize = 80f
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        
        // Create and start game thread
        gameThread = GameThread(holder, this)
        
        // Make the view focusable so it can handle events
        isFocusable = true
    }
    
    /**
     * Called when the surface is created
     * Initialize game objects here since we now know the screen dimensions
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        // Get screen dimensions using display metrics (more reliable)
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        
        // Initialize game objects with screen dimensions
        initializeGame()
        
        // Start the game thread
        gameThread.setRunning(true)
        gameThread.start()
    }
    
    /**
     * Initialize or reset all game objects
     */
    private fun initializeGame() {
        val paddleWidth = 40
        val paddleHeight = 300
        val paddleMargin = 100
        
        // Create player paddle on the left side
        playerPaddle = Paddle(
            x = paddleMargin.toFloat(),
            y = (screenHeight / 2 - paddleHeight / 2).toFloat(),
            width = paddleWidth,
            height = paddleHeight,
            screenHeight = screenHeight
        )
        
        // Create AI paddle on the right side
        aiPaddle = Paddle(
            x = (screenWidth - paddleMargin - paddleWidth).toFloat(),
            y = (screenHeight / 2 - paddleHeight / 2).toFloat(),
            width = paddleWidth,
            height = paddleHeight,
            screenHeight = screenHeight
        )
        
        // Create ball in the center of the screen
        ball = Ball(
            x = (screenWidth / 2).toFloat(),
            y = (screenHeight / 2).toFloat(),
            radius = 20,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        )
        
        // Reset scores if starting new game
        if (gameOver) {
            playerScore = 0
            aiScore = 0
            gameOver = false
        }
    }
    
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Stop the game thread when surface is destroyed
        var retry = true
        while (retry) {
            try {
                gameThread.setRunning(false)
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Handle touch events for player paddle control
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (!gameOver) {
                    // Move player paddle to touch Y position
                    playerPaddle.moveTo(event.y)
                } else {
                    // Check if user tapped restart area
                    val restartY = screenHeight / 2 + 150f
                    if (event.y > restartY - 50 && event.y < restartY + 50) {
                        initializeGame()
                    }
                }
            }
        }
        return true
    }
    
    /**
     * Update game state
     * Called every frame by the game thread
     */
    fun update() {
        if (!gameOver) {
            // Update ball position
            ball.update()
            
            // AI paddle follows the ball with slight delay
            val aiTarget = ball.y
            val aiSpeed = 8f
            if (aiPaddle.y + aiPaddle.height / 2 < aiTarget) {
                aiPaddle.moveDown(aiSpeed)
            } else if (aiPaddle.y + aiPaddle.height / 2 > aiTarget) {
                aiPaddle.moveUp(aiSpeed)
            }
            
            // Check collision with player paddle
            if (ball.isCollidingWith(playerPaddle)) {
                ball.bounceOffPaddle(playerPaddle)
            }
            
            // Check collision with AI paddle
            if (ball.isCollidingWith(aiPaddle)) {
                ball.bounceOffPaddle(aiPaddle)
            }
            
            // Check if ball went off screen (someone scored)
            if (ball.x - ball.radius < 0) {
                // AI scored
                aiScore++
                checkGameOver()
                resetBall()
            } else if (ball.x + ball.radius > screenWidth) {
                // Player scored
                playerScore++
                checkGameOver()
                resetBall()
            }
        }
    }
    
    /**
     * Reset ball to center after a score
     */
    private fun resetBall() {
        ball.reset((screenWidth / 2).toFloat(), (screenHeight / 2).toFloat())
    }
    
    /**
     * Check if game is over (someone reached winning score)
     */
    private fun checkGameOver() {
        if (playerScore >= winningScore || aiScore >= winningScore) {
            gameOver = true
        }
    }
    
    /**
     * Draw all game elements on the canvas
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        
        // Clear screen with black background
        canvas.drawColor(Color.BLACK)
        
        if (!gameOver) {
            // Draw center line (dashed)
            drawCenterLine(canvas)
            
            // Draw paddles
            playerPaddle.draw(canvas, paint)
            aiPaddle.draw(canvas, paint)
            
            // Draw ball
            ball.draw(canvas, paint)
            
            // Draw scores
            textPaint.textSize = 80f
            canvas.drawText(playerScore.toString(), screenWidth / 4f, 100f, textPaint)
            canvas.drawText(aiScore.toString(), screenWidth * 3 / 4f, 100f, textPaint)
            
        } else {
            // Draw game over screen
            textPaint.textSize = 100f
            val winner = if (playerScore >= winningScore) "PLAYER WINS!" else "AI WINS!"
            canvas.drawText(winner, screenWidth / 2f, screenHeight / 2f - 100, textPaint)
            
            textPaint.textSize = 60f
            canvas.drawText("$playerScore - $aiScore", screenWidth / 2f, screenHeight / 2f, textPaint)
            
            textPaint.textSize = 50f
            canvas.drawText("TAP TO RESTART", screenWidth / 2f, screenHeight / 2f + 150, textPaint)
        }
    }
    
    /**
     * Draw dashed center line
     */
    private fun drawCenterLine(canvas: Canvas) {
        val dashHeight = 20f
        val dashGap = 15f
        var y = 0f
        
        while (y < screenHeight) {
            canvas.drawRect(
                screenWidth / 2f - 5,
                y,
                screenWidth / 2f + 5,
                y + dashHeight,
                paint
            )
            y += dashHeight + dashGap
        }
    }
    
    /**
     * Pause the game
     */
    fun pause() {
        gameThread.setRunning(false)
    }
    
    /**
     * Resume the game
     * Creates a new thread if the previous one is not alive
     */
    fun resume() {
        if (!gameThread.isAlive) {
            gameThread = GameThread(holder, this)
            gameThread.setRunning(true)
            gameThread.start()
        }
    }
}