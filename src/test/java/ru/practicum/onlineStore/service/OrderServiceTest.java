package ru.practicum.onlineStore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import ru.practicum.onlineStore.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Кружка");
        item1.setDescription("Белая кружка");
        item1.setImgPath("mug.jpg");
        item1.setCount(0);
        item1.setPrice(BigDecimal.valueOf(500));

        item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Футболка");
        item2.setDescription("Черная футболка");
        item2.setImgPath("tshirt.jpg");
        item2.setCount(0);
        item2.setPrice(BigDecimal.valueOf(1200));
    }

    @Test
    @DisplayName("Сохраняем заказ")
    void saveOrder_Success() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order saved = orderService.save(new Order());

        assertThat(saved.getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Поиск всех заказов")
    void findAll_ReturnsOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.findAll();

        assertThat(orders).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    @DisplayName("Поиск заказа по ID")
    void findById_ReturnsOrder() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> found = orderService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Создание заказа с товарами")
    void createOrder_WithItems() {
        OrderItem oi1 = new OrderItem();
        oi1.setItem(item1);
        oi1.setCount(1);
        oi1.setPrice(item1.getPrice());

        OrderItem oi2 = new OrderItem();
        oi2.setItem(item2);
        oi2.setCount(2);
        oi2.setPrice(item2.getPrice());

        List<OrderItem> orderItems = List.of(oi1, oi2);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.getItems().addAll(orderItems);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order order = orderService.createOrder(orderItems);

        assertNotNull(order.getId());
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().stream().anyMatch(i -> i.getItem().equals(item1)));
        assertTrue(order.getItems().stream().anyMatch(i -> i.getItem().equals(item2)));

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
