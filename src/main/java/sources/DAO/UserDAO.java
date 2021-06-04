package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sources.entity.User;

import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {

     User findByEmailAndPass(String email, String pass);
     User findByEmail(String email);
     List<User> findByAndLevel_Id(long id);
     User findById(long id);

}
