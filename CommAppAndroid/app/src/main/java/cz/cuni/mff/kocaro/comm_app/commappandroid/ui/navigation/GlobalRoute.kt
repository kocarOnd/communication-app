package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Represents the navigation from main page
 */
sealed interface GlobalRoute {

    @Serializable
    data object MainMenu : GlobalRoute
}