package es.uc3m.manager.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uc3m.manager.activities.contacts.Constants;
import es.uc3m.manager.pojo.Product;

/**
 * Created by Snapster on 5/1/2016.
 */
public class ProxyDao {

    private final RestfulDao restfulDao = new RestfulDao();
    private final Map<String, Product> dummyList = new HashMap<String, Product>();

    public List<Product> getProduct(String id) {
        if (Constants.OFFLINE) {
            if (id == null || id.isEmpty()) {
                return new ArrayList<Product>(dummyList.values());
            }
            Product p = dummyList.get(id);
            if (p != null) return Collections.singletonList(p);
            return new ArrayList<Product>();
        } else {
            return restfulDao.realGetProduct(id);
        }
    }

    public Product updateProduct(Product product) {
        if (Constants.OFFLINE) {
            dummyList.put(product.get_id(), product);
            return dummyList.get(product.get_id());
        } else {
            return restfulDao.updateProduct(product);
        }
    }

    public Boolean deleteProduct(String productId) {
        if (Constants.OFFLINE) {
            return dummyList.remove(productId) != null;
        } else {
            return restfulDao.deleteProduct(productId);
        }
    }

    public Product returnProduct(Product product) {
        if (Constants.OFFLINE) {
            product.getCurrentStatus().setStatus("Available");
            dummyList.put(product.get_id(), product);
            return dummyList.get(product.get_id());
        } else {
            restfulDao.returnProduct(product);
            List<Product> productList = restfulDao.getProduct(product.get_id());
            if (productList != null && !productList.isEmpty()) {
                return productList.get(0);
            }
            return product;
        }
    }

    public Product withdrawProduct(String id) {
        if (Constants.OFFLINE) {
            Product product = dummyList.get(id);
            if (product != null) {
                product.getCurrentStatus().setStatus("Taken");
                dummyList.put(id, product);
                return dummyList.get(id);
            }
            return new Product();
        } else {
            restfulDao.withdrawProduct(id);
            List<Product> product = restfulDao.getProduct(id);
            if (product != null && !product.isEmpty()) {
                return product.get(0);
            }
            return new Product();
        }
    }

    public Boolean add(Product p) {
        if (Constants.OFFLINE) {
            dummyList.put(p.get_id(), p);
            return true;
        } else {
            return restfulDao.add(p);
        }
    }

}
