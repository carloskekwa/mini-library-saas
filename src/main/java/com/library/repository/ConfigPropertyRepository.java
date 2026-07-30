package com.library.repository;
import com.library.entity.ConfigProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConfigPropertyRepository extends JpaRepository<ConfigProperty, Long> {
    Optional<ConfigProperty> findByKey(String key);
    boolean existsByKey(String key);
}
