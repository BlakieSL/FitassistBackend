package source.code.service.implementation.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import source.code.dto.request.comment.CommentCreateDto;
import source.code.dto.request.comment.CommentUpdateDto;
import source.code.dto.response.comment.CommentResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.comment.CommentMapper;
import source.code.model.thread.Comment;
import source.code.repository.CommentRepository;
import source.code.service.declaration.comment.CommentService;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {
    private final JsonPatchService jsonPatchService;
    private final ValidationService validationService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(JsonPatchService jsonPatchService,
                              ValidationService validationService,
                              CommentMapper commentMapper,
                              CommentRepository commentRepository) {
        this.jsonPatchService = jsonPatchService;
        this.validationService = validationService;
        this.commentMapper = commentMapper;
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
    public void updateComment(int commentId, JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        Comment comment = commentRepository.findByIdWithoutAssociations(commentId)
                .orElseThrow(() -> new RecordNotFoundException(Comment.class, commentId));

        CommentUpdateDto patched = applyPatchToComment(patch);

        validationService.validate(patched);
        commentMapper.update(comment, patched);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(int commentId) {
        commentRepository.deleteCommentDirectly(commentId);
    }

    @Override
    public CommentResponseDto getComment(int commentId) {
        Comment comment = commentRepository.findByIdWithoutAssociations(commentId)
                .orElseThrow(() -> new RecordNotFoundException(Comment.class, commentId));
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

    public List<CommentResponseDto> getReplies(int commentId) {
        List<Object[]> results = commentRepository.findCommentHierarchy(commentId);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, CommentResponseDto> dtoMap = new HashMap<>();

        for (var row : results) {
            CommentResponseDto dto = new CommentResponseDto();
            dto.setId((Integer) row[0]);
            dto.setText((String) row[1]);
            dto.setThreadId((Integer) row[2]);
            dto.setUserId((Integer) row[3]);
            dto.setParentCommentId((Integer) row[4]);
            dto.setReplies(new ArrayList<>());
            dtoMap.put(dto.getId(), dto);
        }

        for (var dto : dtoMap.values()) {
            Integer parentId = dto.getParentCommentId();
            if (parentId != null && dtoMap.containsKey(parentId)) {
                dtoMap.get(parentId).getReplies().add(dto);
            }
        }

        return dtoMap.values().stream()
                .filter(dto -> commentId == dto.getParentCommentId())
                .sorted(Comparator.comparing(CommentResponseDto::getId))
                .toList();
    }

    public CommentUpdateDto applyPatchToComment(JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException
    {
        return jsonPatchService.createFromPatch(patch, CommentUpdateDto.class);
    }
}
