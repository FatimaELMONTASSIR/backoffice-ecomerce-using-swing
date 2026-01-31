package views;

import com.shopfx.entities.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import services.ProduitService;

public class ProductPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ProduitService produitService;

    public ProductPanel() {
        produitService = new ProduitService();
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAdd = new JButton("Ajouter");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");
        JButton btnRefresh = new JButton("Actualiser");

        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDelete);
        toolBar.add(btnRefresh);
        add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Nom", "Catégorie", "Prix", "Stock" };
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

        btnAdd.addActionListener(e -> showProductDialog(null));

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                // In a real app, fetches freshly from DB or uses the row data
                Produit p = new Produit(
                        id,
                        (String) model.getValueAt(selectedRow, 1),
                        (String) model.getValueAt(selectedRow, 2),
                        (double) model.getValueAt(selectedRow, 3),
                        (int) model.getValueAt(selectedRow, 4));
                showProductDialog(p);
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ce produit ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        produitService.deleteProduit(id);
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur suppression : " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Produit> produits = produitService.getAllProduits();
        for (Produit p : produits) {
            model.addRow(new Object[] { p.getId(), p.getNom(), p.getCategorie(), p.getPrix(), p.getStock() });
        }
    }

    private void showProductDialog(Produit produit) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Produit", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField txtNom = new JTextField(produit != null ? produit.getNom() : "");
        JTextField txtCat = new JTextField(produit != null ? produit.getCategorie() : "");
        JTextField txtPrix = new JTextField(produit != null ? String.valueOf(produit.getPrix()) : "");
        JTextField txtStock = new JTextField(produit != null ? String.valueOf(produit.getStock()) : "");

        dialog.add(new JLabel("Nom:"));
        dialog.add(txtNom);
        dialog.add(new JLabel("Catégorie:"));
        dialog.add(txtCat);
        dialog.add(new JLabel("Prix:"));
        dialog.add(txtPrix);
        dialog.add(new JLabel("Stock:"));
        dialog.add(txtStock);

        JButton btnSave = new JButton("Enregistrer");
        dialog.add(new JLabel(""));
        dialog.add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                String nom = txtNom.getText();
                String cat = txtCat.getText();
                double prix = Double.parseDouble(txtPrix.getText());
                int stock = Integer.parseInt(txtStock.getText());

                if (produit == null) {
                    Produit newP = new Produit(nom, cat, prix, stock);
                    produitService.addProduit(newP);
                } else {
                    produit.setNom(nom);
                    produit.setCategorie(cat);
                    produit.setPrix(prix);
                    produit.setStock(stock);
                    produitService.updateProduit(produit);
                }

                dialog.dispose();
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur de format numérique.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur : " + ex.getMessage());
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
