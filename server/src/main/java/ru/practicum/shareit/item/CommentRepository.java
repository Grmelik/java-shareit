package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT c FROM Comment c
             WHERE c.item.id = :itemId
             ORDER BY c.created DESC
            """)
    Page<Comment> findByItemId(Long itemId, Pageable pageable);

    @Query("""
            SELECT c FROM Comment c
              LEFT JOIN FETCH c.author
             WHERE c.item.id = :itemId
             ORDER BY c.created DESC
            """)
    List<Comment> findByItemIdWithAuthor(Long itemId);

}