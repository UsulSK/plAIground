package org.usul.plaiground.frontend;

import org.usul.plaiground.backend.app.PlaigroundApp;
import org.usul.plaiground.frontend.gameviews.decrypto.DecryptoMainView;

import javax.swing.*;
import java.awt.*;

public class PlaigroundMainView extends JFrame {
    private final PlaigroundApp app;
    private final MainScreen mainScreen = new MainScreen();

    public PlaigroundMainView(PlaigroundApp app) {
        super("PlAIground");

        this.app = app;

        this.setupMenu();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setNewContentPane(this.mainScreen);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 9 * 8;
        int height = screenSize.height / 9 * 8;
        this.setSize(width, height);
        this.setMinimumSize(new Dimension(width, height));

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Decrypto");
        JMenuItem startGameItem = new JMenuItem("Start New Game");
        startGameItem.addActionListener(e -> startDecryptoGame());
        gameMenu.add(startGameItem);
        menuBar.add(gameMenu);
        this.setJMenuBar(menuBar);
    }

    private void startDecryptoGame() {
        DecryptoMainView decryptoMainView = new DecryptoMainView(this.app);
        this.setNewContentPane(decryptoMainView);
    }

    private void setNewContentPane(JPanel newContentPane) {
        this.setContentPane(newContentPane);
        this.revalidate();
        this.repaint();
    }
}
