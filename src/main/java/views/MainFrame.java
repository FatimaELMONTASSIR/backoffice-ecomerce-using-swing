package views;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("ShopFX Admin - Back-Office E-commerce");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        menuFile.add(exitItem);

        JMenu menuView = new JMenu("Vue");
        JMenuItem dashItem = new JMenuItem("Tableau de Bord");
        JMenuItem productsItem = new JMenuItem("Produits");
        JMenuItem clientsItem = new JMenuItem("Clients");
        JMenuItem ordersItem = new JMenuItem("Commandes");

        menuView.add(dashItem);
        menuView.add(productsItem);
        menuView.add(clientsItem);
        menuView.add(ordersItem);

        menuBar.add(menuFile);
        menuBar.add(menuView);
        setJMenuBar(menuBar);

        // Main Panel with Card Layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels (Lazy loading or direct init)
        // Note: We need to pass services to panels usually, or they instantiate them.
        // For simplicity, panels will instantiate their services.

        JPanel dashboardPanel = new DashboardPanel();
        JPanel productPanel = new ProductPanel();
        JPanel clientPanel = new ClientPanel();
        JPanel orderPanel = new OrderPanel();

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(productPanel, "PRODUITS");
        mainPanel.add(clientPanel, "CLIENTS");
        mainPanel.add(orderPanel, "COMMANDES");

        add(mainPanel, BorderLayout.CENTER);

        // Navigation Actions
        dashItem.addActionListener(e -> {
            cardLayout.show(mainPanel, "DASHBOARD");
            ((DashboardPanel) dashboardPanel).refresh(); // Custom method to reload stats
        });
        productsItem.addActionListener(e -> cardLayout.show(mainPanel, "PRODUITS"));
        clientsItem.addActionListener(e -> cardLayout.show(mainPanel, "CLIENTS"));
        ordersItem.addActionListener(e -> cardLayout.show(mainPanel, "COMMANDES"));

        // Default view
        cardLayout.show(mainPanel, "DASHBOARD");
    }
}
