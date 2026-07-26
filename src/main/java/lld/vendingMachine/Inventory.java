package lld.vendingMachine;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {
    Map<UUID, Product> productMap ;
    Map<UUID , Integer> productStock ;

    Inventory(){
        this.productMap= new ConcurrentHashMap<>();
        this.productStock = new ConcurrentHashMap<>();
    }

    void add(Product product , int qty){
        productMap.computeIfAbsent(product.id, k -> product);
        productStock.merge(product.id,qty,Integer::sum);
    }

    public Product getProduct(UUID id){
        return this.productMap.get(id);
    }

    public boolean hasProduct(UUID pdtId){
        return productStock.getOrDefault(pdtId,0)>0;
    }

    public void releaseProduct(UUID pdtId) throws Exception {
        if(this.hasProduct(pdtId)){
            productStock.merge(pdtId,-1 , Integer::sum);
            if(productStock.get(pdtId) ==0){
                productStock.remove(pdtId);
                productMap.remove(pdtId);
            }

        }else
            throw  new Exception("Product is out of stock , Cant release!");
    }
}
