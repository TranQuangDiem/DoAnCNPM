package sources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sources.DAO.ProductDAO;
import sources.entity.Product;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    public void save(Product product){
        productDAO.save(product);
    }
    public Product findId(long id){
        return productDAO.findId(id);
    }
    public Optional<Product> findById(long id){
        return productDAO.findById(id);
    }
    public List<Product> findAll(){
        return productDAO.findAll();
    }
    public List<Product> findByTenLike(String name){
        return productDAO.findByTenLike(name);
    }
    public List<Product> findAllLimit(boolean ishot,int limit){
        return productDAO.findAllByTrangthaiHot(ishot,limit);
    }
    public List<Product> findBySaleLimit(boolean sale,int limit){
        return productDAO.findAllByTrangthaiSale(sale,limit);
    }
    public boolean existsById(long id){
        return productDAO.existsById(id);
    }

    public Page<Product> findPaginated(Pageable pageable , String timtheo,String loai,String mausac) {
        List<Product> products =productDAO.findAll();
        if (timtheo!=null) {
            products = productDAO.findByTenLike(timtheo);
        }else if (loai!=null){
            products = productDAO.findByLoaiOrderByTenAsc(loai);
        }else if (mausac!=null){
            products=productDAO.findByMausacOrderByTenAsc(mausac);
        }
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Product> list;

        if (products.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, products.size());
            list = products.subList(startItem, toIndex);
        }

        Page<Product> productPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), products.size());

        return productPage;
    }
    public List<Product> findByLoaiSortPaginated(String search , String loai, String sort,String mausac) {
        List<Product> products = productDAO.findAll();
        if (sort.equals("gia")&&loai!=null){
            products= productDAO.findByLoaiOrderByGiaAsc(loai);
        }else if (search!=null&&sort.equals("gia")){
            products= productDAO.findByTenLikeOrderByGiaAsc(search);
        }else if (sort.equals("ten")&&loai!=null){
            products= productDAO.findByLoaiOrderByTenAsc(loai);
        }else if (search!=null&&sort.equals("ten")){
            products= productDAO.findByTenLike(search);
        }else if (sort.equals("ten")&&mausac!=null){
            products= productDAO.findByMausacOrderByTenAsc(mausac);
        }else if (mausac!=null&&sort.equals("gia")){
            products= productDAO.findByMausacOrderByGiaAsc(mausac);
        }else if (sort.equals("giagiam")&&loai!=null){
            products= productDAO.findByLoaiOrderByGiaDesc(loai);
        }else if (search!=null&&sort.equals("giagiam")){
            products= productDAO.findByTenLikeOrderByGiaDesc(search);
        }else if (sort.equals("tengiam")&&loai!=null){
            products= productDAO.findByLoaiOrderByTenDesc(loai);
        }else if (search!=null&&sort.equals("tengiam")){
            products= productDAO.findByTenLikeDesc(search);
        }else if (sort.equals("tengiam")&&mausac!=null){
            products= productDAO.findByMausacOrderByTenDesc(mausac);
        }else if (mausac!=null&&sort.equals("giagiam")){
            products= productDAO.findByMausacOrderByGiaDesc(mausac);
        }if (loai!=null){
            products = productDAO.findByLoaiOrderByTenAsc(loai);
        }else if (search!=null){
            products = productDAO.findByTenLike(search);
        }else if (mausac!=null){
            products = productDAO.findByMausacOrderByTenAsc(mausac);
        }else if (sort.equals("gia")){
            products=productDAO.findAllOrderbyGia();
        }else if (sort.equals("giagiam")){
            products=productDAO.findAllOrderbyGiaDesc();
        }else if (sort.equals("tengiam")){
            products=productDAO.findAllOrderbyTenDesc();
        }
        return products;
    }

    public List<String> getLoai(){
        return productDAO.getByLoai();
    }
    public List<String> getMausac(){
        return productDAO.getByMausac();
    }
}
