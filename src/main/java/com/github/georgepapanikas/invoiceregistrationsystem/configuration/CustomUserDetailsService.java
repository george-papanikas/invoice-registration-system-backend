package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import com.github.georgepapanikas.invoiceregistrationsystem.model.User;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link UserDetailsService} implementation for loading user-specific data
 * during authentication.
 *
 * <p>Retrieves a {@link User} from the database by username or email,
 * converts their roles into Spring Security authorities, and returns
 * a {@link UserDetails} object for use by the authentication framework.</p>
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository used to look up {@link User} entities by username or email.
     */
    private UserRepository userRepository;

    /**
     * Loads the user identified by the given username or email.
     *
     * <p>First attempts to find the user via {@code usernameOrEmail} in the
     * {@link UserRepository}. If found, converts the user's roles into
     * {@link GrantedAuthority} instances and returns a Spring Security
     * {@link UserDetails} object containing the username, password, and authorities.</p>
     *
     * @param usernameOrEmail the username or email address of the user
     * @return a fully populated {@link UserDetails} instance
     * @throws UsernameNotFoundException if no user was found with the given identifier
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usernameOrEmail,
                user.getPassword(),
                authorities
        );
    }
}
