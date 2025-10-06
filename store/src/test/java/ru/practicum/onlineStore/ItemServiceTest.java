package ru.practicum.onlineStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.repository.ItemRepository;
import ru.practicum.onlineStore.service.ItemService;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Кружка");
        item1.setPrice(BigDecimal.valueOf(500));

        item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Футболка");
        item2.setPrice(BigDecimal.valueOf(1200));
    }

    @Test
    @DisplayName("findAll возвращает все товары")
    void findAll_ReturnsAllItems() {
        when(itemRepository.findAll()).thenReturn(Flux.fromIterable(List.of(item1, item2)));

        StepVerifier.create(itemService.findAll())
                .expectNext(item1)
                .expectNext(item2)
                .verifyComplete();

        verify(itemRepository).findAll();
    }

    @Test
    @DisplayName("findById возвращает товар по id")
    void findById_ReturnsItem() {
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));

        StepVerifier.create(itemService.findById(1L))
                .expectNext(item1)
                .verifyComplete();

        verify(itemRepository).findById(1L);
    }

    @Test
    @DisplayName("save сохраняет товар")
    void save_SavesItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item1));

        StepVerifier.create(itemService.save(item1))
                .expectNext(item1)
                .verifyComplete();

        verify(itemRepository).save(item1);
    }

    @Test
    @DisplayName("delete удаляет товар по id")
    void delete_DeletesItem() {
        when(itemRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(itemService.delete(1L))
                .verifyComplete();

        verify(itemRepository).deleteById(1L);
    }
}
