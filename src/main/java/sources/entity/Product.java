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
    private int width;
    private int height;
    private int detph;
    private String mausac;
    private int soluong;
    private String chitiet;
    private boolean trangthaiSale;
    private boolean trangthaiHot;

    public long getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getTen() {
        return ten;
    }

    public int getGia() {
        return gia;
    }

    public String getLoai() {
        return loai;
    }

    public String getKichthuoc() {
        return kichthuoc;
    }

    public String getMausac() {
        return mausac;
    }

    public int getSoluong() {
        return soluong;
    }

    public String getChitiet() {
        return chitiet;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDetph() {
        return detph;
    }
}
