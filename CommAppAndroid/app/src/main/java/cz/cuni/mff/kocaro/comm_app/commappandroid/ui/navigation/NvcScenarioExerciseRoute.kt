package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation

sealed class NvcScenarioExerciseRoute(val route: String) {
    data object Loading : NvcScenarioExerciseRoute("nvc_loading")
    data object MultiSelectPhase : NvcScenarioExerciseRoute("nvc_multi_select")
    data object SwipePhase : NvcScenarioExerciseRoute("nvc_swipe")
    data object SwipeSummary : NvcScenarioExerciseRoute("nvc_swipe_summary")
    data object FullReport : NvcScenarioExerciseRoute("nvc_full_report")
}