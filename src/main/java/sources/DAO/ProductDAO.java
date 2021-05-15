package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sources.entity.Product;

import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {
    @Query(value = "select * from Product p where p.ten LIKE %:name% order by ten asc",nativeQuery = true)
    List<Product> findByTenLike(@Param("name") String name);
    @Query(value = "select * from Product p where p.ten LIKE %:name% order by gia asc",nativeQuery = true)
    List<Product> findByTenLikeOrderByGiaAsc(@Param("name") String name);
    @Query(value = "SELECT * FROM Product p where p.trangthai_hot= :hot limit :limit", nativeQuery=true)
    List<Product> findAllByTrangthaiHot(@Param("hot") boolean ishot,@Param("limit") int limit);
    @Query(value = "SELECT * FROM Product p where p.trangthai_sale= :sale limit :limit", nativeQuery=true)
    List<Product> findAllByTrangthaiSale(@Param("sale") boolean sale,@Param("limit") int limit);
    List<Product> findByLoaiOrderByTenAsc(String loai);
    List<Product> findByLoaiOrderByGiaAsc(String loai);
    List<Product> findByMausacOrderByGiaAsc(String mausac);
    List<Product> findByMausacOrderByTenAsc(String mausac);
    @Query(value = "select DISTINCT  loai from product",nativeQuery = true)
    List<String> getByLoai ();
    @Query(value = "select DISTINCT  mausac from product",nativeQuery = true)
    List<String> getByMausac ();
    @Query(value = "select * from product order by gia asc ",nativeQuery = true)
    List<Product> findAllOrderbyGia();
    @Query(value = "select * from Product where id = :id",nativeQuery = true)
    Product findId(@Param("id") long id);
}
