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
	public boolean upPressed, downPressed, leftPressed, rightPressed, debugMode = false;

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

		if ( code == KeyEvent.VK_W ) upPressed = true;
		if ( code == KeyEvent.VK_S ) downPressed = true;
		if ( code == KeyEvent.VK_A ) leftPressed = true;
		if ( code == KeyEvent.VK_D ) rightPressed = true;

		// Debug
		if ( code == KeyEvent.VK_T )
			debugMode = !debugMode;
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
