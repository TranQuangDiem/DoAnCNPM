package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.QuenMatKhauDAO;
import sources.DAO.UserDAO;
import sources.entity.QuenMatKhau;
import sources.entity.User;
import sources.model.SendMail;

import java.sql.Timestamp;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuenMatKhauDAO quenMatKhauDAO;
    @Autowired
    SendMail sendMail;
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
}
