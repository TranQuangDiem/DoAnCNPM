package sources.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "danhgia")
@Data
public class DanhGia {
    @Id
    private long id;
    private double sosao;
    private String danhgia;
    @ManyToOne
    @JoinColumn(name = "iduser", nullable = false)
    private User iduser;
    @ManyToOne
    @JoinColumn(name = "idproduct", nullable = false)
    private Product idproduct;
}
