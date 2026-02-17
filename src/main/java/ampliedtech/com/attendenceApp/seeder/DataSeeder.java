package ampliedtech.com.attendenceApp.seeder;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ampliedtech.com.attendenceApp.entity.Role;
import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.jpaRepo.UserRepository;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Normalize legacy/invalid role values so they match Role enum (ROLE_ADMIN, ROLE_USER)
        int updated = entityManager.createNativeQuery(
                "UPDATE user SET role = 'ROLE_USER' WHERE role IS NULL OR role NOT IN ('ROLE_ADMIN', 'ROLE_USER')"
        ).executeUpdate();
        if (updated > 0) {
            logger.info("Normalized role for {} user(s) to ROLE_USER", updated);
        }

        String adminEmail = "admin@example.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail(adminEmail);
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            logger.info("Admin user created: {}", adminEmail);
        } else {
            logger.info("Admin user already exists: {}", adminEmail);
        }
    }
}
