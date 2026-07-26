package org.dimchik.service;

import java.util.List;

public interface MovieDeletionQueueService {
    List<Long> getPendingDeletionMovieIds();

    void scheduleForDeletion(long movieId);

    void cancelDeletion(long movieId);

    void clearRemovedMovieIds();
}
