package net.soberanacraft

// Authorship: polyana@polyclub.games

import com.velocitypowered.api.event.Continuation
import kotlinx.coroutines.Job

infix fun Continuation.await(job: Job): Continuation {
    job.invokeOnCompletion { e ->
        if (e == null) {
            resume()
        } else {
            resumeWithException(e)
        }
    }
    return this
}

infix fun <J: Job> J.with(continuation: Continuation): J {
    continuation await this
    return this
}
