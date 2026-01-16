package com.fitassist.backend.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fitassist.backend.dto.pojo.AuthorDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * fetched with db (findById) -> mapper -> populated in getComment fetched with db
 * (findAllByThreadIdAndParentCommentNull) -> mapper -> populated in
 * getTopCommentsForThread fetched with db (findCommentHierarchy) -> manually mapped ->
 * populated in getReplies
 *
 * <p>
 * Mapper sets: id, text, createdAt, threadId, parentCommentId, author (id, username)
 * Population sets: author.imageName/imageUrl, likesCount, dislikesCount, liked, disliked
 *
 * <p>
 * replies - null in getComment and getTopCommentsForThread (mapper ignores); only set in
 * getReplies by building hierarchy liked/disliked - when user not authenticated
 * (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CommentResponseDto implements Serializable {

	private LocalDateTime createdAt;

	private Integer id;

	private String text;

	private Integer threadId;

	private Integer parentCommentId;

	private AuthorDto author;

	private List<CommentResponseDto> replies;

	private long likesCount;

	private long dislikesCount;

	private long repliesCount;

	private Boolean liked;

	private Boolean disliked;

}
