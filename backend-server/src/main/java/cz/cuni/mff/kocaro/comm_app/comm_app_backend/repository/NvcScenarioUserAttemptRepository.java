package cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioUserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NvcScenarioUserAttemptRepository extends JpaRepository<NvcScenarioUserAttempt, Long> {
    
}