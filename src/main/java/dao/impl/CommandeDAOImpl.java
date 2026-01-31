package dao.impl;

import com.shopfx.entities.Commande;
import com.shopfx.entities.LigneCommande;
import config.DBConnection;
import dao.interfaces.CommandeDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAOImpl implements CommandeDAO {

    @Override
    public int create(Commande commande) {
        String sql = "INSERT INTO commande (numero_unique, client_id, date, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, commande.getNumeroUnique());
            stmt.setInt(2, commande.getClientId());
            stmt.setTimestamp(3, Timestamp.valueOf(commande.getDate()));
            stmt.setString(4, commande.getStatut());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void createLigne(LigneCommande ligne) {
        String sql = "INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ligne.getCommandeId());
            stmt.setInt(2, ligne.getProduitId());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setDouble(4, ligne.getPrixUnitaire());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commande findById(int id) {
        String sql = "SELECT c.*, cl.nom as client_nom FROM commande c JOIN client cl ON c.client_id = cl.id WHERE c.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCommande(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Commande> findAll() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, cl.nom as client_nom FROM commande c JOIN client cl ON c.client_id = cl.id ORDER BY c.date DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                commandes.add(mapResultSetToCommande(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    @Override
    public List<LigneCommande> findLignesByCommandeId(int commandeId) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, p.nom as produit_nom FROM ligne_commande lc JOIN produit p ON lc.produit_id = p.id WHERE lc.commande_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commandeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LigneCommande lc = new LigneCommande();
                    lc.setId(rs.getInt("id"));
                    lc.setCommandeId(rs.getInt("commande_id"));
                    lc.setProduitId(rs.getInt("produit_id"));
                    lc.setQuantite(rs.getInt("quantite"));
                    lc.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                    lc.setProduitNom(rs.getString("produit_nom"));
                    lignes.add(lc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lignes;
    }

    @Override
    public void updateStatut(int id, String statut) {
        String sql = "UPDATE commande SET statut = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statut);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getTotalSales() {
        String sql = "SELECT sum(montant) FROM paiement"; // Simplified assumption, actually better to check payments
        // Or if we base it on orders:
        // SELECT sum(lc.quantite * lc.prix_unitaire) FROM ligne_commande lc JOIN
        // commande c on lc.commande_id = c.id WHERE c.statut = 'PAYEE'
        // Let's use the latter for more accuracy relative to items sold
        String sqlSales = "SELECT sum(lc.quantite * lc.prix_unitaire) FROM ligne_commande lc JOIN commande c on lc.commande_id = c.id WHERE c.statut IN ('PAYEE', 'EXPEDIEE')";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlSales)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public int getOrdersCountByStatus(String status) {
        String sql = "SELECT count(*) FROM commande WHERE statut = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande c = new Commande();
        c.setId(rs.getInt("id"));
        c.setNumeroUnique(rs.getString("numero_unique"));
        c.setClientId(rs.getInt("client_id"));
        c.setDate(rs.getTimestamp("date").toLocalDateTime());
        c.setStatut(rs.getString("statut"));
        c.setClientNom(rs.getString("client_nom"));
        return c;
    }
}
