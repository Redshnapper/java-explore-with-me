package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation AS c WHERE COALESCE(:pinned, c.pinned) = c.pinned")
    List<Compilation> findAllPinned(@Param("pinned") Boolean pinned, Pageable pageable);

}
