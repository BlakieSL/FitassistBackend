package source.code.service.implementation.comment;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.comment.CommentCountsProjection;
import source.code.dto.pojo.projection.comment.CommentRepliesCountProjection;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.model.media.Media;
import source.code.repository.CommentRepository;
import source.code.repository.MediaRepository;
import source.code.repository.UserCommentRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.comment.CommentPopulationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentPopulationServiceImpl implements CommentPopulationService {
    private final UserCommentRepository userCommentRepository;
    private final CommentRepository commentRepository;
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public CommentPopulationServiceImpl(
            UserCommentRepository userCommentRepository,
            CommentRepository commentRepository,
            MediaRepository mediaRepository,
            AwsS3Service s3Service) {
        this.userCommentRepository = userCommentRepository;
        this.commentRepository = commentRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(List<CommentSummaryDto> comments) {
        if (comments.isEmpty()) return;

        List<Integer> commentIds = comments.stream()
                .map(CommentSummaryDto::getId)
                .toList();

        populateAuthorImages(comments);
        populateCounts(comments, commentIds);
        populateRepliesCounts(comments, commentIds);
    }

    private void populateAuthorImages(List<CommentSummaryDto> comments) {
        List<Integer> authorIds = comments.stream()
                .map(CommentSummaryDto::getAuthorId)
                .toList();

        if (authorIds.isEmpty()) return;

        Map<Integer, String> authorImageMap = mediaRepository
                .findFirstMediaByParentIds(authorIds, MediaConnectedEntity.USER)
                .stream()
                .collect(Collectors.toMap(Media::getParentId, Media::getImageName));

        comments.forEach(comment -> {
            String imageName = authorImageMap.get(comment.getAuthorId());
            if (imageName != null) {
                comment.setAuthorImageName(imageName);
                comment.setAuthorImageUrl(s3Service.getImage(imageName));
            }
        });
    }

    private void populateCounts(List<CommentSummaryDto> comments, List<Integer> commentIds) {
        Map<Integer, CommentCountsProjection> countsMap = userCommentRepository
                .findCountsByCommentIds(commentIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentCountsProjection::getCommentId,
                        projection -> projection
                ));

        comments.forEach(comment -> {
            CommentCountsProjection counts = countsMap.get(comment.getId());
            if (counts != null) {
                comment.setLikesCount(counts.getLikesCount());
                comment.setDislikesCount(counts.getDislikesCount());
            }
        });
    }

    private void populateRepliesCounts(List<CommentSummaryDto> comments, List<Integer> commentIds) {
        Map<Integer, Long> repliesCountMap = commentRepository
                .findRepliesCountsByCommentIds(commentIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentRepliesCountProjection::getCommentId,
                        CommentRepliesCountProjection::getRepliesCount
                ));

        comments.forEach(comment -> {
            Long repliesCount = repliesCountMap.get(comment.getId());
            comment.setRepliesCount(repliesCount);
        });
    }
}
