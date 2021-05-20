package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sources.entity.ChiTietDonHang;

import java.util.List;

@Repository
public interface ChiTietDonHangDAO extends JpaRepository<ChiTietDonHang,Long> {
    List<ChiTietDonHang> findByMasanpham_Loai (String loai);
}
