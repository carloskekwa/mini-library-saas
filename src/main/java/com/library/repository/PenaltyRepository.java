package com.library.repository;
import com.library.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    List<Penalty> findByUserId(Long userId);
    List<Penalty> findByUserIdAndStatus(Long userId, Penalty.PenaltyStatus status);
    
    @Query("SELECT p FROM Penalty p WHERE p.user.id = :userId AND p.status = 'ACTIVE'")
    List<Penalty> findActivePenaltiesByUserId(@Param("userId") Long userId);
}
