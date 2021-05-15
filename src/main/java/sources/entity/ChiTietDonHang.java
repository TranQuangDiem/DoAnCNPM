package sources.entity;

import lombok.Data;

import javax.persistence.*;
@Entity
@Table(name = "chitietdonhang")
@Data
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    @JoinColumn(name = "mahoadon",nullable = false)
    private DonHang mahoadon;
    @ManyToOne()
    @JoinColumn(name = "masanpham")
    private Product masanpham;
    @Column(name = "soluong")
    private int soluong;
    @Column(name = "dongia")
    private double dongia;
}
