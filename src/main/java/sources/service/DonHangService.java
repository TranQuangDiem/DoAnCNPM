package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sources.DAO.DonHangDAO;
import sources.entity.DonHang;
import sources.model.SendMail;

import java.util.Collections;
import java.util.List;

@Service
public class DonHangService {
    @Autowired
    DonHangDAO donHangDAO;
    @Autowired
    SendMail sendMail;
    public DonHang findById(long id){
        return donHangDAO.findById(id);
    }
    public void save(DonHang donHang) throws Exception {
        sendMail.sendEmail(donHang.getIdUser().getEmail(),"Mua hàng thành công","Đơn hàng của bạn đã được đặt thành công, chúng tôi sẽ đảm bảo giao hàng trong thời gian cho trước. Chân thành cảm ơn");
        donHangDAO.save(donHang);
    }
    public void update(DonHang donHang){
        donHangDAO.save(donHang);
    }
    public List<DonHang> findAll(){
        return donHangDAO.findAllByOrderByDateDesc();
    }
    public List<DonHang> findByIdUser (long idUser){
        return donHangDAO.findByIdUser_Id(idUser);
    }
    public List<DonHang> findByIdUserAndTinhTrang (long idUser, String tinhtrang){
        return donHangDAO.findByIdUser_IdAndTinhtrang(idUser,tinhtrang);
    }
    public Page<DonHang> findPaginated(Pageable pageable) {
        List<DonHang> donhang =donHangDAO.findAllByOrderByDateDesc();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<DonHang> list;

        if (donhang.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, donhang.size());
            list = donhang.subList(startItem, toIndex);
        }

        Page<DonHang> donHangPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), donhang.size());

        return donHangPage;
    }
}
