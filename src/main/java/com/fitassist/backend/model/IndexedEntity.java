package com.fitassist.backend.model;

public interface IndexedEntity {

	Integer getId();

	String getName();

	default String getClassName() {
		return this.getClass().getSimpleName();
	}

}
