package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sources.entity.DonHang;

import java.util.List;

@Repository
public interface DonHangDAO extends JpaRepository<DonHang,Long> {

//    @Query(value = "select DISTINCT  year from DonHang order by year desc ",nativeQuery = true)
//    List<String> getByYear();
//    @Query(value = "select DISTINCT  month from DonHang where year =:year order by month desc",nativeQuery = true)
//    List<String> getMonthByYear(@Param("year") int year);
    @Query(value = "select DISTINCT  year from DonHang order by year desc",nativeQuery = true)
    List<Integer> getByYear();
    @Query(value = "select DISTINCT  month from DonHang where year =:year order by month desc",nativeQuery = true)
    List<Integer> getMonthByYear(@Param("year") int year);
}
