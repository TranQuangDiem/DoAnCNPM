package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.DonHangDAO;
import sources.entity.DonHang;

import java.util.List;

@Service
public class DonHangService {
    @Autowired
    DonHangDAO donHangDAO;
    public void save(DonHang donHang){
        donHangDAO.save(donHang);
    }
    public List<DonHang> findAll(){
        return donHangDAO.findAll();
    }
}
