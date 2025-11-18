package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c FROM Comment c
             WHERE c.item.id = :itemId
             ORDER BY c.created DESC
            """)
    Page<CommentDto> findByItemId(Long itemId, Pageable pageable);

    List<Comment> findAllByItem(Item item);
}
