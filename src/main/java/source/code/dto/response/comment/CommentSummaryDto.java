package source.code.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.dto.pojo.AuthorDto;
import source.code.helper.BaseUserEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * fetched with db (findAll) -> mapper -> populated in getFilteredComments fetched with db
 * (UserCommentRepository.findAllByUserIdAndType) -> mapper -> populated in
 * UserCommentService.getAllFromUser
 *
 * <p>
 * Mapper sets: id, text, createdAt, author (id, username) Population sets:
 * author.imageName/imageUrl, likesCount, dislikesCount, repliesCount, liked, disliked
 *
 * <p>
 * interactedWithAt - only set in UserCommentService.getAllFromUser liked/disliked - when
 * user not authenticated (userId=-1), always false since query matches on userId
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentSummaryDto implements BaseUserEntity, Serializable {

	private LocalDateTime createdAt;

	private Integer id;

	private String text;

	private Integer threadId;

	private AuthorDto author;

	private LocalDateTime interactionCreatedAt;

	private long likesCount;

	private long dislikesCount;

	private long repliesCount;

	private Boolean liked;

	private Boolean disliked;

}
