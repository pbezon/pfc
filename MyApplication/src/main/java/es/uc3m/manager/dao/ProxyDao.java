package es.uc3m.manager.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.util.Constants;

/**
 * Created by Snapster on 5/1/2016.
 */
public class ProxyDao {

    private final RestfulDao restfulDao = new RestfulDao();
    private final Map<String, Item> dummyList = new HashMap<String, Item>();

    public List<Item> getItem(String id) {
        if (Constants.OFFLINE) {
            if (id == null || id.isEmpty()) {
                return new ArrayList<Item>(dummyList.values());
            }
            Item p = dummyList.get(id);
            if (p != null) return Collections.singletonList(p);
            return new ArrayList<Item>();
        } else {
            return restfulDao.realGetItem(id);
        }
    }

    public Item updateItem(Item item) {
        if (Constants.OFFLINE) {
            dummyList.put(item.get_id(), item);
            return dummyList.get(item.get_id());
        } else {
            return restfulDao.updateItem(item);
        }
    }

    public Boolean deleteItem(String itemId) {
        if (Constants.OFFLINE) {
            return dummyList.remove(itemId) != null;
        } else {
            return restfulDao.deleteItem(itemId);
        }
    }

    public Item returnItem(Item item) {
        if (Constants.OFFLINE) {
            dummyList.put(item.get_id(), item);
            return dummyList.get(item.get_id());
        } else {
            restfulDao.returnItem(item);
            List<Item> itemList = restfulDao.getItem(item.get_id());
            if (itemList != null && !itemList.isEmpty()) {
                return itemList.get(0);
            }
            return item;
        }
    }

    public Item withdrawItem(Item item) {
        if (Constants.OFFLINE) {
            if (item != null) {
                dummyList.put(item.get_id(), item);
                return dummyList.get(item.get_id());
            }
            return new Item();
        } else {
            restfulDao.withdrawItem(item);
            return this.getItem(item.get_id()).get(0);
        }
    }

    public Boolean add(Item p) {
        if (Constants.OFFLINE) {
            dummyList.put(p.get_id(), p);
            return true;
        } else {
            return restfulDao.add(p);
        }
    }

    public boolean reload(String ip, String port) {
        return restfulDao.reload(ip, port);
    }
}
