package org.dimchik.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieDeletionQueueServiceImplTest {

    private MovieDeletionQueueServiceImpl deletionQueueService;

    @BeforeEach
    void setUp() {
        deletionQueueService = new MovieDeletionQueueServiceImpl();
    }

    @Test
    void scheduleForDeletionShouldAddMovieId() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(2L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(1L, 2L);
    }

    @Test
    void cancelDeletionShouldRemoveMovieId() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(2L);

        deletionQueueService.cancelDeletion(1L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(2L);
    }

    @Test
    void cancelDeletionShouldNotAffectOtherIds() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(2L);
        deletionQueueService.scheduleForDeletion(3L);

        deletionQueueService.cancelDeletion(2L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(1L, 3L);
    }

    @Test
    void cancelDeletionShouldNotThrowWhenIdNotPresent() {
        deletionQueueService.scheduleForDeletion(1L);

        deletionQueueService.cancelDeletion(999L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(1L);
    }

    @Test
    void clearRemovedMovieIdsShouldRemoveAll() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(2L);
        deletionQueueService.scheduleForDeletion(3L);

        deletionQueueService.clearRemovedMovieIds();

        assertThat(deletionQueueService.getPendingDeletionMovieIds()).isEmpty();
    }

    @Test
    void getPendingDeletionMovieIdsShouldReturnEmptyByDefault() {
        assertThat(deletionQueueService.getPendingDeletionMovieIds()).isEmpty();
    }

    @Test
    void scheduleForDeletionShouldAllowDuplicates() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(1L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(1L, 1L);
    }

    @Test
    void cancelDeletionShouldOnlyRemoveFirstOccurrence() {
        deletionQueueService.scheduleForDeletion(1L);
        deletionQueueService.scheduleForDeletion(1L);

        deletionQueueService.cancelDeletion(1L);

        assertThat(deletionQueueService.getPendingDeletionMovieIds())
                .containsExactly(1L);
    }
}
