package org.dimchik.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CachedMovieRating {
    private double averageRating;
    private int voteCount;
}
