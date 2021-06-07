package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sources.entity.DanhGia;

import java.util.List;

@Repository
public interface DanhGiaDAO extends JpaRepository<DanhGia,Long> {

    List<DanhGia> findByIdproduct_Id(long id);
}
