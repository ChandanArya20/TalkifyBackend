package in.ineuron.repositories;

import in.ineuron.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

     public boolean existsByPhone(String phone);
	 public boolean existsByEmail(String email);
	 public User findByPhone(String phone);
	 public User findByEmail(String email);

	 @Query("SELECT u FROM User u WHERE u.name LIKE %:query% OR u.email LIKE %:query%")
	 public List<User> searchUser(String query);

	 
}
