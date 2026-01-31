package views;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import services.CommandeService;

public class DashboardPanel extends JPanel {

    private CommandeService commandeService;
    private JLabel lblSales;
    private JPanel chartsPanel;

    public DashboardPanel() {
        commandeService = new CommandeService();
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(60, 179, 113));
        lblSales = new JLabel("Chiffre d'Affaires Total : 0.00 €");
        lblSales.setFont(new Font("Arial", Font.BOLD, 24));
        lblSales.setForeground(Color.WHITE);
        headerPanel.add(lblSales);
        add(headerPanel, BorderLayout.NORTH);

        // Charts Area
        chartsPanel = new JPanel(new GridLayout(1, 2)); // Or 1,1 if only Pie
        add(chartsPanel, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.addActionListener(e -> refresh());
        add(btnRefresh, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        // Update Sales
        double sales = commandeService.getChiffreAffaires();
        lblSales.setText(String.format("Chiffre d'Affaires Total : %.2f €", sales));

        // Update Charts
        chartsPanel.removeAll();

        // 1. Pie Chart - Commandes par statut
        DefaultPieDataset dataset = new DefaultPieDataset();
        String[] statuses = { "NOUVELLE", "PAYEE", "EXPEDIEE", "REMBOURSEE" };
        for (String s : statuses) {
            int count = commandeService.getCountByStatus(s);
            if (count > 0) {
                dataset.setValue(s, count);
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Répartition des Commandes",
                dataset,
                true,
                true,
                false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartsPanel.add(chartPanel);

        // (Optional) Bar Chart for Top Products could go here if implemented

        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
}
