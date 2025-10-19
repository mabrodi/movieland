package org.dimchik.service.base;

import lombok.RequiredArgsConstructor;
import org.dimchik.dto.UserAuthDTO;
import org.dimchik.entity.User;
import org.dimchik.repository.UserRepository;
import org.dimchik.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceBase implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserAuthDTO authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new UserAuthDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
