package com.lucasjosino.hawapi.services.auth;

import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    @Transactional
    UserDTO register(UserRegistrationDTO user);

    UserDTO authenticate(UserAuthDTO userAuth);

    @Transactional
    void delete(UserAuthDTO userAuth);
}
