package ru.practicum.onlineStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.onlineStore.model.Item;
import ru.practicum.onlineStore.repository.ItemRepository;


@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Flux<Item> findAll() {
        return itemRepository.findAll();
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Mono<Item> save(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Void> delete(Long id) {
        return itemRepository.deleteById(id);
    }

}
