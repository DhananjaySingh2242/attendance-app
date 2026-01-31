package ampliedtech.com.attendenceApp.jpaRepo;

import ampliedtech.com.attendenceApp.entity.User;
import ampliedtech.com.attendenceApp.responseDto.UserResponse;

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

  List<UserResponse> findByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email,
      String name);
}