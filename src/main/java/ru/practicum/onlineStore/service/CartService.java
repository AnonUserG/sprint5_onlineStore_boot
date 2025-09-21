package ru.practicum.onlineStore.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Getter
    private final Map<Item, Integer> cart = new ConcurrentHashMap<>();

    public Mono<Void> addItem(Item item) {
        return Mono.fromRunnable(() -> cart.merge(item, 1, Integer::sum));
    }

    public Mono<Void> removeOne(Item item) {
        return Mono.fromRunnable(() -> {
            cart.computeIfPresent(item, (k, v) -> (v > 1) ? v - 1 : null);
        });
    }

    public  Mono<Void> deleteItem(Item item) {
        return Mono.fromRunnable(() -> cart.remove(item));
    }

    public  Mono<Void> clear() {
        return Mono.fromRunnable(cart::clear);
    }

    public Mono<BigDecimal> getTotal() {
        return Mono.fromSupplier(() ->
                cart.entrySet().stream()
                        .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    public Mono<Boolean> isEmpty() {
        return Mono.fromSupplier(cart::isEmpty);
    }

    public Mono<Map<Long, Integer>> getCartItemsCount() {
        return Mono.fromSupplier(() ->
                cart.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().getId(),
                                Map.Entry::getValue
                        ))
        );
    }
}
