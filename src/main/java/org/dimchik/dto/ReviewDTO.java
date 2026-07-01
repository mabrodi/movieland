package org.dimchik.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private long id;
    private String comment;
    private UserShortDTO user;
}
