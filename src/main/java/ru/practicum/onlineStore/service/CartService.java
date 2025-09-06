package ru.practicum.onlineStore.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.practicum.onlineStore.model.Item;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Getter
    private final Map<Item, Integer> cart = new HashMap<>();

    public void addItem(Item item) {
        cart.put(item, cart.getOrDefault(item, 0) + 1);
    }

    public void removeOne(Item item) {
        cart.computeIfPresent(item, (i, count) -> count > 1 ? count - 1 : null);
    }

    public void deleteItem(Item item) {
        cart.remove(item);
    }

    public void clear() {
        cart.clear();
    }

    public BigDecimal getTotal() {
        return cart.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public Map<Long, Integer> getCartItemsCount() {
        return cart.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getId(),
                        Map.Entry::getValue
                ));
    }
}
