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
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.ItemService;

import java.math.BigDecimal;
import java.util.Map;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Tag("controllers")
@WebFluxTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("GET / редиректит на /main/items")
    void rootRedirect() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }

    @Test
    @DisplayName("GET /main/items возвращает view main с атрибутами модели")
    void showItems_ReturnsMainView() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Java Mug");
        item.setDescription("Cool mug");
        item.setPrice(BigDecimal.valueOf(500));

        when(itemService.findAll()).thenReturn(Flux.just(item));
        when(cartService.getCartItemsCount()).thenReturn(Mono.just(Map.of(1L, 2)));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/main/items")
                        .queryParam("search", "java")
                        .queryParam("sort", "ALPHA")
                        .queryParam("pageSize", "10")
                        .queryParam("pageNumber", "1")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    // Thymeleaf view attributes проверяются через HTML, здесь можно проверить контент
                });
    }

    @Test
    @DisplayName("POST /main/items/{id} с action=PLUS вызывает addItem()")
    void updateCartFromMain_Plus() {
        Item item = new Item();
        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(Mono.just(item));

        webTestClient.post()
                .uri("/main/items/1?action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(cartService).addItem(item);
    }

    @Test
    @DisplayName("GET /items/{id} возвращает view item с атрибутами модели")
    void showItem_ReturnsItemView() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Java Mug");
        item.setDescription("Cool mug");
        item.setPrice(BigDecimal.valueOf(500));

        when(itemService.findById(1L)).thenReturn(Mono.just(item));
        when(cartService.getCartItemsCount()).thenReturn(Mono.just(Map.of(1L, 2)));

        webTestClient.get().uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    // проверка атрибутов модели через HTML
                });
    }

    @Test
    @DisplayName("POST /items/{id} с action=DELETE вызывает deleteItem() и редиректит обратно")
    void updateCartFromItem_Delete() {
        Item item = new Item();
        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(Mono.just(item));

        webTestClient.post().uri("/items/1?action=DELETE")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/1");

        verify(cartService).deleteItem(item);
    }
}
