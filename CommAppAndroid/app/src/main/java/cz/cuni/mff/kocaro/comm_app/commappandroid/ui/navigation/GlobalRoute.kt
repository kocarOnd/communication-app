package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation

import kotlinx.serialization.Serializable

/**
 *
 */
sealed interface GlobalRoute {

    @Serializable
    data object MainMenu : GlobalRoute
}