package com.fitassist.backend.dto.request.filter;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.specification.specification.filter.FilterDataOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterDto {

	@NotNull
	@Valid
	private List<FilterCriteria> filterCriteria;

	private FilterDataOption dataOption;

}
