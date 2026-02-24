package com.fitassist.backend.service.implementation.comment;

import tools.jackson.core.JacksonException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.dto.pojo.AuthorDto;
import com.fitassist.backend.dto.request.comment.CommentCreateDto;
import com.fitassist.backend.dto.request.comment.CommentUpdateDto;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.response.comment.CommentAncestryDto;
import com.fitassist.backend.dto.response.comment.CommentResponseDto;
import com.fitassist.backend.dto.response.comment.CommentSummaryDto;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.comment.CommentMapper;
import com.fitassist.backend.mapper.comment.CommentMappingContext;
import com.fitassist.backend.model.thread.Comment;
import com.fitassist.backend.model.thread.ForumThread;
import com.fitassist.backend.model.user.User;
import com.fitassist.backend.repository.CommentRepository;
import com.fitassist.backend.repository.ForumThreadRepository;
import com.fitassist.backend.repository.UserRepository;
import com.fitassist.backend.service.declaration.comment.CommentPopulationService;
import com.fitassist.backend.service.declaration.comment.CommentService;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.CommentSpecification;
import jakarta.json.JsonMergePatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final CommentMapper commentMapper;

	private final CommentRepository commentRepository;

	private final ForumThreadRepository forumThreadRepository;

	private final UserRepository userRepository;

	private final SpecificationDependencies dependencies;

	private final CommentPopulationService commentPopulationService;

	public CommentServiceImpl(JsonPatchService jsonPatchService, ValidationService validationService,
			CommentMapper commentMapper, CommentRepository commentRepository,
			ForumThreadRepository forumThreadRepository, UserRepository userRepository,
			SpecificationDependencies dependencies, CommentPopulationService commentPopulationService) {
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.commentMapper = commentMapper;
		this.commentRepository = commentRepository;
		this.forumThreadRepository = forumThreadRepository;
		this.userRepository = userRepository;
		this.dependencies = dependencies;
		this.commentPopulationService = commentPopulationService;
	}

	@Override
	@Transactional
	public CommentResponseDto createComment(CommentCreateDto createDto) {
		CommentMappingContext context = prepareCreateContext(createDto);
		Comment mapped = commentMapper.toEntity(createDto, context);
		Comment saved = commentRepository.save(mapped);

		commentRepository.flush();

		return findAndMap(saved.getId());
	}

	private CommentResponseDto findAndMap(int commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RecordNotFoundException(Comment.class, commentId));
		CommentResponseDto dto = commentMapper.toResponse(comment);
		commentPopulationService.populate(dto);

		return dto;
	}

	private CommentMappingContext prepareCreateContext(CommentCreateDto createDto) {
		int userId = AuthorizationUtil.getUserId();
		User user = findUser(userId);
		ForumThread thread = findThread(createDto.getThreadId());
		Comment parentComment = findParentComment(createDto.getParentCommentId());

		return new CommentMappingContext(user, thread, parentComment);
	}

	private User findUser(int userId) {
		return userRepository.findById(userId).orElseThrow(() -> RecordNotFoundException.of(User.class, userId));
	}

	private ForumThread findThread(int threadId) {
		return forumThreadRepository.findById(threadId)
			.orElseThrow(() -> RecordNotFoundException.of(ForumThread.class, threadId));
	}

	private Comment findParentComment(Integer parentCommentId) {
		if (parentCommentId == null) {
			return null;
		}
		return commentRepository.findById(parentCommentId)
			.orElseThrow(() -> RecordNotFoundException.of(Comment.class, parentCommentId));
	}

	@Override
	@Transactional
	public void updateComment(int commentId, JsonMergePatch patch) throws JacksonException {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new RecordNotFoundException(Comment.class, commentId));

		CommentUpdateDto patched = applyPatchToComment(patch);

		validationService.validate(patched);
		commentMapper.update(comment, patched);
		commentRepository.save(comment);
	}

	private CommentUpdateDto applyPatchToComment(JsonMergePatch patch) throws JacksonException {
		return jsonPatchService.createFromPatch(patch, CommentUpdateDto.class);
	}

	@Override
	@Transactional
	public void deleteComment(int commentId) {
		commentRepository.deleteById(commentId);
	}

	@Override
	public CommentResponseDto getComment(int commentId) {
		return findAndMap(commentId);
	}

	@Override
	public Page<CommentSummaryDto> getFilteredComments(FilterDto filter, Pageable pageable) {
		SpecificationFactory<Comment> commentFactory = CommentSpecification::new;
		SpecificationBuilder<Comment> specificationBuilder = SpecificationBuilder.of(filter, commentFactory,
				dependencies);
		Specification<Comment> specification = specificationBuilder.build();

		Page<Comment> commentPage = commentRepository.findAll(specification, pageable);
		List<CommentSummaryDto> summaries = commentPage.getContent().stream().map(commentMapper::toSummary).toList();
		commentPopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, commentPage.getTotalElements());
	}

	@Override
	public Page<CommentResponseDto> getTopCommentsForThread(int threadId, Pageable pageable) {
		Sort.Order sortOrder = pageable.getSort().isSorted() ? pageable.getSort().stream().toList().getFirst() : null;

		List<Comment> comments;
		long total;

		if (sortOrder != null && "likesCount".equals(sortOrder.getProperty())) {
			List<Integer> ids = commentRepository.findTopCommentIdsSortedByLikesCount(threadId,
					sortOrder.getDirection().name(), pageable.getPageSize(), (int) pageable.getOffset());
			comments = commentRepository.findAllByIds(ids);
			total = commentRepository.countTopCommentsByThreadId(threadId);
		}
		else {
			Page<Comment> commentPage = commentRepository.findAllByThreadIdAndParentCommentNull(threadId, pageable);
			comments = commentPage.getContent();
			total = commentPage.getTotalElements();
		}

		List<CommentResponseDto> dtos = comments.stream().map(commentMapper::toResponse).toList();
		commentPopulationService.populateList(dtos);

		return new PageImpl<>(dtos, pageable, total);
	}

	@Override
	public List<CommentResponseDto> getReplies(int commentId) {
		List<Object[]> results = commentRepository.findCommentHierarchy(commentId);

		if (results.isEmpty()) {
			return List.of();
		}

		Map<Integer, CommentResponseDto> dtoMap = new LinkedHashMap<>();
		List<CommentResponseDto> dtoList = new ArrayList<>();

		for (Object[] row : results) {
			CommentResponseDto dto = new CommentResponseDto();
			dto.setId((Integer) row[0]);
			dto.setText((String) row[1]);
			dto.setThreadId((Integer) row[2]);
			dto.setParentCommentId((Integer) row[4]);
			dto.setCreatedAt(row[5] instanceof Timestamp ts ? ts.toLocalDateTime() : (LocalDateTime) row[5]);
			dto.setReplies(new ArrayList<>());

			AuthorDto author = new AuthorDto();
			author.setId((Integer) row[3]);
			author.setUsername((String) row[6]);
			dto.setAuthor(author);

			dtoMap.put(dto.getId(), dto);
			dtoList.add(dto);
		}

		commentPopulationService.populateList(dtoList);

		for (CommentResponseDto dto : dtoMap.values()) {
			Integer parentId = dto.getParentCommentId();
			if (parentId != null && dtoMap.containsKey(parentId)) {
				dtoMap.get(parentId).getReplies().add(dto);
			}
		}

		return dtoMap.values()
			.stream()
			.filter(dto -> dto.getParentCommentId() != null && dto.getParentCommentId() == commentId)
			.toList();
	}

	@Override
	public CommentAncestryDto getCommentAncestry(int commentId) {
		List<Object[]> results = commentRepository.findCommentAncestry(commentId);

		if (results.isEmpty()) {
			throw new RecordNotFoundException(Comment.class, commentId);
		}

		Integer threadId = null;
		List<Integer> ancestorIds = new ArrayList<>();

		for (Object[] row : results) {
			ancestorIds.add((Integer) row[0]);
			threadId = (Integer) row[1];
		}

		return new CommentAncestryDto(threadId, ancestorIds);
	}

}
