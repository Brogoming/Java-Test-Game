package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input for the game by implementing {@link KeyListener}.
 * <p>
 * Tracks the pressed/released state of the W, A, S, D movement keys.
 * An instance of this class is registered to {@link GamePanel} so it can
 * receive keyboard events, and passed to {@link entity.Player} so movement
 * logic can read the current input state each frame.
 * </p>
 */
public class KeyHandler implements KeyListener {


	// -------------------------------------------------------------------------
	// Input State Flags
	// -------------------------------------------------------------------------
	public boolean upPressed, downPressed, leftPressed, rightPressed, interactPressed, debugMode = false;
	GamePanel gamePanel;

	public KeyHandler( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
	}

	// -------------------------------------------------------------------------
	// KeyListener Implementation
	// -------------------------------------------------------------------------

	@Override
	public void keyTyped( KeyEvent e ) {} //Don't need this

	/**
	 * Fires when a key is pressed down and sets the corresponding movement flag to {@code true}.
	 * <p>
	 * Uses {@link KeyEvent#getKeyCode()} to identify which key was pressed
	 * based on its virtual key code (not character value), ensuring input works
	 * regardless of keyboard locale or case.
	 * </p>
	 *
	 * @param e the {@link KeyEvent} containing the key code of the pressed key
	 */
	@Override
	public void keyPressed( KeyEvent e ) {
		int code = e.getKeyCode(); //returns the keyCode associated with the key in this event, based on ascii characters

		// Title State
		if ( gamePanel.gameState == GameState.Title ) titleState(code);
			// Play State
		else if ( gamePanel.gameState == GameState.Play ) playState(code);
			// Paused State
		else if ( gamePanel.gameState == GameState.Pause ) pauseState(code);
			// Dialogue State
		else if ( gamePanel.gameState == GameState.Dialogue ) dialogState(code);
			// Character State
		else if ( gamePanel.gameState == GameState.CharStats ) charState(code);
	}

	/**
	 * Handles user actions on the title screen
	 *
	 * @param code the current keyboard button that was pressed
	 */
	private void titleState( int code ) {
		if ( gamePanel.ui.titleScreenState == 0 ) {
			if ( code == KeyEvent.VK_W ) {
				gamePanel.ui.commandNumber--;
				if ( gamePanel.ui.commandNumber < 0 ) { // Circle back to quit option
					gamePanel.ui.commandNumber = 2;
				}
			}
			if ( code == KeyEvent.VK_S ) {
				gamePanel.ui.commandNumber++;
				if ( gamePanel.ui.commandNumber > 2 ) { // Circle back to new game option
					gamePanel.ui.commandNumber = 0;
				}
			}
			if ( code == KeyEvent.VK_ENTER ) {
				if ( gamePanel.ui.commandNumber == 0 ) { // New Game
					gamePanel.ui.titleScreenState = 1;
				} else if ( gamePanel.ui.commandNumber == 1 ) { // Load Game

				} else if ( gamePanel.ui.commandNumber == 2 ) System.exit(0); // Quit
			}
		} else if ( gamePanel.ui.titleScreenState == 1 ) { // Character select menu
			if ( code == KeyEvent.VK_W ) {
				gamePanel.ui.commandNumber--;
				if ( gamePanel.ui.commandNumber < 0 ) { // Circle back to quit option
					gamePanel.ui.commandNumber = 3;
				}
			}
			if ( code == KeyEvent.VK_S ) {
				gamePanel.ui.commandNumber++;
				if ( gamePanel.ui.commandNumber > 3 ) { // Circle back to new game option
					gamePanel.ui.commandNumber = 0;
				}
			}
			if ( code == KeyEvent.VK_ENTER ) {
				// In each of these we can put specific player stats
				if ( gamePanel.ui.commandNumber == 0 ) { // Fighter Class
					gamePanel.gameState = GameState.Play;
//						gamePanel.playMusic();
				} else if ( gamePanel.ui.commandNumber == 1 ) { // Wizard Class
					gamePanel.gameState = GameState.Play;
//						gamePanel.playMusic();
				} else if ( gamePanel.ui.commandNumber == 2 ) { // Ranger Class
					gamePanel.gameState = GameState.Play;
//						gamePanel.playMusic();
				} else if ( gamePanel.ui.commandNumber == 3 ) gamePanel.ui.titleScreenState = 0; // Back
			}
		}
	}

	/**
	 * Handles user actions when playing the game
	 *
	 * @param code the current keyboard button that was pressed
	 */
	private void playState( int code ) {
		// Movement
		if ( code == KeyEvent.VK_W ) upPressed = true;
		if ( code == KeyEvent.VK_S ) downPressed = true;
		if ( code == KeyEvent.VK_A ) leftPressed = true;
		if ( code == KeyEvent.VK_D ) rightPressed = true;

		// Character stats
		if ( code == KeyEvent.VK_C ) gamePanel.gameState = GameState.CharStats;

		// Interaction
		if ( code == KeyEvent.VK_E ) interactPressed = true;

		// Debug
		if ( code == KeyEvent.VK_T ) debugMode = !debugMode;

		// Pause
		if ( code == KeyEvent.VK_ESCAPE ) {
			gamePanel.gameState = GameState.Pause;
			gamePanel.stopMusic();
		}
	}

	/**
	 * Handles the user actions when the game is paused
	 *
	 * @param code the current keyboard button that was pressed
	 */
	private void pauseState( int code ) {
		if ( code == KeyEvent.VK_ESCAPE ) {
			gamePanel.gameState = GameState.Play;
//				gamePanel.playMusic();
		}
	}

	/**
	 * Handles the user actions when in dialogue
	 *
	 * @param code the current keyboard button that was pressed
	 */
	private void dialogState( int code ) {
		if ( code == KeyEvent.VK_E ) gamePanel.gameState = GameState.Play;
	}

	/**
	 * Handles the user actions when the player is checking their stats
	 *
	 * @param code the current keyboard button that was pressed
	 */
	private void charState( int code ) {
		if ( code == KeyEvent.VK_C ) gamePanel.gameState = GameState.Play;
	}

	/**
	 * Fires when a key is released and resets the corresponding movement flag to {@code false}.
	 * <p>
	 * Releasing a key stops the associated movement by clearing its flag,
	 * preventing the player from continuing to move after the key is let go.
	 * </p>
	 *
	 * @param e the {@link KeyEvent} containing the key code of the released key
	 */
	@Override
	public void keyReleased( KeyEvent e ) {
		int code = e.getKeyCode(); //returns the keyCode associated with the key in this event, based on ascii characters

		if ( code == KeyEvent.VK_W ) upPressed = false;
		if ( code == KeyEvent.VK_S ) downPressed = false;
		if ( code == KeyEvent.VK_A ) leftPressed = false;
		if ( code == KeyEvent.VK_D ) rightPressed = false;
	}
}
