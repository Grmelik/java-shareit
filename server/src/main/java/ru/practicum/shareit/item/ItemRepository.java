package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("""
            SELECT i FROM Item i
             WHERE i.available = true
               AND i.name ILIKE %:text%
                OR i.description ILIKE %:text%
            """)
    Page<Item> search(String text, Pageable pageable);

    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("""
            SELECT i FROM Item i
              LEFT JOIN FETCH i.owner
              LEFT JOIN FETCH i.request
             WHERE i.id = :itemId
           """)
    Optional<Item> findItemWithDependencies(Long itemId);

    @Query("""
            SELECT i FROM Item i
              LEFT JOIN FETCH i.owner
              LEFT JOIN FETCH i.request
             WHERE i.id IN :itemIds
            """)
    List<Item> findItemsWithDependenciesByIds(List<Long> itemIds);

    @Query("""
            SELECT i FROM Item i
              LEFT JOIN FETCH i.owner
              LEFT JOIN FETCH i.request
             WHERE i.request.id = :requestId
            """)
    List<Item> findItemsWithDependenciesByRequestId(Long requestId);
}
