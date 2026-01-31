package services;

import com.shopfx.entities.Client;
import dao.impl.ClientDAOImpl;
import dao.interfaces.ClientDAO;
import java.util.List;

public class ClientService {
    private ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAOImpl();
    }

    public void addClient(Client client) {
        // Idéalement vérifier si l'email existe déjà via le DAO (faudrait une méthode
        // findByEmail)
        clientDAO.create(client);
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }

    public List<Client> searchClients(String keyword) {
        return clientDAO.search(keyword);
    }

    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    public void deleteClient(int id) {
        clientDAO.delete(id);
    }
}
