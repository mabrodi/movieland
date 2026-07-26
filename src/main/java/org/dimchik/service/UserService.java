package org.dimchik.service;

import org.dimchik.entity.User;

public interface UserService {
    User getByEmail(String email);
}
