package dao.interfaces;

import com.shopfx.entities.Client;
import java.util.List;

public interface ClientDAO {
    void create(Client client);

    Client findById(int id);

    List<Client> findAll();

    List<Client> search(String keyword); 

    void update(Client client);

    void delete(int id);
}
