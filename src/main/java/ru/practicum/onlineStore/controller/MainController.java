package ru.practicum.onlineStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.ItemService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/main/items";
    }

    @GetMapping("/main/items")
    public String showItems(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model
    ) {
        List<Item> filtered = itemService.findAll().stream()
                .filter(i -> i.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        switch (sort) {
            case "ALPHA" -> filtered.sort(Comparator.comparing(Item::getTitle));
            case "PRICE" -> filtered.sort(Comparator.comparing(Item::getPrice));
        }

        int fromIndex = Math.min((pageNumber - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        List<Item> pageItems = filtered.subList(fromIndex, toIndex);

        List<List<Item>> itemsRows = pageItems.stream()
                .collect(Collectors.groupingBy(i -> pageItems.indexOf(i) / 3))
                .values().stream().toList();

        Map<Long, Integer> cartCounts = cartService.getCartItemsCount();

        model.addAttribute("items", itemsRows);
        model.addAttribute("cartCounts", cartCounts);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", new Object() {
            public int pageNumber() { return pageNumber; }
            public int pageSize() { return pageSize; }
            public boolean hasNext() { return toIndex < filtered.size(); }
            public boolean hasPrevious() { return pageNumber > 1; }
        });

        return "main";
    }

    @PostMapping("/main/items/{id}")
    public String updateCartFromMain(
            @PathVariable Long id,
            @RequestParam String action
    ) {
        itemService.findById(id).ifPresent(item -> {
            switch (action.toUpperCase()) {
                case "PLUS" -> cartService.addItem(item);
                case "MINUS" -> cartService.removeOne(item);
                case "DELETE" -> cartService.deleteItem(item);
            }
        });
        return "redirect:/main/items";
    }

    @GetMapping("/items/{id}")
    public String showItem(@PathVariable Long id, Model model) {
        itemService.findById(id).ifPresent(item -> model.addAttribute("item", item));
        model.addAttribute("itemsCount", cartService.getCartItemsCount());
        return "item";
    }

    @PostMapping("/items/{id}")
    public String updateCartFromItem(
            @PathVariable Long id,
            @RequestParam String action
    ) {
        itemService.findById(id).ifPresent(item -> {
            switch (action.toUpperCase()) {
                case "PLUS" -> cartService.addItem(item);
                case "MINUS" -> cartService.removeOne(item);
                case "DELETE" -> cartService.deleteItem(item);
            }
        });
        return "redirect:/items/" + id;
    }

}
