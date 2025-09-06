package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("POST /orders/buy создаёт заказ и редиректит на страницу заказа")
    void buy_CreatesOrderAndRedirects() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Кружка Java");
        item.setPrice(BigDecimal.valueOf(500));

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(2)
                .price(BigDecimal.valueOf(500))
                .build();

        Order order = new Order();
        order.setId(42L);
        order.setItems(List.of(orderItem));

        when(cartService.getCart()).thenReturn(Map.of(item, 2));
        when(orderService.createOrder(anyList())).thenReturn(order);

        mockMvc.perform(post("/orders/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/42?newOrder=true"));

        verify(cartService).clear();
        verify(orderService).createOrder(anyList());
    }

    @Test
    void listOrders_ReturnsOrdersView() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Test Item");
        item.setPrice(BigDecimal.valueOf(100));

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(2)
                .price(item.getPrice())
                .build();

        Order order = new Order();
        order.setId(1L);
        order.setItems(List.of(orderItem));

        when(orderService.findAll()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("orderTotals"))
                .andExpect(content().string(
                        containsString("Test Item")
                ));
    }


    @Test
    @DisplayName("GET /orders/{id} возвращает страницу заказа с total и newOrder=false")
    void showOrder_ReturnsOrderView() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(300));

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(1)
                .price(BigDecimal.valueOf(300))
                .build();

        Order order = new Order();
        order.setId(99L);
        order.setItems(List.of(orderItem));

        when(orderService.findById(99L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attribute("newOrder", false));
    }
}
