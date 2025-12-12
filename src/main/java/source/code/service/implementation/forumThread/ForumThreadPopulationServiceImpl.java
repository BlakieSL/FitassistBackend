package source.code.service.implementation.forumThread;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.thread.ForumThreadCommentsCountProjection;
import source.code.dto.pojo.projection.thread.ForumThreadCountsProjection;
import source.code.dto.pojo.projection.thread.ForumThreadUserInteractionProjection;
import source.code.dto.response.forumThread.ForumThreadResponseDto;
import source.code.dto.response.forumThread.ForumThreadSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.media.Media;
import source.code.repository.ForumThreadRepository;
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
    private final ForumThreadRepository forumThreadRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public ForumThreadPopulationServiceImpl(
            UserThreadRepository userThreadRepository,
            ForumThreadRepository forumThreadRepository,
            MediaRepository mediaRepository,
            AwsS3Service s3Service) {
        this.userThreadRepository = userThreadRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(List<ForumThreadSummaryDto> threads) {
        if (threads.isEmpty()) return;

        List<Integer> threadIds = threads.stream()
                .map(ForumThreadSummaryDto::getId)
                .toList();

        populateAuthorImages(threads);
        populateSavesCounts(threads, threadIds);
        populateCommentsCounts(threads, threadIds);
    }

    @Override
    public void populate(ForumThreadResponseDto thread) {
        int userId = AuthorizationUtil.getUserId();

        populateAuthorImage(thread);
        populateUserInteractionAndCounts(thread, userId);
    }

    private void populateAuthorImage(ForumThreadResponseDto thread) {
        mediaRepository.findFirstByParentIdAndParentTypeOrderByIdAsc(thread.getAuthorId(), MediaConnectedEntity.USER)
                .ifPresent(media -> {
                    thread.setAuthorImageName(media.getImageName());
                    thread.setAuthorImageUrl(s3Service.getImage(media.getImageName()));
                });
    }

    private void populateUserInteractionAndCounts(ForumThreadResponseDto thread, int userId) {
        ForumThreadUserInteractionProjection result = userThreadRepository
                .findUserInteractionAndCounts(userId, thread.getId());

        if (result == null) return;

        thread.setSaved(result.isSaved());
        thread.setSavesCount(result.savesCount());
        thread.setCommentsCount(result.commentsCount());
    }

    private void populateAuthorImages(List<ForumThreadSummaryDto> threads) {
        List<Integer> authorIds = threads.stream()
                .map(ForumThreadSummaryDto::getAuthorId)
                .toList();

        if (authorIds.isEmpty()) return;

        Map<Integer, String> authorImageMap = mediaRepository
                .findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
                .stream()
                .collect(Collectors.toMap(Media::getParentId, Media::getImageName));

        threads.forEach(thread -> {
            String imageName = authorImageMap.get(thread.getAuthorId());
            if (imageName != null) {
                thread.setAuthorImageName(imageName);
                thread.setAuthorImageUrl(s3Service.getImage(imageName));
            }
        });
    }

    private void populateSavesCounts(List<ForumThreadSummaryDto> threads, List<Integer> threadIds) {
        Map<Integer, Long> savesCountMap = userThreadRepository
                .findSavesCountsByThreadIds(threadIds)
                .stream()
                .collect(Collectors.toMap(
                        ForumThreadCountsProjection::getThreadId,
                        ForumThreadCountsProjection::getSavesCount
                ));

        threads.forEach(thread -> {
            thread.setSavesCount(savesCountMap.getOrDefault(thread.getId(), 0L));
        });
    }

    private void populateCommentsCounts(List<ForumThreadSummaryDto> threads, List<Integer> threadIds) {
        Map<Integer, Long> commentsCountMap = forumThreadRepository
                .findCommentsCountsByThreadIds(threadIds)
                .stream()
                .collect(Collectors.toMap(
                        ForumThreadCommentsCountProjection::getThreadId,
                        ForumThreadCommentsCountProjection::getCommentsCount
                ));

        threads.forEach(thread -> {
            thread.setCommentsCount(commentsCountMap.getOrDefault(thread.getId(), 0L));
        });
    }
}
