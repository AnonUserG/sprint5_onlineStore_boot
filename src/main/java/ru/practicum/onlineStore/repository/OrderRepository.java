package ru.practicum.onlineStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.onlineStore.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
