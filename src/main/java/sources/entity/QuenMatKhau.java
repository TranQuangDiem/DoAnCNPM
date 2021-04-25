package sources.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "quenmatkhau")
@Data
public class QuenMatKhau {

    @Id

    private String email;
    private int otp;
    private Timestamp ngaytao;
}
