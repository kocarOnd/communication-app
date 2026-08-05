package cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NvcScenarioRepositoryTest {

    @Autowired
    private NvcScenarioRepository scenarioRepository;

    @Test
    void findRandomScenario_ReturnsScenario_WhenDataExists() {
        // ARRANGE: Manually save a scenario directly to the in-memory database
        NvcScenario scenario = new NvcScenario();
        scenario.setTitle("Test Scenario");
        scenario.setContextDescription("Test Context");
        scenarioRepository.save(scenario);

        // ACT: Execute our custom native query
        Optional<NvcScenario> result = scenarioRepository.findRandomScenario();

        // ASSERT: Verify the query successfully pulled and mapped the row
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Scenario");
    }

    @Test
    void findRandomScenario_ReturnsEmpty_WhenDatabaseIsEmpty() {
        // ACT: Execute query on a fresh, empty database
        Optional<NvcScenario> result = scenarioRepository.findRandomScenario();

        // ASSERT: Verify it handles the empty state gracefully
        assertThat(result).isEmpty();
    }
}