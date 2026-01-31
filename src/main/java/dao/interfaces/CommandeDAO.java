package dao.interfaces;

import com.shopfx.entities.Commande;
import com.shopfx.entities.LigneCommande;
import java.util.List;

public interface CommandeDAO {
    int create(Commande commande); // Retourne l'ID généré

    void createLigne(LigneCommande ligne);

    Commande findById(int id);

    List<Commande> findAll();

    List<LigneCommande> findLignesByCommandeId(int commandeId);

    void updateStatut(int id, String statut);

    // Statistiques
    double getTotalSales();

    int getOrdersCountByStatus(String status);
}
