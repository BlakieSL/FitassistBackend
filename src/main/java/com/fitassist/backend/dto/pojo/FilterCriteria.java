package com.fitassist.backend.dto.pojo;

import com.fitassist.backend.specification.specification.filter.FilterOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class FilterCriteria {

	private String filterKey;

	private Object value;

	private FilterOperation operation;

	private Boolean isPublic;

	public static FilterCriteria of(String filterKey, Object value, FilterOperation operation) {
		FilterCriteria criteria = new FilterCriteria();
		criteria.setFilterKey(filterKey);
		criteria.setValue(value);
		criteria.setOperation(operation);
		return criteria;
	}

}
