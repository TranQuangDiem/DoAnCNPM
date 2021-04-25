package sources.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sources.entity.QuenMatKhau;

public interface QuenMatKhauDAO extends JpaRepository<QuenMatKhau, String> {
   QuenMatKhau findByEmailAndOtp(String email, int otp);
   @Query(value = "select * from QuenMatKhau q where q.email=:email and q.otp=:otp and LOCALTIMESTAMP-q.ngaytao<600",nativeQuery = true)
   QuenMatKhau checkOtp(@Param("email") String email,@Param("otp") int otp);
}
