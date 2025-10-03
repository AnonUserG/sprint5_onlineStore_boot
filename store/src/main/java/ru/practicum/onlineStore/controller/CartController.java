package ru.practicum.onlineStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public Mono<Rendering> showCart() {
        return Mono.zip(
                cartService.getCartItemsCount(),
                cartService.getTotal(),
                cartService.isEmpty()
        ).map(tuple -> {
            Map<Long, Integer> itemsCount = tuple.getT1();
            BigDecimal total = tuple.getT2();
            Boolean empty = tuple.getT3();

            List<Item> items = cartService.getCart().keySet().stream().toList();

            return Rendering.view("cart")
                    .modelAttribute("items", items)
                    .modelAttribute("itemsCount", itemsCount)
                    .modelAttribute("total", total)
                    .modelAttribute("empty", empty)
                    .build();
        });
    }

    @PostMapping("/items/{id}")
    public Mono<String> updateCart(@PathVariable Long id, ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String action = formData.getFirst("action");
                    if (action == null) return Mono.just("redirect:/cart/items");

                    return Mono.defer(() ->
                            cartService.getCart().keySet().stream()
                                    .filter(item -> item.getId().equals(id))
                                    .findFirst()
                                    .map(item -> switch (action.toLowerCase()) {
                                        case "plus" -> cartService.addItem(item);
                                        case "minus" -> cartService.removeOne(item);
                                        case "delete" -> cartService.deleteItem(item);
                                        default -> Mono.empty();
                                    })
                                    .orElse(Mono.empty())
                    ).then(Mono.just("redirect:/cart/items"));
                });
    }
}

