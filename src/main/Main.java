package main;

import javax.swing.*;

/**
 * Entry point for the 2D Java Game application.
 * This class is responsible for initializing the main application window
 * ({@link JFrame}) and launching the game loop via {@link GamePanel}.
 */
public class Main {
    /**
     * The main method that serves as the entry point of the application.
     * Sets up the {@link JFrame} window with the appropriate settings,
     * attaches the {@link GamePanel} to it, and starts the game thread.
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        JFrame window = new JFrame(); // Create the main application window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure the application exits cleanly when the window is closed
        window.setResizable(false); // Prevent the user from resizing the window to avoid layout/scaling issues
        window.setTitle("2D Java Game");

        GamePanel gamePanel = new GamePanel();  // Create the game panel (a JPanel subclass that handles rendering and input)
        window.add(gamePanel);  // Attach the game panel to the window so it is rendered inside the JFrame

        window.pack(); //causes the window to be sized to fit the preferred size and layouts of its subcomponents

        window.setLocationRelativeTo(null); // Center the window on the screen (null means relative to the screen itself)
        window.setVisible(true); // Make the window visible to the user

        gamePanel.setupGame();
        gamePanel.startGameThread(); // Start the game loop on a separate thread to handle updates and rendering
    }
}