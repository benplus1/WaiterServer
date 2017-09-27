package com.chives.serve.backend.processor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.chives.serve.backend.models.Client;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chives.serve.backend.Main;
import com.chives.serve.backend.models.Item;
import com.chives.serve.backend.models.Order;


/**
 * Created by aditya on 4/26/17.
 */

@WebSocket
public class RestaurantSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        System.out.println("received something");
        int restaurantId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("restaurantId").get(0));
        Main.restaurantMap.put(new Client(restaurantId, 0), user);
    }

    public static void broadcastOrder(Order o) throws Exception {
        String response;
        try {
            JSONObject obj = new JSONObject();
            obj.put("restaurant", o.restaurant.id);
            obj.put("echo", o.echoId);

            JSONArray array = new JSONArray();

            List<Item> x = ((List<Item>) o.items.clone()).parallelStream().distinct().collect(Collectors.toList());
            x.forEach(e -> {
                JSONObject jo = new JSONObject();
                int quantity = 0;
                for (Item f : o.items) {
                    if (f.name.equals(e.name))
                        quantity++;
                }
                jo.put("name", e.name);
                jo.put("quantity", quantity);
                jo.put("price", e.price);
                jo.put("total",
                        Double.parseDouble(new DecimalFormat("#.##").format(new Double(quantity * e.price))));
                array.put(jo);
            });

            obj.put("items", array);
            obj.put("total_items", x.size());
            obj.put("subtotal", o.subtotal);
            System.err.println("Screen Update: " + obj.toString());

            response = obj.toString();
        } catch (Throwable t) {
            JSONObject errorObj = new JSONObject();
            errorObj.put("status", "No current order. Start your order!");
            System.err.println("Screen Update: " + errorObj);
            response = errorObj.toString();
        }
        Client x = new Client(o.restaurant.id, 0);
        Main.restaurantMap.get(x).getRemote().sendString(response);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        Set<Client> clients = Main.customerMap.keySet();
        for (Client c : clients){
            if (Main.restaurantMap.get(c).equals(user)){
                Main.restaurantMap.remove(c);
                break;
            }
        }
    }

}
