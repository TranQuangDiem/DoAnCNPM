package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sources.DAO.QuenMatKhauDAO;
import sources.DAO.UserDAO;
import sources.entity.QuenMatKhau;
import sources.entity.User;
import sources.model.CustomUserDetails;
import sources.model.SendMail;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuenMatKhauDAO quenMatKhauDAO;
    @Autowired
    SendMail sendMail;
    @Autowired
    HttpSession session;
    public User findById (long id){
        return userDAO.findById(id);
    }
    public User checkLogin(String email, String pass){
        return userDAO.findByEmailAndPass(email,pass);
    }
    public void save(User user){
        userDAO.save(user);
    }
    public User findByEmail(String email){
        return userDAO.findByEmail(email);
    }

public void quenmatkhau(QuenMatKhau quenMatKhau) throws Exception {
    long millis=System.currentTimeMillis();
    java.sql.Date date=new java.sql.Date(millis);
    Timestamp datetime = new Timestamp(date.getTime());
    int code = (int) Math.floor(((Math.random() * 899999) + 10000000));
    quenMatKhau.setNgaytao(datetime);
    quenMatKhau.setOtp(code);
    sendMail.sendEmail(quenMatKhau.getEmail(),"Bạn muốn thay đổi mật khẩu","Mã xác thực của bạn là: "+code + " mã có thời hạn 5 phút");
    quenMatKhauDAO.save(quenMatKhau);
}
public QuenMatKhau findByEmailAndOtp(String email, int otp){
        return quenMatKhauDAO.findByEmailAndOtp(email,otp);
}
public QuenMatKhau checkotp(QuenMatKhau quenMatKhau){
        return quenMatKhauDAO.checkOtp(quenMatKhau.getEmail(),quenMatKhau.getOtp());
}
public void deleteQuenPass(QuenMatKhau quenMatKhau){
        quenMatKhauDAO.save(quenMatKhau);
}
   public List<User> findByAndLevel(long level){
        return userDAO.findByAndLevel_Id(level);
   }
    public Page<User> findPaginated(Pageable pageable , long level) {
        List<User> users =userDAO.findByAndLevel_Id(level);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> list;

        if (users.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            list = users.subList(startItem, toIndex);
        }

        Page<User> userPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), users.size());

        return userPage;
    }
//spring security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Kiểm tra xem user có tồn tại trong database không?
        User user = userDAO.findByEmail(email);
        if (user!=null) {
            session.setAttribute("user", user);
        }
        return new CustomUserDetails(user);
    }
}
