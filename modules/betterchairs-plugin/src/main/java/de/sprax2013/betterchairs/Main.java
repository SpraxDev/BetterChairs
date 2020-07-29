package de.sprax2013.betterchairs;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * This class is run when the .jar is executed directly by the user and displays a message
 */
public class Main {
    public static void main(String[] args) {
        String version = "UNKNOWN", web = null;

        // Parse plugin.yml and extract some information
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Main.class.getResourceAsStream("/plugin.yml"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null && (version.equals("UNKNOWN") || web == null)) {
                if (line.startsWith("version:")) {
                    version = line.substring(8).trim();
                } else if (line.startsWith("website:")) {
                    web = line.substring(8).trim();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Prepare message to show
        StringBuilder sb = new StringBuilder("This is a Minecraft Spigot Plugin (SpigotMC.org).\n")
                .append("Just place it inside your server's plugin folder.\n\n")
                .append("Version: ")
                .append(version).append("\n");

        if (web != null) {
            sb.append("Website: ")
                    .append(web);
        }

        if (!GraphicsEnvironment.isHeadless()) {
            // Create whole JFrame to make sure the window appears in the system's taskbar
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);

            try {
                frame.setIconImage(new ImageIcon(Main.class.getResource("/icon.png")).getImage());
            } catch (NullPointerException ignore) {
                // Image not  found
            }

            JOptionPane.showMessageDialog(frame, sb.toString(), "BetterChairs v" + version, JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        } else {
            // Fallback to console when running headless
            System.out.println(sb.toString());
        }
    }
}