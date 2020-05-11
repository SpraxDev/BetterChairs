/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import javax.swing.*;

/**
 * Created by BlackScarx on 11/05/2016.
 * BlackScarx all right reserved
 */
public class Panel extends JPanel {

    public Panel() {
    }

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(new Panel(), "This is a plugin.\n" +
                "Just place it in the plugins folder of your server\n" +
                "BetterChairs Version: 0.10.1", "BetterChairs", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

}
