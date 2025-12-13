package source.code.dto.response.forumThread;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredForumThreads
 * fetched with db (UserThreadRepository.findAllByUserId) -> mapper -> populated in UserThreadService.getAllFromUser
 *
 * Mapper sets: id, title, text, createdAt, views, category, author (id, username)
 * Population sets: author.imageName/imageUrl, savesCount, commentsCount, saved
 *
 * interactedWithAt - only set in UserThreadService.getAllFromUser
 * saved - when user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThreadSummaryDto implements BaseUserEntity, Serializable {
    private LocalDateTime createdAt;
    private Integer id;
    private String title;
    private String text;
    private CategoryResponseDto category;

    private AuthorDto author;

    private LocalDateTime interactionCreatedAt;

    private long views;
    private long savesCount;
    private long commentsCount;

    private Boolean saved;
}
