package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sources.entity.User;
@Repository
public interface UserDAO extends JpaRepository<User, Long> {

     User findByEmailAndPass(String email, String pass);
     User findByEmail(String email);

}
