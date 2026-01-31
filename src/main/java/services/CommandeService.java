package services;

import com.shopfx.entities.Commande;
import com.shopfx.entities.LigneCommande;
import com.shopfx.entities.Produit;
import dao.impl.CommandeDAOImpl;
import dao.impl.ProduitDAOImpl;
import dao.interfaces.CommandeDAO;
import dao.interfaces.ProduitDAO;
import java.util.List;

public class CommandeService {
    private CommandeDAO commandeDAO;
    private ProduitDAO produitDAO;

    public CommandeService() {
        this.commandeDAO = new CommandeDAOImpl();
        this.produitDAO = new ProduitDAOImpl();
    }

    public void createCommande(Commande commande, List<LigneCommande> lignes) {
        // Validation
        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException("Une commande doit contenir au moins un produit.");
        }

        // 1. Créer la commande
        int commandeId = commandeDAO.create(commande);

        // 2. Créer les lignes
        if (commandeId != -1) {
            for (LigneCommande ligne : lignes) {
                ligne.setCommandeId(commandeId);
                commandeDAO.createLigne(ligne);
            }
        }
    }

    public List<Commande> getAllCommandes() {
        return commandeDAO.findAll();
    }

    public List<LigneCommande> getLignesCommande(int commandeId) {
        return commandeDAO.findLignesByCommandeId(commandeId);
    }

    public void updateStatutCommande(Commande commande, String newStatut) {
        String oldStatut = commande.getStatut();

        // Règle: Stock décrémenté uniquement à l'expédition
        if ("EXPEDIEE".equals(newStatut) && !"EXPEDIEE".equals(oldStatut)) {
            // On décrémente le stock
            List<LigneCommande> lignes = commandeDAO.findLignesByCommandeId(commande.getId());
            for (LigneCommande ligne : lignes) {
                Produit p = produitDAO.findById(ligne.getProduitId());
                if (p != null) {
                    int newStock = p.getStock() - ligne.getQuantite();
                    if (newStock < 0) {
                        // On devrait peut-être empêcher l'expédition ou autoriser le stock négatif
                        // selon les règles.
                        // Ici on accepte mais on pourrait throw exception.
                    }
                    p.setStock(newStock);
                    produitDAO.update(p);
                }
            }
        }

        commandeDAO.updateStatut(commande.getId(), newStatut);
    }

    // Stats helpers
    public double getChiffreAffaires() {
        return commandeDAO.getTotalSales();
    }

    public int getCountByStatus(String status) {
        return commandeDAO.getOrdersCountByStatus(status);
    }
}
