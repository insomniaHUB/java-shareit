package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final ItemMapper itemMapper;

    public Comment toComment(CommentDto dto, User author, Item item, LocalDateTime created) {
        return Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .item(itemMapper.toItemDto(comment.getItem()))
                .created(comment.getCreated())
                .build();
    }
}
