package org.usul.plaiground.frontend;

import javax.swing.*;
import java.awt.*;

public class MainScreen extends JPanel {
    public MainScreen() {
        super(new BorderLayout());

        JLabel startText = new JLabel("I make friends. They're toys. My friends are toys. I make them. It's a hobby.");
        startText.setFont(new Font("Arial", Font.BOLD, 20));
        startText.setHorizontalAlignment(SwingConstants.CENTER);
        startText.setVerticalAlignment(SwingConstants.CENTER);

        startText.setForeground(Color.MAGENTA);

        this.setBackground(Color.BLACK);

        this.add(startText, BorderLayout.CENTER);
    }
}
