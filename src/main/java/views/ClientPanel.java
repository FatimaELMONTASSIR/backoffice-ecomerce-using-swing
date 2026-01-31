package views;

import com.shopfx.entities.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import services.ClientService;

public class ClientPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ClientService clientService;
    private JTextField txtSearch;

    public ClientPanel() {
        clientService = new ClientService();
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Ajouter");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");

        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Rechercher");
        JButton btnRefresh = new JButton("Tout voir");

        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDelete);
        toolBar.addSeparator();
        toolBar.add(new JLabel("Recherche: "));
        toolBar.add(txtSearch);
        toolBar.add(btnSearch);
        toolBar.add(btnRefresh);
        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Nom", "Email" };
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

        btnSearch.addActionListener(e -> searchTable(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });

        btnAdd.addActionListener(e -> showClientDialog(null));

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                Client c = new Client(
                        id,
                        (String) model.getValueAt(selectedRow, 1),
                        (String) model.getValueAt(selectedRow, 2));
                showClientDialog(c);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ce client ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        clientService.deleteClient(id);
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur suppression : " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.");
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Client> clients = clientService.getAllClients();
        for (Client c : clients) {
            model.addRow(new Object[] { c.getId(), c.getNom(), c.getEmail() });
        }
    }

    private void searchTable(String keyword) {
        model.setRowCount(0);
        List<Client> clients = clientService.searchClients(keyword);
        for (Client c : clients) {
            model.addRow(new Object[] { c.getId(), c.getNom(), c.getEmail() });
        }
    }

    private void showClientDialog(Client client) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Client", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField txtNom = new JTextField(client != null ? client.getNom() : "");
        JTextField txtEmail = new JTextField(client != null ? client.getEmail() : "");

        dialog.add(new JLabel("Nom:"));
        dialog.add(txtNom);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);

        JButton btnSave = new JButton("Enregistrer");
        dialog.add(new JLabel(""));
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                String nom = txtNom.getText();
                String email = txtEmail.getText();

                if (client == null) {
                    Client newC = new Client(nom, email);
                    clientService.addClient(newC);
                } else {
                    client.setNom(nom);
                    client.setEmail(email);
                    clientService.updateClient(client);
                }

                dialog.dispose();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur : " + ex.getMessage());
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
