package com.fitassist.backend.mapper.complaint;

import com.fitassist.backend.dto.request.complaint.ComplaintCreateDto;
import com.fitassist.backend.dto.response.comment.ComplaintResponseDto;
import com.fitassist.backend.model.complaint.CommentComplaint;
import com.fitassist.backend.model.complaint.ComplaintBase;
import com.fitassist.backend.model.complaint.ThreadComplaint;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ComplaintMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "comment", expression = "java(context.getComment())")
	@Mapping(target = "mediaList", ignore = true)
	public abstract CommentComplaint toCommentComplaint(ComplaintCreateDto createDto,
			@Context ComplaintMappingContext context);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "user", expression = "java(context.getUser())")
	@Mapping(target = "thread", expression = "java(context.getThread())")
	@Mapping(target = "mediaList", ignore = true)
	public abstract ThreadComplaint toThreadComplaint(ComplaintCreateDto createDto,
			@Context ComplaintMappingContext context);

	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "discriminatorValue", expression = "java(getDiscriminatorValue(complaint))")
	@Mapping(target = "associatedId", expression = "java(getAssociatedId(complaint))")
	@Mapping(target = "imageUrls", ignore = true)
	public abstract ComplaintResponseDto toResponseDto(ComplaintBase complaint);

	protected String getDiscriminatorValue(ComplaintBase complaint) {
		if (complaint instanceof CommentComplaint) {
			return "COMMENT_COMPLAINT";
		}
		else if (complaint instanceof ThreadComplaint) {
			return "THREAD_COMPLAINT";
		}
		return null;
	}

	protected Integer getAssociatedId(ComplaintBase complaint) {
		if (complaint instanceof CommentComplaint) {
			return ((CommentComplaint) complaint).getComment() != null
					? ((CommentComplaint) complaint).getComment().getId() : null;
		}
		else if (complaint instanceof ThreadComplaint) {
			return ((ThreadComplaint) complaint).getThread() != null ? ((ThreadComplaint) complaint).getThread().getId()
					: null;
		}
		return null;
	}

}
