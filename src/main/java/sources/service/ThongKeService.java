package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.ChiTietDonHangDAO;
import sources.DAO.DonHangDAO;
import sources.DAO.ProductDAO;
import sources.DAO.UserDAO;
import sources.entity.DonHang;

import java.sql.Date;
import java.util.List;

@Service
public class ThongKeService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    DonHangDAO donHangDAO;
    @Autowired
    ChiTietDonHangDAO chiTietDonHangDAO;
    public long tongDoanhThu(){
        long result = 0;
        List<DonHang> donHangs = donHangDAO.findAll();
        for (DonHang donhang: donHangs) {
            result+=donhang.getPrice();
        }
        return result;
    }
    public long tongUser(){
        return userDAO.count();
    }
    public long doanhThuHomNay(){
        long result = 0;
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        String ngay=""+date;

        List<DonHang> donHangs = donHangDAO.findAll();
        for (DonHang donhang: donHangs) {
            Date d = donhang.getDate();
            String ngay1=""+d;
            if (ngay.equals(ngay1)) {
                result += donhang.getPrice();
            }
        }
        return result;
    }

}
