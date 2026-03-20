package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * Manages loading and playback of game audio clips including music and sound effects.
 */
public class Sound {

	Clip clip;                         // The currently loaded audio clip ready for playback
	URL[] soundURL = new URL[30];      // Stores resource paths for up to 30 sound files, indexed by ID

	/**
	 * Constructs the Sound system and registers all game audio file paths into the URL array.
	 * <p>
	 * TODO: Consider using a Map instead of an array to reference sounds by name rather than index.
	 */
	public Sound() {
		soundURL[0] = getClass().getResource("/sounds/BlueBoyAdventure.wav"); // Theme song
		soundURL[1] = getClass().getResource("/sounds/coin.wav");             // Pick up coin
		soundURL[2] = getClass().getResource("/sounds/powerup.wav");          // Pick up power-up
		soundURL[3] = getClass().getResource("/sounds/unlock.wav");           // Unlock door
		soundURL[4] = getClass().getResource("/sounds/fanfare.wav");          // Game finished fanfare
	}

	/**
	 * Loads the audio file at the given index into the active clip, ready for playback.
	 *
	 * @param index the index of the sound in {@code soundURL} to load
	 */
	public void setFile( int index ) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[index]);
			clip = AudioSystem.getClip();
			clip.open(ais); // Open the clip with the loaded audio stream
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the currently loaded clip once from its current position.
	 */
	public void play() {
		clip.start();
	}

	/**
	 * Loops the currently loaded clip continuously until stopped.
	 */
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	/**
	 * Stops playback of the currently loaded clip.
	 */
	public void stop() {
		clip.stop();
	}
}