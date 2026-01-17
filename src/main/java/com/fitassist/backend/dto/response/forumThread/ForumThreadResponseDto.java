package com.fitassist.backend.dto.response.forumThread;

import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.response.category.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * fetched with db (findById) -> mapper -> populated in createForumThread and
 * getForumThread
 *
 * <p>
 * Mapper sets: id, title, text, createdAt, views, category, author (id, username)
 * Population sets: author.imageName/imageUrl, savesCount, commentsCount, saved
 *
 * <p>
 * saved - when user not authenticated (userId=-1), always false since query matches on
 * userId
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForumThreadResponseDto implements Serializable {

	private Integer id;

	private String title;

	private LocalDateTime createdAt;

	private String text;

	private long views;

	private long savesCount;

	private long commentsCount;

	private CategoryResponseDto category;

	private AuthorDto author;

	private boolean saved;

}
