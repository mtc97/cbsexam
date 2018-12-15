package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. fix
public class OrderCache {

    private ArrayList<Order> orders;

    private long ttl;

    private long created;

    public OrderCache(){this.ttl = Config.getOrderTtl();}

    public ArrayList<Order> getOrders(Boolean forceUpdate){
        if (forceUpdate
                || ((this.created + this.ttl) >= (System.currentTimeMillis() / 1000L))
                || this.orders == null){
            ArrayList<Order> orders = OrderController.getOrders();

            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
        }
        return this.orders;
    }
}
