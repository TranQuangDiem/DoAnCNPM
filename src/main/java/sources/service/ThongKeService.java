package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.ChiTietDonHangDAO;
import sources.DAO.DonHangDAO;
import sources.DAO.ProductDAO;
import sources.DAO.UserDAO;
import sources.entity.ChiTietDonHang;
import sources.entity.DonHang;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public long tongDoanhThu() {
        long result = 0;
        List<DonHang> donHangs = donHangDAO.findAll();
        for (DonHang donhang : donHangs) {
            result += donhang.getPrice();
        }
        return result;
    }

    public long tongUser() {
        return userDAO.count();
    }

    public long doanhThuHomNay() {
        long result = 0;
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        String ngay = "" + date;

        List<DonHang> donHangs = donHangDAO.findAll();
        for (DonHang donhang : donHangs) {
            Date d = donhang.getDate();
            String ngay1 = "" + d;
            if (ngay.equals(ngay1)) {
                result += donhang.getPrice();
            }
        }
        return result;
    }

    public Map<String,Integer> soLuongBanTheoLoai(int month, int year) {
        List<String> loai1 = productDAO.getByLoai();
        Map<String,Integer> result = new LinkedHashMap<>();
        for (String loai : loai1) {
        List<ChiTietDonHang> chiTietDonHangs = chiTietDonHangDAO.findByMasanpham_Loai(loai);
        if (chiTietDonHangs != null) {
            int sum = 0;
            for (ChiTietDonHang c : chiTietDonHangs) {
                String date = "" + c.getMahoadon().getDate();
                String[] list = date.split("-");
                int thang = Integer.parseInt(list[1]);
                int nam = Integer.parseInt(list[0]);
                if (thang == month && year == nam) {
                    sum += c.getSoluong();
                }
            }
            result.put(loai,sum);
        }
        }
        return result;
    }

}
