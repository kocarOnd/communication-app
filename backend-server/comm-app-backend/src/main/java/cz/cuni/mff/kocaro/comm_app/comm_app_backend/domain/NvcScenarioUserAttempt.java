package cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nvc_scenario_user_attempts")
@Getter
@Setter
@NoArgsConstructor
public class NvcScenarioUserAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private NvcScenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id", nullable = false)
    private NvcScenarioOption selectedOption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NvcPhase phase;

    @Column(nullable = false)
    private boolean wasCorrect;

    @Column(nullable = false)
    private LocalDateTime attemptedAt = LocalDateTime.now();
}
