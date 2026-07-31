package cz.cuni.mff.kocaro.comm_app.comm_app_backend.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcPhase;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenario;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioOption;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioRepository;

/**
 * Seeds the database with basic data upon server initialization
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final NvcScenarioRepository scenarioRepository;

    public DatabaseSeeder(NvcScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public void run(String... args) {
        // Prevent duplicate seeding if the database is already populated
        if (scenarioRepository.count() > 0) {
            return;
        }

        System.out.println("Seeding database with a NVC scenario...");

        NvcScenario scenario = new NvcScenario();
        scenario.setTitle("The Dirty Dishes");
        scenario.setContextDescription("Your roommate left their dirty dishes in the sink for the third day in a row. You are annoyed.");

        addOption(scenario, NvcPhase.OBSERVATION, "Seeing the three plates and a pan in the sink for three days...", true, "Correct! This is a factual, camera-check observation.");
        addOption(scenario, NvcPhase.OBSERVATION, "When you act like a slob...", false, "This is an evaluation/judgment, not a neutral observation.");
        addOption(scenario, NvcPhase.OBSERVATION, "You never clean up...", false, "Words like 'never' or 'always' usually signal an evaluation, not an observation.");

        addOption(scenario, NvcPhase.FEELING, "Frustrated", true, "Correct! Frustration is a true internal feeling.");
        addOption(scenario, NvcPhase.FEELING, "Annoyed", true, "Correct! Annoyance is a true internal feeling.");
        addOption(scenario, NvcPhase.FEELING, "Overwhelmed", true, "Correct! Overwhelmed is a true internal feeling.");
        addOption(scenario, NvcPhase.FEELING, "Disrespected", false, "This is a pseudo-feeling. It describes what you think the other person is doing to you, not your emotion.");
        addOption(scenario, NvcPhase.FEELING, "Ignored", false, "This is a pseudo-feeling. It evaluates the other person's behavior rather than your internal state.");
        addOption(scenario, NvcPhase.FEELING, "Like you don't care about this household", false, "This is a thought. Phrases like 'I feel like...' or 'I feel that...' introduce our thoughts rather than internal state.");

        addOption(scenario, NvcPhase.NEED, "Order", true, "Correct! Order is a universal human need.");
        addOption(scenario, NvcPhase.NEED, "Support", true, "Correct! Support and shared responsibility are valid needs.");
        addOption(scenario, NvcPhase.NEED, "Consideration", true, "Correct! Consideration for shared spaces is a universal need.");
        addOption(scenario, NvcPhase.NEED, "You to clean up", false, "This is a strategy involving a specific person and action, not a universal need.");
        addOption(scenario, NvcPhase.NEED, "A clean kitchen", false, "While close, this is a specific strategy to meet the need for 'order'. Needs shouldn't be tied to specific objects.");

        addOption(scenario, NvcPhase.REQUEST, "Would you be willing to wash your dishes tonight?", true, "Correct! This is specific, actionable, and allows for a 'no'.");
        addOption(scenario, NvcPhase.REQUEST, "Are you open to discussing a kitchen cleaning schedule?", true, "Correct! This is a clear, actionable request for dialogue.");
        addOption(scenario, NvcPhase.REQUEST, "Clean your dishes right now.", false, "This is a demand. It uses imperative language and leaves no room for connection.");
        addOption(scenario, NvcPhase.REQUEST, "Could you start being more responsible?", false, "This is too vague. A request must be a specific, doable action.");
        addOption(scenario, NvcPhase.REQUEST, "Could you stop leaving them there?", false, "This request is negative. A request should state what you DO want the person to do, rather than what you DON'T want them to do.");

        scenarioRepository.save(scenario);

        System.out.println("Database seeding complete!");
    }

    private void addOption(NvcScenario scenario, NvcPhase phase, String text, boolean isCorrect, String feedback) {
        NvcScenarioOption option = new NvcScenarioOption();
        option.setPhase(phase);
        option.setText(text);
        option.setCorrect(isCorrect);
        option.setFeedback(feedback);
        scenario.addOption(option);
    }
}
