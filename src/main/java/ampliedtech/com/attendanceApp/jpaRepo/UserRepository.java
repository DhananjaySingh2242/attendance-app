package ampliedtech.com.attendanceApp.jpaRepo;

import ampliedtech.com.attendanceApp.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Page<User> findByEmail(String email, Pageable pageable);

  void deleteByEmail(String email);

  List<User> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email, String name);

  Optional<User> findByKeycloakId(String keycloakId);
}