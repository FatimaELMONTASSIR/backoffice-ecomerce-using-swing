package dao.interfaces;

import com.shopfx.entities.Produit;
import java.util.List;

public interface ProduitDAO {
    void create(Produit produit);

    Produit findById(int id);

    List<Produit> findAll();

    void update(Produit produit);

    void delete(int id);
}
