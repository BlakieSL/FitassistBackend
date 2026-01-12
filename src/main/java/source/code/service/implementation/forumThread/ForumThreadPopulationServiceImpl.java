package source.code.service.implementation.forumThread;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.thread.ForumThreadCountsProjection;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.utils.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.MediaRepository;
import source.code.repository.UserThreadRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.thread.ForumThreadPopulationService;

import java.util.List;
import java.util.Map;
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
		if (threads.isEmpty())
			return;

		List<Integer> threadIds = threads.stream().map(ForumThreadSummaryDto::getId).toList();

		fetchAndPopulateAuthorImages(threads);
		fetchAndPopulateUserInteractionAndCounts(threads, threadIds);
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

		if (result == null)
			return;

		thread.setSaved(result.isSaved());
		thread.setSavesCount(result.savesCount());
		thread.setCommentsCount(result.commentsCount());
	}

	private void fetchAndPopulateAuthorImages(List<ForumThreadSummaryDto> threads) {
		List<Integer> authorIds = threads.stream().map(thread -> thread.getAuthor().getId()).toList();

		if (authorIds.isEmpty())
			return;

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
			.collect(Collectors.toMap(ForumThreadCountsProjection::getThreadId, projection -> projection));

		threads.forEach(thread -> {
			ForumThreadCountsProjection counts = countsMap.get(thread.getId());
			if (counts != null) {
				thread.setSavesCount(counts.savesCount());
				thread.setCommentsCount(counts.commentsCount());
				thread.setSaved(counts.isSaved());
			}
		});
	}

}
