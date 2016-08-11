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

    public List<Item> getProduct(String id) {
        if (Constants.OFFLINE) {
            if (id == null || id.isEmpty()) {
                return new ArrayList<Item>(dummyList.values());
            }
            Item p = dummyList.get(id);
            if (p != null) return Collections.singletonList(p);
            return new ArrayList<Item>();
        } else {
            return restfulDao.realGetProduct(id);
        }
    }

    public Item updateProduct(Item item) {
        if (Constants.OFFLINE) {
            dummyList.put(item.get_id(), item);
            return dummyList.get(item.get_id());
        } else {
            return restfulDao.updateProduct(item);
        }
    }

    public Boolean deleteProduct(String productId) {
        if (Constants.OFFLINE) {
            return dummyList.remove(productId) != null;
        } else {
            return restfulDao.deleteProduct(productId);
        }
    }

    public Item returnProduct(Item item) {
        if (Constants.OFFLINE) {
            item.getCurrentStatus().setStatus("Available");
            dummyList.put(item.get_id(), item);
            return dummyList.get(item.get_id());
        } else {
            restfulDao.returnProduct(item);
            List<Item> itemList = restfulDao.getProduct(item.get_id());
            if (itemList != null && !itemList.isEmpty()) {
                return itemList.get(0);
            }
            return item;
        }
    }

    public Item withdrawProduct(String id) {
        if (Constants.OFFLINE) {
            Item item = dummyList.get(id);
            if (item != null) {
                item.getCurrentStatus().setStatus("Taken");
                dummyList.put(id, item);
                return dummyList.get(id);
            }
            return new Item();
        } else {
            restfulDao.withdrawProduct(id);
            List<Item> item = restfulDao.getProduct(id);
            if (item != null && !item.isEmpty()) {
                return item.get(0);
            }
            return new Item();
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

}
