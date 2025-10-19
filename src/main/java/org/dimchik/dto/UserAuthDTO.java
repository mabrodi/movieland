package org.dimchik.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dimchik.common.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDTO {
    private long id;
    private String name;
    private String email;
    private Role role;
}
