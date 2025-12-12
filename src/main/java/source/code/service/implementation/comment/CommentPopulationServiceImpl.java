package source.code.service.implementation.comment;

import org.springframework.stereotype.Service;
import source.code.dto.pojo.projection.comment.CommentCountsProjection;
import source.code.dto.response.comment.CommentSummaryDto;
import source.code.helper.Enum.model.MediaConnectedEntity;
import source.code.helper.user.AuthorizationUtil;
import source.code.model.media.Media;
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
    private final MediaRepository mediaRepository;
    private final AwsS3Service s3Service;

    public CommentPopulationServiceImpl(
            UserCommentRepository userCommentRepository,
            MediaRepository mediaRepository,
            AwsS3Service s3Service) {
        this.userCommentRepository = userCommentRepository;
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    @Override
    public void populate(List<CommentSummaryDto> comments) {
        if (comments.isEmpty()) return;

        int userId = AuthorizationUtil.getUserId();

        List<Integer> commentIds = comments.stream()
                .map(CommentSummaryDto::getId)
                .toList();

        populateAuthorImages(comments);
        populateCounts(comments, commentIds, userId);
    }

    private void populateAuthorImages(List<CommentSummaryDto> comments) {
        List<Integer> authorIds = comments.stream()
                .map(comment -> comment.getAuthor().getId())
                .toList();

        if (authorIds.isEmpty()) return;

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

    private void populateCounts(List<CommentSummaryDto> comments, List<Integer> commentIds, int userId) {
        Map<Integer, CommentCountsProjection> countsMap = userCommentRepository
                .findCountsByCommentIds(userId, commentIds)
                .stream()
                .collect(Collectors.toMap(
                        CommentCountsProjection::getCommentId,
                        projection -> projection
                ));

        comments.forEach(comment -> {
            CommentCountsProjection counts = countsMap.get(comment.getId());
            if (counts != null) {
                comment.setLikesCount(counts.likesCount());
                comment.setDislikesCount(counts.dislikesCount());
                comment.setLiked(counts.isLiked());
                comment.setDisliked(counts.isDisliked());
                comment.setRepliesCount(counts.repliesCount());
            } else {
                comment.setLikesCount(0L);
                comment.setDislikesCount(0L);
                comment.setLiked(false);
                comment.setDisliked(false);
                comment.setRepliesCount(0L);
            }
        });
    }
}
