package ru.practicum.onlineStore.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.repository.ItemRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    @DisplayName("findAll возвращает список всех товаров")
    void findAll_ReturnsAllItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setTitle("Item 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Item 2");

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        List<Item> items = itemService.findAll();

        assertThat(items).hasSize(2).contains(item1, item2);
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById возвращает товар по ID")
    void findById_ReturnsItem() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("Item 1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<Item> result = itemService.findById(1L);

        assertThat(result).isPresent().contains(item);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("save сохраняет товар")
    void save_SavesItem() {
        Item item = new Item();
        item.setTitle("New Item");

        when(itemRepository.save(item)).thenReturn(item);

        Item saved = itemService.save(item);

        assertThat(saved).isEqualTo(item);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("delete удаляет товар по ID")
    void delete_DeletesItem() {
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteById(itemId);

        itemService.delete(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }
}
