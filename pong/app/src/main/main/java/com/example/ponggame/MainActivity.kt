package com.example.ponggame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Main Activity that hosts the Pong game
 * Sets the content view to the GameView which handles all game logic and rendering
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var gameView: GameView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the custom game view
        gameView = GameView(this)
        
        // Set the game view as the content view
        setContentView(gameView)
    }
    
    /**
     * Pause the game when activity is paused
     */
    override fun onPause() {
        super.onPause()
        gameView.pause()
    }
    
    /**
     * Resume the game when activity is resumed
     */
    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
}