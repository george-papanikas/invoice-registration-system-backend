package com.github.georgepapanikas.invoiceregistrationsystem.service;

import com.github.georgepapanikas.invoiceregistrationsystem.dto.JWTAuthResponseDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.LoginDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.dto.RegisterDTO;
import com.github.georgepapanikas.invoiceregistrationsystem.model.Role;
import com.github.georgepapanikas.invoiceregistrationsystem.model.User;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.RoleRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.repository.UserRepository;
import com.github.georgepapanikas.invoiceregistrationsystem.configuration.JWTProvider;
import com.github.georgepapanikas.invoiceregistrationsystem.service.exceptions.APIException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * {@link IAuthService} implementation for user authentication and registration.
 *
 * <p>Handles:
 * <ul>
 *   <li>User registration with validation for unique username/email</li>
 *   <li>Password hashing using the configured {@link PasswordEncoder}</li>
 *   <li>Assignment of default user roles</li>
 *   <li>User login and JWT token issuance</li>
 * </ul>
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements IAuthService {

    /**
     * Repository for performing CRUD operations on {@link User} entities.
     */
    private UserRepository userRepository;

    /**
     * Repository for retrieving {@link Role} entities.
     */
    private RoleRepository roleRepository;

    /**
     * Encoder used to hash plaintext passwords.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * Spring Security authentication manager for credential validation.
     */
    private AuthenticationManager authenticationManager;

    /**
     * Provider responsible for generating and validating JWT tokens.
     */
    private JWTProvider jwtProvider;

    /**
     * Registers a new user in the system.
     *
     * <p>Performs the following steps:
     * <ol>
     *   <li>Checks for existing username and email to enforce uniqueness</li>
     *   <li>Encodes the supplied password</li>
     *   <li>Assigns the default {@code ROLE_USER}</li>
     *   <li>Persists the new user</li>
     * </ol>
     *
     * @param registerDto DTO containing the user's registration details
     * @return a confirmation message upon successful registration
     * @throws APIException if the username or email is already in use
     */
    @Override
    public String register(RegisterDTO registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username " + registerDto.getUsername() + " is already in use");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email " + registerDto.getEmail() + " is already in use");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);

        return "User " + user.getName() + " successfully registered";
    }

    /**
     * Authenticates a user and generates a JWT for subsequent requests.
     *
     * <p>Performs the following steps:
     * <ol>
     *   <li>Validates credentials via {@link AuthenticationManager}</li>
     *   <li>Stores the resulting {@link Authentication} in the SecurityContext</li>
     *   <li>Generates a JWT using {@link JWTProvider}</li>
     *   <li>Retrieves the user's roles to include in the response payload</li>
     * </ol>
     *
     * @param loginDto DTO containing username/email and password
     * @return a {@link JWTAuthResponseDTO} containing the access token and assigned role
     */
    @Override
    public JWTAuthResponseDTO login(LoginDTO loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        Optional<User> user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail());

        String roleName = null;
        if (user.isPresent()) {
            User loggedUser = user.get();
            Optional<Role> optionalRole = loggedUser.getRoles().stream().findFirst();

            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                roleName = role.getName();
            }
        }
        JWTAuthResponseDTO jwtAuthResponse = new JWTAuthResponseDTO();
        jwtAuthResponse.setRole(roleName);
        jwtAuthResponse.setAccessToken(token);

        return jwtAuthResponse;
    }
}

