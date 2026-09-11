package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation

import kotlinx.serialization.Serializable

sealed interface NvcScenarioExerciseRoute {

    @Serializable
    data object Loading : NvcScenarioExerciseRoute

    @Serializable
    data object MultiSelectPhase : NvcScenarioExerciseRoute

    @Serializable
    data object SwipePhase : NvcScenarioExerciseRoute

    @Serializable
    data object SwipeSummary : NvcScenarioExerciseRoute

    @Serializable
    data object FullReport : NvcScenarioExerciseRoute
}