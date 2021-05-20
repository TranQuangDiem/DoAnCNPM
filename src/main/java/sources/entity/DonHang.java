package sources.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
@Entity
@Table(name="donhang")
@Data
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "idAccount",nullable = false)
    private User idUser;
    private String name;
    private String address;
    private int phone;
    private Date date;
    private double price;
    private String tinhtrang;
    private String ghichu;
    private String loaithanhtoan;
}
