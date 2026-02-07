package com.fitassist.backend.service.implementation.forumthread;

import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.projection.thread.ForumThreadCountsProjection;
import com.fitassist.backend.dto.response.forumThread.ForumThreadResponseDto;
import com.fitassist.backend.dto.response.forumThread.ForumThreadSummaryDto;
import com.fitassist.backend.model.media.Media;
import com.fitassist.backend.model.media.MediaConnectedEntity;
import com.fitassist.backend.repository.MediaRepository;
import com.fitassist.backend.repository.UserThreadRepository;
import com.fitassist.backend.service.declaration.aws.AwsS3Service;
import com.fitassist.backend.service.declaration.thread.ForumThreadPopulationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ForumThreadPopulationServiceImpl implements ForumThreadPopulationService {

	private final UserThreadRepository userThreadRepository;

	private final MediaRepository mediaRepository;

	private final AwsS3Service s3Service;

	public ForumThreadPopulationServiceImpl(UserThreadRepository userThreadRepository, MediaRepository mediaRepository,
			AwsS3Service s3Service) {
		this.userThreadRepository = userThreadRepository;
		this.mediaRepository = mediaRepository;
		this.s3Service = s3Service;
	}

	@Override
	public void populate(List<ForumThreadSummaryDto> threads) {
		if (threads.isEmpty()) {
			return;
		}

		List<Integer> threadIds = threads.stream().map(ForumThreadSummaryDto::getId).toList();

		fetchAndPopulateAuthorImages(threads);
		fetchAndPopulateUserInteractionAndCounts(threads, threadIds);
	}

	private void fetchAndPopulateAuthorImages(List<ForumThreadSummaryDto> threads) {
		List<Integer> authorIds = threads.stream().map(thread -> thread.getAuthor().getId()).toList();

		if (authorIds.isEmpty()) {
			return;
		}

		Map<Integer, String> authorImageMap = mediaRepository
			.findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
			.stream()
			.collect(Collectors.toMap(Media::getParentId, Media::getImageName));

		threads.forEach(thread -> {
			String imageName = authorImageMap.get(thread.getAuthor().getId());
			if (imageName != null) {
				thread.getAuthor().setImageName(imageName);
				thread.getAuthor().setImageUrl(s3Service.getImage(imageName));
			}
		});
	}

	private void fetchAndPopulateUserInteractionAndCounts(List<ForumThreadSummaryDto> threads,
			List<Integer> threadIds) {
		int userId = AuthorizationUtil.getUserId();

		Map<Integer, ForumThreadCountsProjection> countsMap = userThreadRepository
			.findCountsAndInteractionsByThreadIds(userId, threadIds)
			.stream()
			.collect(Collectors.toMap(ForumThreadCountsProjection::getThreadId, Function.identity()));

		threads.forEach(thread -> {
			ForumThreadCountsProjection counts = countsMap.get(thread.getId());
			if (counts != null) {
				thread.setSavesCount(counts.savesCount());
				thread.setCommentsCount(counts.commentsCount());
				thread.setSaved(counts.isSaved());
			}
		});
	}

	@Override
	public void populate(ForumThreadResponseDto thread) {
		int userId = AuthorizationUtil.getUserId();

		fetchAndPopulateAuthorImage(thread);
		fetchAndPopulateUserInteractionAndCounts(thread, userId);
	}

	private void fetchAndPopulateAuthorImage(ForumThreadResponseDto thread) {
		mediaRepository
			.findFirstByParentIdAndParentTypeOrderByIdAsc(thread.getAuthor().getId(), MediaConnectedEntity.USER)
			.ifPresent(media -> {
				thread.getAuthor().setImageName(media.getImageName());
				thread.getAuthor().setImageUrl(s3Service.getImage(media.getImageName()));
			});
	}

	private void fetchAndPopulateUserInteractionAndCounts(ForumThreadResponseDto thread, int userId) {
		ForumThreadCountsProjection result = userThreadRepository.findCountsAndInteractionsByThreadId(userId,
				thread.getId());

		if (result == null) {
			return;
		}

		thread.setSaved(result.isSaved());
		thread.setSavesCount(result.savesCount());
		thread.setCommentsCount(result.commentsCount());
	}

}
