package org.dimchik.dto;

import lombok.*;
import org.dimchik.enums.Action;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PendingMovieRating {
    private long movieId;
    private long userId;
    private double rating;
    private Action action;
}
