package org.dimchik.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dimchik.service.MovieDeletionQueueService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieDeletionQueueServiceImpl implements MovieDeletionQueueService {
    @Getter
    private final List<Long> pendingDeletionMovieIds = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void scheduleForDeletion(long movieId) {
        pendingDeletionMovieIds.add(movieId);
    }

    @Override
    public void cancelDeletion(long movieId) {
        pendingDeletionMovieIds.remove(movieId);
    }

    @Override
    public void clearRemovedMovieIds() {
        pendingDeletionMovieIds.clear();
    }
}
