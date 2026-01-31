package com.mycompany.projetvf;

import javax.swing.SwingUtilities;
import views.MainFrame;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
