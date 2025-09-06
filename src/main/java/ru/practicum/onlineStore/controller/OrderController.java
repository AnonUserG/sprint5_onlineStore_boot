package ru.practicum.onlineStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import ru.practicum.onlineStore.service.CartService;
import ru.practicum.onlineStore.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @PostMapping("/buy")
    public String buy() {
        Map<Item, Integer> cart = cartService.getCart();
        System.out.println("Cart contents: " + cart);

        List<OrderItem> orderItems = cart.entrySet().stream()
                .map(e -> OrderItem.builder()
                        .item(e.getKey())
                        .count(e.getValue())
                        .price(e.getKey().getPrice())
                        .build())
                .collect(Collectors.toList());

        Order order = orderService.createOrder(orderItems);
        cartService.clear();
        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }


    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);

        Map<Long, BigDecimal> orderTotals = orders.stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        order -> order.getItems().stream()
                                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getCount())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ));
        model.addAttribute("orderTotals", orderTotals);

        return "orders";
    }

    @GetMapping("/{id}")
    public String showOrder(@PathVariable Long id,
                            @RequestParam(defaultValue = "false") boolean newOrder,
                            Model model) {
        orderService.findById(id).ifPresent(order -> {
            model.addAttribute("order", order);

            BigDecimal total = order.getItems().stream()
                    .map(OrderItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("total", total);
        });
        model.addAttribute("newOrder", newOrder);
        return "order";
    }
}
