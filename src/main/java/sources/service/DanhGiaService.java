package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sources.DAO.DanhGiaDAO;
import sources.entity.DanhGia;

import java.util.List;

@Service
public class DanhGiaService {
    @Autowired
    DanhGiaDAO danhGiaDAO;
    public List<DanhGia> findByIdproduct_Id(long id){
        return danhGiaDAO.findByIdproduct_Id(id);
    }
    public void save(DanhGia danhGia){
        danhGiaDAO.save(danhGia);
    }
}
