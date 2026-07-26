package org.dimchik.service.impl;

import lombok.RequiredArgsConstructor;
import org.dimchik.entity.User;
import org.dimchik.exception.UserNotFoundException;
import org.dimchik.repository.UserRepository;
import org.dimchik.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
