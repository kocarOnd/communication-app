package cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NvcScenarioRepository extends JpaRepository<NvcScenario, Long> {
    
    @Query(value = "SELECT * FROM nvc_scenarios ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<NvcScenario> findRandomScenario();
}
