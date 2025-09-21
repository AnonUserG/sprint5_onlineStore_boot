package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag("controllers")
@WebFluxTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("GET /cart/items возвращает cart view с атрибутами модели")
    void showCart_ReturnsCartView() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Кружка Java");

        Map<Item, Integer> cart = Map.of(item, 2);

        when(cartService.getCart()).thenReturn(cart);
        when(cartService.getTotal()).thenReturn(Mono.just(BigDecimal.valueOf(1000)));
        when(cartService.isEmpty()).thenReturn(Mono.just(false));

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBodyContent());
                    assert body.contains("Кружка Java");
                    assert body.contains("1000");
                });
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=PLUS вызывает addItem()")
    void updateCart_PlusAction() {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items/{id}")
                        .queryParam("action", "PLUS")
                        .build(1))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).addItem(item);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=MINUS вызывает removeOne()")
    void updateCart_MinusAction() {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items/{id}")
                        .queryParam("action", "MINUS")
                        .build(1))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).removeOne(item);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=DELETE вызывает deleteItem()")
    void updateCart_DeleteAction() {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/cart/items/{id}")
                        .queryParam("action", "DELETE")
                        .build(1))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).deleteItem(item);
    }
}
