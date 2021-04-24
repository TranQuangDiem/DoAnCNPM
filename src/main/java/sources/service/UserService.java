package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.UserDAO;
import sources.entity.User;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public User checkLogin(String email, String pass){
        return userDAO.findByEmailAndPass(email,pass);
    }
}
