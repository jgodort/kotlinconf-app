package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import org.jetbrains.kotlinconf.data.*

class SessionModel(
    val id: String,
    val title: String,
    val category: String?,
    val description: String,
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val room: String?,
    val speakers: Array<Speaker>
) {
    companion object {
        fun forSession(all: AllData, sessionId: String): SessionModel {
            val briefSession = all.sessions.first { it.id == sessionId }
            val speakerMap = all.speakers.associateBy { it.id }
            val roomMap = all.rooms.associateBy { it.id }
            val categoryMap = all.categories
                .flatMap { it.items }
                .associateBy { it.id }

            return forSession(briefSession,
                speakerProvider = { id -> speakerMap[id] },
                categoryProvider = { id -> categoryMap[id] },
                roomProvider = { id -> roomMap[id]!! }
            )
        }

        fun forSession(
            briefSession: Session,
            speakerProvider: (String) -> Speaker?,
            categoryProvider: (Int) -> CategoryItem?,
            roomProvider: (Int) -> Room
        ): SessionModel {
            val startsAt = briefSession.startsAt
            val endsAt = briefSession.endsAt

            return SessionModel(
                id = briefSession.id,
                title = briefSession.title,
                category = briefSession.categoryItems.map(categoryProvider).firstOrNull()?.name,
                description = briefSession.description,
                startsAt = parseDate(startsAt),
                endsAt = parseDate(endsAt),
                speakers = briefSession.speakers.mapNotNull { speakerProvider(it) }.toTypedArray(),
                room = briefSession.roomId.let(roomProvider).name
            )
        }
    }
}

fun AllData.allSessions(): List<SessionModel> =
    sessions.map { it.id }.map { SessionModel.forSession(this, it) }

fun AllData.favoriteSessions(): List<SessionModel> =
    favorites.map { it.sessionId }.map { SessionModel.forSession(this, it) }
