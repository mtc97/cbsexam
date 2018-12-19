package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. fix
public class OrderCache {

    // List of orders
    private ArrayList<Order> orders;

    // Time cache should live
    private long ttl;

    // Sets when the cache has been created
    private long created;

    // Order constructor
    public OrderCache(){this.ttl = Config.getOrderTtl();}

    // If we whis to clear cache, we can set force update.
    // Otherwise we look at the age of the cache and figure out if we should update.
    // If the list is empty we also check for new orders
    public ArrayList<Order> getOrders(Boolean forceUpdate){
        if (forceUpdate
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.orders == null){
            // Get orders from the controller, since they will be updated
            ArrayList<Order> orders = OrderController.getOrders();

            // Sets orders for the instance and sets a timestamp
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
        }
        // Returns orders
        return this.orders;
    }
}
