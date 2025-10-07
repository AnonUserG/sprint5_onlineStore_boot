package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.BalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private DefaultApi defaultApi;

    private Item item1;

    @BeforeEach
    void setUp() {
        item1 = Item.builder()
                .id(1L)
                .title("Кружка")
                .description("Белая кружка")
                .imgPath("mug.jpg")
                .count(0)
                .price(BigDecimal.valueOf(500))
                .build();

        when(cartService.getCart()).thenReturn(Map.of(item1, 2));

        when(cartService.getCartItemsCount()).thenReturn(Mono.just(Map.of(1L, 2)));
        when(cartService.getTotal()).thenReturn(Mono.just(BigDecimal.valueOf(1000)));
        when(cartService.isEmpty()).thenReturn(Mono.just(false));

        when(cartService.addItem(any(Item.class))).thenReturn(Mono.empty());
        when(cartService.removeOne(any(Item.class))).thenReturn(Mono.empty());
        when(cartService.deleteItem(any(Item.class))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("GET /cart/items возвращает страницу корзины с товарами")
    void showCart_ReturnsCartView() {
        Item item1 = Item.builder().id(1L).title("Кружка").price(BigDecimal.valueOf(100)).build();
        Map<Item, Integer> cartItems = Map.of(item1, 2);
        Map<Long, Integer> itemsCount = Map.of(item1.getId(), 2);
        BigDecimal total = BigDecimal.valueOf(200);

        when(cartService.getCart()).thenReturn(cartItems);
        when(cartService.getCartItemsCount()).thenReturn(Mono.just(itemsCount));
        when(cartService.getTotal()).thenReturn(Mono.just(total));
        when(cartService.isEmpty()).thenReturn(Mono.just(false));

        when(defaultApi.apiPaymentsAccountIdBalanceGet("defaultAccount"))
                .thenReturn(Mono.just(new BalanceResponse().balance(500.0)));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response ->
                        org.assertj.core.api.Assertions.assertThat(response.getResponseBody())
                                .contains("cart")
                                .contains("Кружка")
                );

        verify(cartService).getCartItemsCount();
        verify(cartService).getTotal();
        verify(cartService).isEmpty();
        verify(defaultApi).apiPaymentsAccountIdBalanceGet("defaultAccount");
    }


    @Test
    @DisplayName("POST /cart/items/{id} с action=PLUS")
    void updateCart_PlusAction_CallsAddItem() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "plus");

        webTestClient.post()
                .uri("/cart/items/{id}", 1L)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).addItem(item1);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=MINUS")
    void updateCart_MinusAction_CallsRemoveOne() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "minus");

        webTestClient.post()
                .uri("/cart/items/{id}", 1L)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).removeOne(item1);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=DELETE")
    void updateCart_DeleteAction_CallsDeleteItem() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "delete");

        webTestClient.post()
                .uri("/cart/items/{id}", 1L)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(cartService).deleteItem(item1);
    }
}
