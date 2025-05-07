package com.github.georgepapanikas.invoiceregistrationsystem.repository;

import com.github.georgepapanikas.invoiceregistrationsystem.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("RoleRepository Unit Tests using an in-memory H2 database")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    @DisplayName("findByName returns null when no role exists")
    void findByName_nonExisting_returnsNull() {
        Role r = roleRepository.findByName("ROLE_UNKNOWN");
        assertThat(r).isNull();
    }

    @Test
    @DisplayName("save and findByName returns saved role")
    void saveAndFindByName_returnsRole() {
        Role admin = new Role();
        admin.setName("ROLE_ADMIN");
        roleRepository.save(admin);

        Role found = roleRepository.findByName("ROLE_ADMIN");
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(admin.getId());
        assertThat(found.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("findAll returns all saved roles")
    void findAll_returnsAllRoles() {
        Role r1 = new Role(); r1.setName("ROLE_ADMIN");
        Role r2 = new Role(); r2.setName("ROLE_USER");
        roleRepository.saveAll(List.of(r1, r2));

        List<Role> all = roleRepository.findAll();
        assertThat(all).hasSize(2)
                .extracting(Role::getName)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
}
