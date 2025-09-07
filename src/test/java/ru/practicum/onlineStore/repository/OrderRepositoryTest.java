package ru.practicum.onlineStore.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.model.Order;
import ru.practicum.onlineStore.model.OrderItem;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Сохранение заказа с товарами")
    void saveOrder_WithItems_Success() {
        Item item = new Item();
        item.setTitle("Кружка");
        item.setPrice(BigDecimal.valueOf(500));
        item = itemRepository.save(item);

        Order order = new Order();
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .count(2)
                .price(item.getPrice())
                .build();

        order.addItem(orderItem);

        Order saved = orderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().getFirst().getItem().getTitle()).isEqualTo("Кружка");
    }

    @Test
    @DisplayName("Поиск заказа по ID")
    void findById_ReturnsOrder() {
        Order order = new Order();
        order = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(order.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("findAll возвращает список заказов")
    void findAll_ReturnsOrders() {
        Order order1 = orderRepository.save(new Order());
        Order order2 = orderRepository.save(new Order());

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).contains(order1, order2);
    }
}
