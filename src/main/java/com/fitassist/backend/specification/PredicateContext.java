package com.fitassist.backend.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.fitassist.backend.dto.pojo.FilterCriteria;

public record PredicateContext<T>(CriteriaBuilder builder, Root<T> root, CriteriaQuery<?> query,
		FilterCriteria criteria) {
}
