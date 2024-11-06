package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository
import source.code.model.thread.Thread

interface ThreadRepository : JpaRepository<Thread, Int> {
}