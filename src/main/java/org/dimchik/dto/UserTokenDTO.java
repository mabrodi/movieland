package org.dimchik.dto;

import lombok.*;
import org.dimchik.enums.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {
    private long id;
    private String name;
    private String email;
    private Role role;
}
