package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerForTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitOrderTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Create a list of items
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        // Set item properties like id, name, price, etc.
        item.setId(1L);
        item.setName("Sample Item");
        item.setPrice(BigDecimal.valueOf(10.99));
        items.add(item);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items); // Set the list of items to the cart

        user.setCart(cart);

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(user, order.getUser());
        assertEquals(cart.getItems().size(), order.getItems().size());
    }

    @Test
    public void getOrderForUserTest() {
        User user = new User();
        user.setUsername("testuser");

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setId(1L);
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());
        assertEquals(order.getId(), userOrders.get(0).getId());
    }
}