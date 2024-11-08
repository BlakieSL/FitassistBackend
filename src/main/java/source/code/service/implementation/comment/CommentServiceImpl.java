package source.code.service.implementation.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.auth.CustomAuthenticationToken;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.User.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.forum.Comment;
import source.code.repository.CommentRepository;
import source.code.service.declaration.comment.CommentService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final CommentMapper commentMapper;
    private final RepositoryHelper repositoryHelper;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(JsonPatchService jsonPatchService,
                              ValidationService validationService,
                              CommentMapper commentMapper,
                              RepositoryHelper repositoryHelper,
                              CommentRepository commentRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.commentMapper = commentMapper;
        this.repositoryHelper = repositoryHelper;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto createDto) {
        int userId = AuthorizationUtil.getUserId();
        Comment mapped = commentMapper.toEntity(createDto, userId);
        Comment saved = commentRepository.save(mapped);
        return commentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    @IsCommentOwnerOrAdmin
    public void updateComment(int commentId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Comment comment = find(commentId);
        CommentUpdateDto patched = applyPatchToComment(comment, patch);

        validationService.validate(patched);
        commentMapper.update(comment, patched);
        Comment saved = commentRepository.save(comment);
    }

    @Override
    @Transactional
    @IsCommentOwnerOrAdmin
    public void deleteComment(int commentId) {
        Comment comment = find(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public CommentResponseDto getComment(int commentId) {
        Comment comment = find(commentId);
        return commentMapper.toResponseDto(comment);
    }

    @Override
    public long countCommentsForThread(int threadId) {
        return commentRepository.countAllByThreadId(threadId);
    }

    @Override
    public List<CommentResponseDto> getTopCommentsForThread(int threadId) {
        return commentRepository.findAllByThreadIdAndParentCommentNull(threadId).stream()
                .map(commentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<CommentResponseDto> getReplies(int commentId) {
        List<Comment> directReplies = commentRepository.findAllByParentCommentId(commentId);

        return directReplies.stream()
                .map(this::buildCommentHierarchy)
                .toList();
    }

    private CommentResponseDto buildCommentHierarchy(Comment comment) {
        CommentResponseDto commentDto = commentMapper.toResponseDto(comment);

        List<Comment> replies = commentRepository.findAllByParentCommentId(comment.getId());

        List<CommentResponseDto> replyDtos = replies.stream()
                .map(this::buildCommentHierarchy)
                .toList();

        commentDto.setReplies(replyDtos);

        return commentDto;
    }

    public CommentUpdateDto applyPatchToComment(Comment comment, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        CommentResponseDto responseDto = commentMapper.toResponseDto(comment);
        return jsonPatchService.applyPatch(patch, responseDto, CommentUpdateDto.class);
    }

    private Comment find(int commentId) {
        return repositoryHelper.find(commentRepository, Comment.class, commentId);
    }

    public boolean isCommentOwnerOrAdmin(int commentId)  {
        Comment comment = find(commentId);
        return AuthorizationUtil.isOwnerOrAdmin(comment.getUser().getId());
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("@commentServiceImpl.isCommentOwnerOrAdmin(#commentId)")
    public @interface IsCommentOwnerOrAdmin {}
}
