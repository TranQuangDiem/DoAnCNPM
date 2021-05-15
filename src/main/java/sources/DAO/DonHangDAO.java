package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sources.entity.DonHang;
@Repository
public interface DonHangDAO extends JpaRepository<DonHang,Long> {
}
