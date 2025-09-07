package ru.practicum.onlineStore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.onlineStore.model.Item;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CartServiceTest {

    private CartService cartService;

    Item item1;
    Item item2;

    @BeforeEach
    void setUp() {
        cartService = new CartService();

        item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Кружка");
        item1.setPrice(BigDecimal.valueOf(500));

        item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Футболка");
        item2.setPrice(BigDecimal.valueOf(1200));
    }

    @Test
    @DisplayName("Добавление товаров в корзину")
    void addItem_IncreasesCount() {
        cartService.addItem(item1);
        cartService.addItem(item1);

        assertEquals(2, cartService.getCart().get(item1));
    }

    @Test
    @DisplayName("Удаление одного экземпляра товара")
    void removeOne_DecreasesCountOrRemoves() {
        cartService.addItem(item1);
        cartService.addItem(item1);
        cartService.removeOne(item1);

        assertEquals(1, cartService.getCart().get(item1));

        cartService.removeOne(item1);
        assertFalse(cartService.getCart().containsKey(item1));
    }

    @Test
    @DisplayName("Полное удаление товара")
    void deleteItem_RemovesItem() {
        cartService.addItem(item1);
        cartService.deleteItem(item1);

        assertFalse(cartService.getCart().containsKey(item1));
    }

    @Test
    @DisplayName("Очистка корзины")
    void clear_RemovesAllItems() {
        cartService.addItem(item1);
        cartService.addItem(item2);
        cartService.clear();

        assertTrue(cartService.getCart().isEmpty());
    }

    @Test
    @DisplayName("Подсчет общей суммы корзины")
    void getTotal_CalculatesCorrectly() {
        cartService.addItem(item1);
        cartService.addItem(item2);
        cartService.addItem(item2);
        BigDecimal total = cartService.getTotal();

        assertEquals(BigDecimal.valueOf(2900), total);
    }

    @Test
    @DisplayName("Проверка пустоты корзины")
    void isEmpty_ReturnsCorrectValue() {
        assertTrue(cartService.isEmpty());
        cartService.addItem(item1);
        assertFalse(cartService.isEmpty());
    }

    @Test
    @DisplayName("Получение количества товаров по ID")
    void getCartItemsCount_ReturnsCorrectMap() {
        cartService.addItem(item1);
        cartService.addItem(item2);
        cartService.addItem(item2);

        Map<Long, Integer> counts = cartService.getCartItemsCount();

        assertEquals(1, counts.get(item1.getId()));
        assertEquals(2, counts.get(item2.getId()));
    }
}
