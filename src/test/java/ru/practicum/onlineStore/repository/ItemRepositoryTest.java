package ru.practicum.onlineStore.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.onlineStore.model.Item;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Sql(statements = "DELETE FROM items", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("Сохранение и поиск по id")
    void saveAndFindById() {
        Item item = new Item();
        item.setTitle("Чашка Spring");
        item.setDescription("Керамическая кружка с логотипом Spring");
        item.setPrice(BigDecimal.valueOf(500));

        Item saved = itemRepository.save(item);

        Optional<Item> found = itemRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Чашка Spring");
        assertThat(found.get().getPrice()).isEqualByComparingTo("500");
    }

    @Test
    @DisplayName("Поиск всех товаров возвращает список")
    void findAll_ReturnsItems() {
        Item item1 = new Item();
        item1.setTitle("Тетрадь");
        item1.setPrice(BigDecimal.valueOf(100));

        Item item2 = new Item();
        item2.setTitle("Ручка");
        item2.setPrice(BigDecimal.valueOf(50));

        itemRepository.saveAll(List.of(item1, item2));

        List<Item> items = itemRepository.findAll();

        assertThat(items).hasSize(2);
        assertThat(items)
                .extracting(Item::getTitle)
                .containsExactlyInAnyOrder("Тетрадь", "Ручка");
    }

    @Test
    @DisplayName("Удаление товара по id")
    void deleteById_RemovesItem() {
        Item item = new Item();
        item.setTitle("Стикеры");
        item.setPrice(BigDecimal.valueOf(30));

        Item saved = itemRepository.save(item);
        Long id = saved.getId();

        itemRepository.deleteById(id);

        Optional<Item> found = itemRepository.findById(id);
        assertThat(found).isEmpty();
    }
}
