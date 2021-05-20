package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.DonHangDAO;
import sources.entity.DonHang;
import sources.model.SendMail;

import java.util.List;

@Service
public class DonHangService {
    @Autowired
    DonHangDAO donHangDAO;
    @Autowired
    SendMail sendMail;
    public void save(DonHang donHang) throws Exception {
        sendMail.sendEmail(donHang.getIdUser().getEmail(),"Mua hàng thành công","Đơn hàng của bạn đã được đặt thành công, chúng tôi sẽ đảm bảo giao hàng trong thời gian cho trước. Chân thành cảm ơn");
        donHangDAO.save(donHang);
    }
    public List<DonHang> findAll(){
        return donHangDAO.findAll();
    }
}
