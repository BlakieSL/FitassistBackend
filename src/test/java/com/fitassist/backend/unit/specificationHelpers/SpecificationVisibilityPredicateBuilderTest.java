package com.fitassist.backend.unit.specificationHelpers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fitassist.backend.dto.pojo.FilterCriteria;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.service.implementation.specification.SpecificationVisibilityPredicateBuilderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecificationVisibilityPredicateBuilderTest {

	private SpecificationVisibilityPredicateBuilderImpl predicateBuilder;

	@Mock
	private CriteriaBuilder criteriaBuilder;

	@Mock
	private Root<Object> root;

	@Mock
	private Path<Object> userPath;

	@Mock
	private Path<Object> idPath;

	@Mock
	private Path<Boolean> publicPath;

	@Mock
	private Predicate predicate;

	private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

	private FilterCriteria criteria;

	@BeforeEach
	void setUp() {
		predicateBuilder = new SpecificationVisibilityPredicateBuilderImpl();
		criteria = new FilterCriteria();
		mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
	}

	@AfterEach
	void tearDown() {
		if (mockedAuthorizationUtil != null) {
			mockedAuthorizationUtil.close();
		}
	}

	@Test
	void buildVisibilityPredicate_shouldReturnOrPredicateWhenIsPublicFalse() {
		int userId = 1;
		criteria.setIsPublic(false);

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(root.get("user")).thenReturn(userPath);
		when(userPath.get("id")).thenReturn(idPath);
		when(root.get("isPublic")).thenReturn((Path) publicPath);
		when(criteriaBuilder.isTrue(publicPath)).thenReturn(predicate);
		when(criteriaBuilder.equal(idPath, userId)).thenReturn(predicate);
		when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

		Predicate result = predicateBuilder.buildVisibilityPredicate(criteriaBuilder, root, criteria, "user", "id",
				"isPublic");

		assertEquals(predicate, result);
		verify(criteriaBuilder).isTrue(publicPath);
		verify(criteriaBuilder).equal(idPath, userId);
		verify(criteriaBuilder).or(any(Predicate.class), any(Predicate.class));
	}

	@Test
	void buildVisibilityPredicate_shouldReturnPublicPredicateWhenIsPublicTrue() {
		criteria.setIsPublic(true);

		when(root.get("isPublic")).thenReturn((Path) publicPath);
		when(criteriaBuilder.isTrue(publicPath)).thenReturn(predicate);

		Predicate result = predicateBuilder.buildVisibilityPredicate(criteriaBuilder, root, criteria, "user", "id",
				"isPublic");

		assertEquals(predicate, result);
		verify(criteriaBuilder).isTrue(publicPath);
		verify(criteriaBuilder, never()).equal(any(), anyInt());
	}

	@Test
	void buildVisibilityPredicate_shouldReturnPublicPredicateWhenIsPublicNull() {
		criteria.setIsPublic(null);

		when(root.get("isPublic")).thenReturn((Path) publicPath);
		when(criteriaBuilder.isTrue(publicPath)).thenReturn(predicate);

		Predicate result = predicateBuilder.buildVisibilityPredicate(criteriaBuilder, root, criteria, "user", "id",
				"isPublic");

		assertEquals(predicate, result);
		verify(criteriaBuilder).isTrue(publicPath);
	}

	@Test
	void buildVisibilityPredicate_shouldUseCorrectFieldNames() {
		int userId = 5;
		criteria.setIsPublic(false);

		mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(userId);
		when(root.get("author")).thenReturn(userPath);
		when(userPath.get("userId")).thenReturn(idPath);
		when(root.get("visible")).thenReturn((Path) publicPath);
		when(criteriaBuilder.isTrue(publicPath)).thenReturn(predicate);
		when(criteriaBuilder.equal(idPath, userId)).thenReturn(predicate);
		when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

		Predicate result = predicateBuilder.buildVisibilityPredicate(criteriaBuilder, root, criteria, "author",
				"userId", "visible");

		assertEquals(predicate, result);
		verify(root).get("author");
		verify(userPath).get("userId");
		verify(root).get("visible");
	}

}
