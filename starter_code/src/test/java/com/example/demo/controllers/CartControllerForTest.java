package com.example.demo.controllers;


import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerForTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository",userRepository);
        TestUtils.injectObjects(cartController, "cartRepository",cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository",itemRepository);
    }

    @Test
    public void addToCartTest() {
        // Mocking user and item
        User user = new User();
        user.setUsername("testuser");
        user.setCart(new Cart());

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.99));

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Creating ModifyCartRequest
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(3);

        // Performing the add to cart operation
        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());
    }

    @Test
    public void removeFromCartTest() {
        // Mocking user and item
        User user = new User();
        user.setUsername("testuser");
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.99));
        cart.addItem(item);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Creating ModifyCartRequest
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        // Performing the remove from cart operation
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart updatedCart = response.getBody();
        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
    }
}