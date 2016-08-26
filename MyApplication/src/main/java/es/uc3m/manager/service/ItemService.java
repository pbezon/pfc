package es.uc3m.manager.service;

import java.util.List;

import es.uc3m.manager.dao.ProxyDao;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.pojo.Status;
import es.uc3m.manager.util.Constants;

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


    public List<Item> getItem(String id) {
        return dao.getItem(id);
    }

    public Item updateItem(Item item) {
        return dao.updateItem(item);
    }

    public Boolean deleteItem(String itemId) {
        return dao.deleteItem(itemId);
    }

    public Item returnItem(Item item) {
        item.getCurrentStatus().setStatus(Constants.STATUS_AVAILABLE);
        return dao.returnItem(item);
    }

    public Item withdrawItem(Item item) {
        item.getCurrentStatus().setStatus(Constants.STATUS_TAKEN);
        return dao.withdrawItem(item);
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
