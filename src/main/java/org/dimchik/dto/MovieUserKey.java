package org.dimchik.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MovieUserKey {
    private long movieId;
    private long userId;
}
