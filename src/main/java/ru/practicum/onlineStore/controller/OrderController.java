package ru.practicum.onlineStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import ru.practicum.onlineStore.repository.ItemRepository;
import ru.practicum.onlineStore.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    @PostMapping("/buy")
    public Mono<String> buy() {
        return Mono.fromSupplier(cartService::getCart)
                .flatMap(cart -> {
                    List<OrderItem> orderItems = cart.entrySet().stream()
                            .map(e -> OrderItem.builder()
                                    .itemId(e.getKey().getId())
                                    .count(e.getValue())
                                    .price(e.getKey().getPrice())
                                    .build())
                            .collect(Collectors.toList());
                    return orderService.createOrder(Flux.fromIterable(orderItems));
                })
                .flatMap(order -> cartService.clear().thenReturn(order))
                .map(order -> "redirect:/orders/" + order.getId() + "?newOrder=true");
    }

    @GetMapping
    public Mono<Rendering> listOrders() {
        return orderService.findAll()
                .flatMap(order ->
                        orderItemRepository.findByOrderId(order.getId())
                                .flatMap(orderItem ->
                                        itemRepository.findById(orderItem.getItemId())
                                                .map(item -> {
                                                    orderItem.setItem(item);
                                                    // вычисляем total для позиции
                                                    orderItem.setTotal(item.getPrice().multiply(BigDecimal.valueOf(orderItem.getCount())));
                                                    return orderItem;
                                                })
                                )
                                .collectList()
                                .map(items -> {
                                    order.setItems(items);
                                    // суммарная стоимость заказа
                                    BigDecimal total = items.stream()
                                            .map(OrderItem::getTotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    order.setTotal(total);
                                    return order;
                                })
                )
                .collectList()
                .map(orders -> Rendering.view("orders")
                        .modelAttribute("orders", orders)
                        .build()
                );
    }




    @GetMapping("/{id}")
    public Mono<Rendering> showOrder(@PathVariable Long id,
                                     @RequestParam(defaultValue = "false") boolean newOrder) {
        Mono<Order> orderMono = orderService.findById(id);

        Flux<Map<String, Object>> itemsFlux = orderItemRepository.findByOrderId(id)
                .flatMap(orderItem -> itemRepository.findById(orderItem.getItemId())
                        .map(item -> Map.of(
                                "item", item,
                                "count", orderItem.getCount(),
                                "price", orderItem.getPrice()
                        ))
                );

        Mono<List<Map<String, Object>>> itemsMono = itemsFlux.collectList();

        Mono<BigDecimal> totalMono = itemsFlux
                .map(m -> ((BigDecimal) m.get("price")).multiply(BigDecimal.valueOf((Integer) m.get("count"))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Mono.zip(orderMono, itemsMono, totalMono)
                .map(tuple -> Rendering.view("order")
                        .modelAttribute("order", tuple.getT1())
                        .modelAttribute("items", tuple.getT2())
                        .modelAttribute("total", tuple.getT3())
                        .modelAttribute("newOrder", newOrder)
                        .build());
    }
}

