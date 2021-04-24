package sources.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String img;
    private String ten;
    private int gia;
    private String loai;
    private String kichthuoc;
    private String mausac;
    private int soluong;
    private String chitiet;
    private boolean isSale;
    private boolean isHot;
}
