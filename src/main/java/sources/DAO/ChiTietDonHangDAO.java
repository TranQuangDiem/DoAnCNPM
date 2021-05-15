package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sources.entity.ChiTietDonHang;

@Repository
public interface ChiTietDonHangDAO extends JpaRepository<ChiTietDonHang,Long> {
}
