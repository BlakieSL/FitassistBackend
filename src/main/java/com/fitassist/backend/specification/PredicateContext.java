package com.fitassist.backend.specification;

import com.fitassist.backend.dto.pojo.FilterCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public record PredicateContext<T>(CriteriaBuilder builder, Root<T> root, CriteriaQuery<?> query,
		FilterCriteria criteria) {
}
