package ru.practicum.onlineStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public String showCart(Model model) {
        Map<Item, Integer> cartItems = cartService.getCart();
        List<Item> items = cartService.getCart().keySet().stream().toList();

        Map<Long, Integer> itemsCount = cartItems.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getId(),
                        Map.Entry::getValue
                ));

        model.addAttribute("items", items);
        model.addAttribute("itemsCount", itemsCount);
        model.addAttribute("total", cartService.getTotal());
        model.addAttribute("empty", cartService.isEmpty());
        return "cart";
    }

    @PostMapping("/items/{id}")
    public String updateCart(
            @PathVariable Long id,
            @RequestParam String action
    ) {
        cartService.getCart().keySet().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .ifPresent(item -> {
                    switch (action.toUpperCase()) {
                        case "PLUS" -> cartService.addItem(item);
                        case "MINUS" -> cartService.removeOne(item);
                        case "DELETE" -> cartService.deleteItem(item);
                    }
                });
        return "redirect:/cart/items";
    }
}
