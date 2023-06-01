package com.lucasjosino.hawapi.services.auth;

import com.lucasjosino.hawapi.models.dto.auth.UserAuthDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserDTO;
import com.lucasjosino.hawapi.models.dto.auth.UserRegistrationDTO;

public interface AuthService {

    UserDTO register(UserRegistrationDTO user);

    UserDTO authenticate(UserAuthDTO userAuth);

    void delete(UserAuthDTO userAuth);
}
