package com.fitassist.backend.service.implementation.comment;

import org.springframework.stereotype.Service;
import com.fitassist.backend.dto.pojo.projection.comment.CommentCountsProjection;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.repository.UserCommentRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentPopulationServiceImpl implements CommentPopulationService {

	private final UserCommentRepository userCommentRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public CommentPopulationServiceImpl(UserCommentRepository userCommentRepository, MediaRepository mediaRepository,
			AwsS3Service s3Service) {
		this.userCommentRepository = userCommentRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Override
	public void populate(List<CommentSummaryDto> comments) {
		if (comments.isEmpty())
			return;

		int userId = AuthorizationUtil.getUserId();

		List<Integer> commentIds = comments.stream().map(CommentSummaryDto::getId).toList();

		populateAuthorImagesForSummaries(comments);
		populateCountsAndInteractionsForSummaries(comments, commentIds, userId);
	}

	@Override
	public void populate(CommentResponseDto comment) {
		if (comment == null)
			return;

		int userId = AuthorizationUtil.getUserId();

		populateAuthorImage(comment);
		populateCountsAndInteractions(comment, userId);
	}

	@Override
	public void populateList(List<CommentResponseDto> comments) {
		if (comments.isEmpty())
			return;

		int userId = AuthorizationUtil.getUserId();

		List<Integer> commentIds = comments.stream().map(CommentResponseDto::getId).toList();

		populateAuthorImages(comments);
		populateCountsAndInteractions(comments, commentIds, userId);
	}

	private void populateAuthorImagesForSummaries(List<CommentSummaryDto> comments) {
		List<Integer> authorIds = comments.stream().map(comment -> comment.getAuthor().getId()).toList();

		if (authorIds.isEmpty())
			return;

		Map<Integer, String> authorImageMap = mediaRepository
			.findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
			.stream()
			.collect(Collectors.toMap(Media::getParentId, Media::getImageName));

		comments.forEach(comment -> {
			String imageName = authorImageMap.get(comment.getAuthor().getId());
			if (imageName != null) {
				comment.getAuthor().setImageName(imageName);
				comment.getAuthor().setImageUrl(s3Service.getImage(imageName));
			}
		});
	}

	private void populateCountsAndInteractionsForSummaries(List<CommentSummaryDto> comments, List<Integer> commentIds,
			int userId) {
		Map<Integer, CommentCountsProjection> countsMap = userCommentRepository
			.findCountsAndInteractionsByCommentIds(userId, commentIds)
			.stream()
			.collect(Collectors.toMap(CommentCountsProjection::getCommentId, projection -> projection));

		comments.forEach(comment -> {
			CommentCountsProjection counts = countsMap.get(comment.getId());
			if (counts != null) {
				comment.setLikesCount(counts.likesCount());
				comment.setDislikesCount(counts.dislikesCount());
				comment.setLiked(counts.isLiked());
				comment.setDisliked(counts.isDisliked());
				comment.setRepliesCount(counts.repliesCount());
			}
			else {
				comment.setLikesCount(0L);
				comment.setDislikesCount(0L);
				comment.setLiked(false);
				comment.setDisliked(false);
				comment.setRepliesCount(0L);
			}
		});
	}

	private void populateAuthorImage(CommentResponseDto comment) {
		if (comment.getAuthor() == null)
			return;

		mediaRepository
			.findFirstByParentIdAndParentTypeOrderByIdAsc(comment.getAuthor().getId(), MediaConnectedEntity.USER)
			.ifPresent(media -> {
				comment.getAuthor().setImageName(media.getImageName());
				comment.getAuthor().setImageUrl(s3Service.getImage(media.getImageName()));
			});
	}

	private void populateCountsAndInteractions(CommentResponseDto comment, int userId) {
		List<CommentCountsProjection> results = userCommentRepository.findCountsAndInteractionsByCommentIds(userId,
				List.of(comment.getId()));

		if (results.isEmpty()) {
			comment.setLikesCount(0L);
			comment.setDislikesCount(0L);
			comment.setLiked(false);
			comment.setDisliked(false);
			comment.setRepliesCount(0L);
			return;
		}

		CommentCountsProjection counts = results.getFirst();
		comment.setLikesCount(counts.likesCount());
		comment.setDislikesCount(counts.dislikesCount());
		comment.setLiked(counts.isLiked());
		comment.setDisliked(counts.isDisliked());
		comment.setRepliesCount(counts.repliesCount());
	}

	private void populateAuthorImages(List<CommentResponseDto> comments) {
		List<Integer> authorIds = comments.stream()
			.filter(c -> c.getAuthor() != null)
			.map(comment -> comment.getAuthor().getId())
			.toList();

		if (authorIds.isEmpty())
			return;

		Map<Integer, String> authorImageMap = mediaRepository
			.findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
			.stream()
			.collect(Collectors.toMap(Media::getParentId, Media::getImageName));

		comments.forEach(comment -> {
			if (comment.getAuthor() == null)
				return;
			String imageName = authorImageMap.get(comment.getAuthor().getId());
			if (imageName != null) {
				comment.getAuthor().setImageName(imageName);
				comment.getAuthor().setImageUrl(s3Service.getImage(imageName));
			}
		});
	}

	private void populateCountsAndInteractions(List<CommentResponseDto> comments, List<Integer> commentIds,
			int userId) {
		Map<Integer, CommentCountsProjection> countsMap = userCommentRepository
			.findCountsAndInteractionsByCommentIds(userId, commentIds)
			.stream()
			.collect(Collectors.toMap(CommentCountsProjection::getCommentId, projection -> projection));

		comments.forEach(comment -> {
			CommentCountsProjection counts = countsMap.get(comment.getId());
			if (counts != null) {
				comment.setLikesCount(counts.likesCount());
				comment.setDislikesCount(counts.dislikesCount());
				comment.setLiked(counts.isLiked());
				comment.setDisliked(counts.isDisliked());
				comment.setRepliesCount(counts.repliesCount());
			}
			else {
				comment.setLikesCount(0L);
				comment.setDislikesCount(0L);
				comment.setLiked(false);
				comment.setDisliked(false);
				comment.setRepliesCount(0L);
			}
		});
	}

}
