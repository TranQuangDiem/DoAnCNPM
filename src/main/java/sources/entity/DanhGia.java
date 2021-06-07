package sources.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "danhgia")
@Data
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double sosao;
    private String danhgia;
    private Date date;
    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User iduser;
    @ManyToOne
    @JoinColumn(name = "idproduct", nullable = false)
    private Product idproduct;
}
