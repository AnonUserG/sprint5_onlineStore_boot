package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("GET /cart/items возвращает cart view с атрибутами модели")
    void showCart_ReturnsCartView() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Кружка Java");

        Map<Item, Integer> cart = Map.of(item, 2);

        when(cartService.getCart()).thenReturn(cart);
        when(cartService.getTotal()).thenReturn(BigDecimal.valueOf(1000));
        when(cartService.isEmpty()).thenReturn(false);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("itemsCount"))
                .andExpect(model().attribute("total", BigDecimal.valueOf(1000)))
                .andExpect(model().attribute("empty", false));
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=PLUS вызывает addItem()")
    void updateCart_PlusAction() throws Exception {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        mockMvc.perform(post("/cart/items/1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).addItem(item);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=MINUS вызывает removeOne()")
    void updateCart_MinusAction() throws Exception {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        mockMvc.perform(post("/cart/items/1")
                        .param("action", "MINUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).removeOne(item);
    }

    @Test
    @DisplayName("POST /cart/items/{id} с action=DELETE вызывает deleteItem()")
    void updateCart_DeleteAction() throws Exception {
        Item item = new Item();
        item.setId(1L);

        when(cartService.getCart()).thenReturn(Map.of(item, 1));

        mockMvc.perform(post("/cart/items/1")
                        .param("action", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).deleteItem(item);
    }
}
