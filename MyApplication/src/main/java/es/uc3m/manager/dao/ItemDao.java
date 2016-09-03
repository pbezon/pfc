package es.uc3m.manager.dao;

import java.util.List;

import es.uc3m.manager.pojo.Item;

/**
 * Created by Snapster on 03-Sep-16.
 */
public interface ItemDao {

    List<Item> getItem(String itemId);
    Boolean updateItem(Item item);
    Boolean deleteItem(String itemId);
    Boolean returnItem(Item item);
    Boolean withdrawItem(Item item);
    Boolean add(Item item);
    boolean reload(String ip, String port);

}
