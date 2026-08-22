package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation

sealed class GlobalRoute(val route: String) {
    data object MainMenu : GlobalRoute("global_main_menu")
}