package org.dimchik.service;

import org.dimchik.dto.UserAuthDTO;

public interface UserService {
    UserAuthDTO authenticate(String email, String password);
}
