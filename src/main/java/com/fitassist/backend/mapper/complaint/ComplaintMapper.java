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

	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "discriminatorValue", source = "discriminatorValue")
	@Mapping(target = "associatedId", source = "associatedId")
	@Mapping(target = "imageUrls", ignore = true)
	public abstract ComplaintResponseDto toResponse(ComplaintBase complaint);

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

}
