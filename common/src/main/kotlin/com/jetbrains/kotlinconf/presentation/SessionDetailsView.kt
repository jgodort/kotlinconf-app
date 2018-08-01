package com.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.SessionModel
import org.jetbrains.kotlinconf.model.SessionRating

interface SessionDetailsView {
    fun updateView(session: SessionModel)
    fun setupRatingButtons(rating: SessionRating?)
    fun setIsFavorite(isFavorite: Boolean)
    fun setRatingClickable(clickable: Boolean)
}