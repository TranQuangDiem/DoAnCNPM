package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.ChiTietDonHangDAO;
import sources.entity.ChiTietDonHang;

import java.util.List;

@Service
public class ChiTietDonHangService {
    @Autowired
    ChiTietDonHangDAO chiTietDonHangDAO;

    public void save(ChiTietDonHang chiTietDonHang){
        chiTietDonHangDAO.save(chiTietDonHang);
    }
    public List<ChiTietDonHang> findAll(){
        return chiTietDonHangDAO.findAll();
    }
}
