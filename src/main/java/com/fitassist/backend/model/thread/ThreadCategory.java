package com.fitassist.backend.model.thread;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thread_category")
@Getter
@Setter
public class ThreadCategory extends CategoryBase {

	@OneToMany(mappedBy = "threadCategory")
	private final Set<ForumThread> threads = new HashSet<>();

}
