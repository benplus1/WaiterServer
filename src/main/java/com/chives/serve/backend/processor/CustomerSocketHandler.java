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

@WebSocket
public class CustomerSocketHandler {

    static final RestaurantClient client = new RestaurantClient();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        int restaurantId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("restaurantId").get(0));
        int echoId = Integer.parseInt(user.getUpgradeRequest().getParameterMap().get("echoId").get(0));
        Client x = new Client(restaurantId, echoId);
        Main.customerMap.put(x, user);
        System.out.println("connected");
    }

    public static void broadcastOrder(int restaurantId, int echoId, String name, double price) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("price", price);
        Main.customerMap.get(new Client(restaurantId, echoId)).getRemote().sendString(obj.toString());
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        Set<Client> clients = Main.customerMap.keySet();
        for (Client c : clients){
            if (Main.customerMap.get(c).equals(user)){
                Main.customerMap.remove(c);
                break;
            }
        }
        System.out.println("disconnected");
    }
}
