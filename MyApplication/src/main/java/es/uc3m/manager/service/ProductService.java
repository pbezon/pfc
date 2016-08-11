package es.uc3m.manager.service;

import java.util.List;

import es.uc3m.manager.dao.ProxyDao;
import es.uc3m.manager.pojo.Item;
import es.uc3m.manager.pojo.Status;

/**
 * Created by Snapster on 21/03/2015.
 */
public class ProductService {

    private static ProductService instance;
    private final ProxyDao dao;

    private ProductService() {
        dao = new ProxyDao();
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
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
}
