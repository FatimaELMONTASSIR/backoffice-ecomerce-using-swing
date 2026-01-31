package views;

import com.shopfx.entities.Client;
import com.shopfx.entities.Commande;
import com.shopfx.entities.LigneCommande;
import com.shopfx.entities.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import services.ClientService;
import services.CommandeService;
import services.ProduitService;

public class OrderPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private CommandeService commandeService;
    private ClientService clientService;
    private ProduitService produitService;

    public OrderPanel() {
        commandeService = new CommandeService();
        clientService = new ClientService();
        produitService = new ProduitService();

        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Nouvelle Commande");
        JButton btnStatus = new JButton("Changer Statut");
        JButton btnView = new JButton("Voir Détails");
        JButton btnRefresh = new JButton("Actualiser");

        toolBar.add(btnAdd);
        toolBar.add(btnStatus);
        toolBar.add(btnView);
        toolBar.add(btnRefresh);
        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Numéro", "Client", "Date", "Statut" };
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        refreshTable();

        btnRefresh.addActionListener(e -> refreshTable());
        btnAdd.addActionListener(e -> showCreateOrderDialog());

        btnStatus.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                String currentStatus = (String) model.getValueAt(selectedRow, 4);
                // Fetch full object to be safe or use what we have
                Commande c = commandeService.getAllCommandes().stream().filter(cmd -> cmd.getId() == id).findFirst()
                        .orElse(null);
                if (c != null)
                    showStatusDialog(c);
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une commande.");
            }
        });

        btnView.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                showDetailsDialog(id);
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Commande> commandes = commandeService.getAllCommandes();
        for (Commande c : commandes) {
            model.addRow(new Object[] { c.getId(), c.getNumeroUnique(), c.getClientNom(), c.getDate(), c.getStatut() });
        }
    }

    private void showStatusDialog(Commande c) {
        String[] statuses = { "NOUVELLE", "PAYEE", "EXPEDIEE", "REMBOURSEE" };
        String result = (String) JOptionPane.showInputDialog(this, "Nouveau statut :", "Changer statut",
                JOptionPane.QUESTION_MESSAGE, null, statuses, c.getStatut());

        if (result != null && !result.equals(c.getStatut())) {
            try {
                commandeService.updateStatutCommande(c, result);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        }
    }

    private void showDetailsDialog(int commandeId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails Commande", true);
        dialog.setSize(600, 400);

        List<LigneCommande> lignes = commandeService.getLignesCommande(commandeId);
        String[] columns = { "Produit", "Quantité", "Prix Unit.", "Total" };
        DefaultTableModel detailModel = new DefaultTableModel(columns, 0);

        double totalCmd = 0;
        for (LigneCommande lc : lignes) {
            double totalLigne = lc.getQuantite() * lc.getPrixUnitaire();
            totalCmd += totalLigne;
            detailModel.addRow(new Object[] { lc.getProduitNom(), lc.getQuantite(), lc.getPrixUnitaire(), totalLigne });
        }

        JTable detailTable = new JTable(detailModel);
        dialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        JLabel lblTotal = new JLabel("Total Commande : " + totalCmd + " €");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(lblTotal, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showCreateOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouvelle Commande", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);

        // --- Top: Client Selection ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Client :"));

        List<Client> clients = clientService.getAllClients();
        JComboBox<Client> cbClients = new JComboBox<>(clients.toArray(new Client[0]));
        topPanel.add(cbClients);

        dialog.add(topPanel, BorderLayout.NORTH);

        // --- Center: Lines ---
        DefaultTableModel lineModel = new DefaultTableModel(new String[] { "Produit ID", "Nom", "Quantité", "Prix" },
                0);
        JTable lineTable = new JTable(lineModel);
        dialog.add(new JScrollPane(lineTable), BorderLayout.CENTER);

        List<LigneCommande> pendingLines = new ArrayList<>();

        // --- Bottom: Actions ---
        JPanel botPanel = new JPanel();
        JButton btnAddLine = new JButton("Ajouter Produit");
        JButton btnCreate = new JButton("CRÉER COMMANDE");

        botPanel.add(btnAddLine);
        botPanel.add(btnCreate);
        dialog.add(botPanel, BorderLayout.SOUTH);

        // Logic
        btnAddLine.addActionListener(e -> {
            // Simple approach: show another dialog to pick product
            List<Produit> produits = produitService.getAllProduits();
            JComboBox<Produit> cbProd = new JComboBox<>(produits.toArray(new Produit[0]));
            JTextField txtQty = new JTextField("1", 5);

            JPanel p = new JPanel();
            p.add(new JLabel("Produit:"));
            p.add(cbProd);
            p.add(new JLabel("Qté:"));
            p.add(txtQty);

            int res = JOptionPane.showConfirmDialog(dialog, p, "Ajouter Ligne", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    Produit selP = (Produit) cbProd.getSelectedItem();
                    int qty = Integer.parseInt(txtQty.getText());
                    if (qty > 0 && selP != null) {
                        // Check stock localement si on veut, mais le service le fera plus tard
                        // potentiellement
                        // ou on laisse faire. Le stock n'est décrémenté qu'à l'expédition, donc on peut
                        // commander sans stock ?
                        // Le User Prompt dit "Stock décrémenté uniquement à l’expédition". Donc OK.

                        LigneCommande lc = new LigneCommande();
                        lc.setProduitId(selP.getId());
                        lc.setProduitNom(selP.getNom()); // For display
                        lc.setPrixUnitaire(selP.getPrix());
                        lc.setQuantite(qty);

                        pendingLines.add(lc);
                        lineModel.addRow(new Object[] { selP.getId(), selP.getNom(), qty, selP.getPrix() });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Erreur format : " + ex.getMessage());
                }
            }
        });

        btnCreate.addActionListener(e -> {
            Client c = (Client) cbClients.getSelectedItem();
            if (c == null) {
                JOptionPane.showMessageDialog(dialog, "Sélectionnez un client");
                return;
            }
            if (pendingLines.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ajoutez au moins un produit");
                return;
            }

            try {
                Commande cmd = new Commande();
                cmd.setNumeroUnique("CMD-" + System.currentTimeMillis()); // Simple Unique ID
                cmd.setClientId(c.getId());
                cmd.setDate(LocalDateTime.now());
                cmd.setStatut("NOUVELLE");

                commandeService.createCommande(cmd, pendingLines);

                JOptionPane.showMessageDialog(dialog, "Commande créée !");
                dialog.dispose();
                refreshTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Erreur création : " + ex.getMessage());
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
