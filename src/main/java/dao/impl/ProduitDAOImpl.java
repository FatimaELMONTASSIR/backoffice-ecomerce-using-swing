package dao.impl;

import com.shopfx.entities.Produit;
import config.DBConnection;
import dao.interfaces.ProduitDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAOImpl implements ProduitDAO {

    @Override
    public void create(Produit produit) {
        String sql = "INSERT INTO produit (nom, categorie, prix, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getCategorie());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getStock());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Produit findById(int id) {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduit(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Produit> findAll() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    @Override
    public void update(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, categorie = ?, prix = ?, stock = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getCategorie());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getStock());
            stmt.setInt(5, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit p = new Produit();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setCategorie(rs.getString("categorie"));
        p.setPrix(rs.getDouble("prix"));
        p.setStock(rs.getInt("stock"));
        return p;
    }
}
