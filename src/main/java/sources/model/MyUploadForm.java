package sources.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class MyUploadForm {
    // Upload files.
    private MultipartFile[] fileDatas;
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

    public MultipartFile[] getFileDatas() {
        return fileDatas;
    }

    public void setFileDatas(MultipartFile[] fileDatas) {
        this.fileDatas = fileDatas;
    }

}
