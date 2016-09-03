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
public class ProxyDao implements ItemDao {

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
            return restfulDao.getItem(id);
        }
    }

    public Boolean updateItem(Item item) {
        if (Constants.OFFLINE) {
            dummyList.put(item.get_id(), item);
            return true;
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

    public Boolean returnItem(Item item) {
        if (Constants.OFFLINE) {
            item.getCurrentStatus().setStatus(Constants.STATUS_AVAILABLE);
            item.getCurrentStatus().setContactUri("");
            item.getCurrentStatus().setCalendarEventId("");
            dummyList.put(item.get_id(), item);
            return true;
        } else {
            return restfulDao.returnItem(item);
        }
    }

    public Boolean withdrawItem(Item item) {
        if (Constants.OFFLINE) {
            item.getCurrentStatus().setStatus(Constants.STATUS_TAKEN);
            dummyList.put(item.get_id(), item);
            return true;
        } else {
            return restfulDao.withdrawItem(item);
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
