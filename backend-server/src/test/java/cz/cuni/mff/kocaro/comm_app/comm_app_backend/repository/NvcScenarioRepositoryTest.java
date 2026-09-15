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
        NvcScenario scenario = new NvcScenario();
        scenario.setTitle("Test Scenario");
        scenario.setContextDescription("Test Context");
        scenarioRepository.save(scenario);

        Optional<NvcScenario> result = scenarioRepository.findRandomScenario();

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Scenario");
    }

    @Test
    void findRandomScenario_ReturnsEmpty_WhenDatabaseIsEmpty() {
        Optional<NvcScenario> result = scenarioRepository.findRandomScenario();

        assertThat(result).isEmpty();
    }
}