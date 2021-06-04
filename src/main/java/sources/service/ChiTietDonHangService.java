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
    public List<ChiTietDonHang> findByMahoadon_IdUser(long idUser){
        return chiTietDonHangDAO.findByMahoadon_IdUser_Id(idUser);
    }
   public List<ChiTietDonHang> findByMahoadon_IdUser_IdAndMahoadon_Tinhtrang(long idUser,String tinhTrang){
        return chiTietDonHangDAO.findByMahoadon_IdUser_IdAndAndMahoadon_Tinhtrang(idUser, tinhTrang);
    }
}
