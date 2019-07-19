package de.my5t3ry.alshubapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class UserController {
    @Autowired
    private UserRepository userRepository;

    public User getUser(Principal principal) {
        final User user = userRepository.findByName(principal.getName());
        if(user != null){
            return user;
        };
        final User newUser = new User(principal);
        return userRepository.save(newUser);
    }
}
