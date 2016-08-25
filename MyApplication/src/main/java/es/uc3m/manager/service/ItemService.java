package es.uc3m.manager.service;

import java.util.List;

import es.uc3m.manager.dao.ProxyDao;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.pojo.Status;

/**
 * Created by Snapster on 21/03/2015.
 */
public class ItemService {

    private static ItemService instance;
    private final ProxyDao dao;

    private ItemService() {
        dao = new ProxyDao();
    }

    public static ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }


    public List<Item> getProduct(String id) {
        return dao.getProduct(id);
    }

    public Item updateProduct(Item item) {
        return dao.updateProduct(item);
    }

    public Boolean deleteProduct(String productId) {
        return dao.deleteProduct(productId);
    }

    public Item returnProduct(Item item) {
        return dao.returnProduct(item);
    }

    public Item withdrawProduct(String id) {
        return dao.withdrawProduct(id);
    }

    public Boolean add(Item p) {
        Status initialStatus = new Status();
        initialStatus.setStatus("Available");
        p.setCurrentStatus(initialStatus);
        return dao.add(p);
    }

    public Boolean edit(Item p) {
        return dao.add(p);
    }

    public boolean reload (String ip, String port) {
        if (ip == null || ip.isEmpty() || port == null || port.isEmpty()) {
            return false;
        }
        return dao.reload(ip, port);
    }
}
