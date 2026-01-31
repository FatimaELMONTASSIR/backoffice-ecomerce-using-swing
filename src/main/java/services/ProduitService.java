package services;

import com.shopfx.entities.Produit;
import dao.impl.ProduitDAOImpl;
import dao.interfaces.ProduitDAO;
import java.util.List;

public class ProduitService {
    private ProduitDAO produitDAO;

    public ProduitService() {
        this.produitDAO = new ProduitDAOImpl();
    }

    public void addProduit(Produit produit) {
        // Validation basique
        if (produit.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix doit être positif");
        }
        if (produit.getStock() < 0) {
            throw new IllegalArgumentException("Le stock doit être positif");
        }
        produitDAO.create(produit);
    }

    public List<Produit> getAllProduits() {
        return produitDAO.findAll();
    }

    public void updateProduit(Produit produit) {
        if (produit.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix doit être positif");
        }
        produitDAO.update(produit);
    }

    public void deleteProduit(int id) {
        produitDAO.delete(id);
    }
}
