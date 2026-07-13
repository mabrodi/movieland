package org.dimchik.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private long id;
    private String comment;
    private UserResponse user;
}
