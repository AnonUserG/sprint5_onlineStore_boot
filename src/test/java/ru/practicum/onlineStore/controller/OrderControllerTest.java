package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag("controllers")
@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("POST /orders/buy создаёт заказ и редиректит на страницу заказа")
    void buy_CreatesOrderAndRedirects() {
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
        when(orderService.createOrder(any(Flux.class))).thenReturn(Mono.just(order));

        webTestClient.post().uri("/orders/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/42?newOrder=true");

        verify(cartService).clear();
        verify(orderService).createOrder(any(Flux.class));
    }


    @Test
    void listOrders_ReturnsOrdersView() {
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

        when(orderService.findAll()).thenReturn(Flux.just(order));

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    assert body.contains("Test Item");
                });
    }

    @Test
    @DisplayName("GET /orders/{id} возвращает страницу заказа с total и newOrder=false")
    void showOrder_ReturnsOrderView() {
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

        when(orderService.findById(99L)).thenReturn(Mono.just(order));

        webTestClient.get().uri("/orders/99")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assert body != null;
                    assert body.contains("300");
                    assert body.contains("order");
                });
    }
}
