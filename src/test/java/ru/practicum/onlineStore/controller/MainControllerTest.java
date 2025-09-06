package ru.practicum.onlineStore.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.ItemService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("GET / редиректит на /main/items")
    void rootRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    @DisplayName("GET /main/items возвращает view main с атрибутами модели")
    void showItems_ReturnsMainView() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Java Mug");
        item.setDescription("Cool mug");
        item.setPrice(BigDecimal.valueOf(500));

        when(itemService.findAll()).thenReturn(List.of(item));
        when(cartService.getCartItemsCount()).thenReturn(Map.of(1L, 2));

        mockMvc.perform(get("/main/items")
                        .param("search", "java")
                        .param("sort", "ALPHA")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("cartCounts"))
                .andExpect(model().attribute("search", "java"))
                .andExpect(model().attribute("sort", "ALPHA"));
    }

    @Test
    @DisplayName("POST /main/items/{id} с action=PLUS вызывает addItem()")
    void updateCartFromMain_Plus() throws Exception {
        Item item = new Item();
        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(post("/main/items/1")
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService).addItem(item);
    }

    @Test
    @DisplayName("GET /items/{id} возвращает view item с атрибутами модели")
    void showItem_ReturnsItemView() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Java Mug");
        item.setDescription("Cool mug");
        item.setPrice(BigDecimal.valueOf(500));

        when(itemService.findById(1L)).thenReturn(Optional.of(item));
        when(cartService.getCartItemsCount()).thenReturn(Map.of(1L, 2));

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeExists("itemsCount"));
    }

    @Test
    @DisplayName("POST /items/{id} с action=DELETE вызывает deleteItem() и редиректит обратно")
    void updateCartFromItem_Delete() throws Exception {
        Item item = new Item();
        item.setId(1L);
        when(itemService.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(post("/items/1")
                        .param("action", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(cartService).deleteItem(item);
    }
}
