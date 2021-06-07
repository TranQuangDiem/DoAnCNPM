package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sources.entity.DanhGia;
import sources.entity.Product;
import sources.model.MyUploadForm;
import sources.service.DanhGiaService;
import sources.service.ProductService;
import sources.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    DanhGiaService danhGiaService;
    @Autowired
    UserService userService;
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));
    @GetMapping("/chitietsanpham")
    public String chitiet(Model model, @RequestParam("id") long id) {
        if (productService.existsById(id)) {
            model.addAttribute("product", productService.findById(id));
            model.addAttribute("productSale", productService.findBySaleLimit(true, 4));
            model.addAttribute("danhgias",danhGiaService.findByIdproduct_Id(id));
            return "single-product";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Product> search(@RequestParam("name") String ten) {
        return productService.findByTenLike(ten);
    }
    @GetMapping("/danhsachsanpham")
    public String danhsach(@RequestParam(value = "search", required = false) String ten, @RequestParam(required = false, value = "page") Optional<Integer> page, Model model,
                           @RequestParam("size") Optional<Integer> size,@RequestParam(value = "loai", required = false) String loai,
                           @RequestParam(value = "mausac",required = false) String mausac) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        int pagePrivious = page.orElse(1);
        pagePrivious--;
        int pageMax = page.orElse(1);
        pageMax++;
        model.addAttribute("pagePrivious", pagePrivious);

        Page<Product> productPage;
            productPage = productService.findPaginated(PageRequest.of(currentPage - 1, pageSize), ten,loai,mausac);
        model.addAttribute("productPage", productPage);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            if (pageMax > pageNumbers.get(pageNumbers.size() - 1)) {
                pageMax = 0;
            }
        }
        if (ten!=null) {
            model.addAttribute("vitri", ten);
        }else if (loai!=null){
            model.addAttribute("type", loai);
        }else {
            model.addAttribute("mausac",mausac);
        }
        model.addAttribute("pageMax", pageMax);
        model.addAttribute("loai",productService.getLoai());
        model.addAttribute("color",productService.getMausac());
        model.addAttribute("sum",productService.findByLoaiSortPaginated(ten,loai,"ten",mausac).size());
        return "category";
    }

    @GetMapping("/danhsachsanpham1")
    @ResponseBody
    public List<Product> sort(@RequestParam(value = "search", required = false) String ten, @RequestParam(required = false) Integer page,
                              @RequestParam(value = "loai", required = false) String loai,@RequestParam(value = "sort", required = false) String sort,
                              @RequestParam(value = "size", required = false) Optional<Integer> size,@RequestParam(value = "mausac",required = false) String mausac) {
        List<Product> product =productService.findByLoaiSortPaginated(ten,loai,sort,mausac);
        int pagesize = size.orElse(12);
            PagedListHolder<Product> pagedListHolder = new PagedListHolder<>(product);
            pagedListHolder.setPageSize(pagesize);
            if (page == null || page < 1 || page > pagedListHolder.getPageCount()) page = 1;
            if (page == null || page < 1 || page > pagedListHolder.getPageCount()) {
                pagedListHolder.setPage(0);
            } else if (page <= pagedListHolder.getPageCount()) {
                pagedListHolder.setPage(page - 1);
            }

        return pagedListHolder.getPageList();
    }
    @GetMapping("/Admin/QuanLySanPham")
    public String quanlysanpham(Model model, @RequestParam(required = false, value = "page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        int pagePrivious = page.orElse(1);
        pagePrivious--;
        int pageMax = page.orElse(1);
        pageMax++;
        model.addAttribute("pagePrivious", pagePrivious);

        Page<Product> productPage;
        productPage = productService.findPaginated(PageRequest.of(currentPage - 1, pageSize), null,null,null);
        model.addAttribute("products", productPage);

        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            if (pageMax > pageNumbers.get(pageNumbers.size() - 1)) {
                pageMax = 0;
            }
        }
        model.addAttribute("pageMax", pageMax);
//        model.addAttribute("products",productService.findAll());
        return "quanLyProduct";
    }

    @GetMapping("/Admin/QuanLySanPham/Sua")
    public String editSanPham(@RequestParam("id") long id,Model model){
        model.addAttribute("product",productService.findById(id));
        return "form-chinh-sua-product";
    }
    @RequestMapping(value = "/Admin/QuanLySanPham/Sua", method = RequestMethod.POST)
    public String editSanPham(HttpServletRequest request,@RequestParam("id") long id, Model model,@ModelAttribute("myUploadForm") MyUploadForm myUploadForm) throws IOException {
        Product product = productService.findId(id);
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();
        if(!fileDatas[0].isEmpty()){
            return this.luuSp(request,model,myUploadForm,product);
        }else {
            product.setTen(myUploadForm.getTen());
            product.setGia(myUploadForm.getGia());
            product.setSoluong(myUploadForm.getSoluong());
            product.setChitiet(myUploadForm.getChitiet());
            product.setLoai(myUploadForm.getLoai());
            product.setMausac(myUploadForm.getMausac());
            product.setKichthuoc(myUploadForm.getKichthuoc());
            product.setDetph(myUploadForm.getDetph());
            product.setHeight(myUploadForm.getHeight());
            product.setWidth(myUploadForm.getWidth());
            product.setTrangthaiHot(myUploadForm.isTrangthaiHot());
            product.setTrangthaiSale(myUploadForm.isTrangthaiSale());
            productService.save(product);
        }

        return "redirect:/Admin/QuanLySanPham";
    }

    // GET: Hiển thị trang form upload
    @RequestMapping(value = "/Admin/QuanLySanPham/Them", method = RequestMethod.GET)
    public String uploadOneFileHandler(Model model) {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        return "form-xem-them-product";
    }

    // POST: Sử lý Upload
    @RequestMapping(value = "/uploadOneFile", method = RequestMethod.POST)
    public String uploadOneFileHandlerPOST(HttpServletRequest request, Model model,@ModelAttribute("myUploadForm") MyUploadForm myUploadForm) throws IOException {
        return this.doUpload(request, model, myUploadForm);
    }
    private String doUpload(HttpServletRequest request, Model model, MyUploadForm myUploadForm) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        Product product = new Product();
        product.setTen(myUploadForm.getTen());
        product.setGia(myUploadForm.getGia());
        product.setSoluong(myUploadForm.getSoluong());
        product.setChitiet(myUploadForm.getChitiet());
        product.setLoai(myUploadForm.getLoai());
        product.setMausac(myUploadForm.getMausac());
        product.setKichthuoc(myUploadForm.getKichthuoc());
        product.setDetph(myUploadForm.getDetph());
        product.setHeight(myUploadForm.getHeight());
        product.setWidth(myUploadForm.getWidth());
        product.setTrangthaiHot(myUploadForm.isTrangthaiHot());
        product.setTrangthaiSale(myUploadForm.isTrangthaiSale());
        MultipartFile fileDatas = myUploadForm.getFileDatas()[0];
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path file = CURRENT_FOLDER.resolve(staticPath)
                .resolve(imagePath).resolve(fileDatas.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(file)) {
            System.out.println(file.toString());
            os.write(fileDatas.getBytes());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        product.setImg("/"+imagePath.resolve(fileDatas.getOriginalFilename()).toString());
        productService.save(product);
        return "redirect:/Admin/QuanLySanPham";
    }
    private String luuSp(HttpServletRequest request, Model model, MyUploadForm myUploadForm,Product product) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        product.setTen(myUploadForm.getTen());
        product.setGia(myUploadForm.getGia());
        product.setSoluong(myUploadForm.getSoluong());
        product.setChitiet(myUploadForm.getChitiet());
        product.setLoai(myUploadForm.getLoai());
        product.setMausac(myUploadForm.getMausac());
        product.setKichthuoc(myUploadForm.getKichthuoc());
        product.setDetph(myUploadForm.getDetph());
        product.setHeight(myUploadForm.getHeight());
        product.setWidth(myUploadForm.getWidth());
        product.setTrangthaiHot(myUploadForm.isTrangthaiHot());
        product.setTrangthaiSale(myUploadForm.isTrangthaiSale());
        MultipartFile fileDatas = myUploadForm.getFileDatas()[0];
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        Path file = CURRENT_FOLDER.resolve(staticPath)
                .resolve(imagePath).resolve(fileDatas.getOriginalFilename());
        try (OutputStream os = Files.newOutputStream(file)) {
            System.out.println(file.toString());
            os.write(fileDatas.getBytes());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

       product.setImg("/"+imagePath.resolve(fileDatas.getOriginalFilename()).toString());
        productService.save(product);
        return "redirect:/Admin/QuanLySanPham";
    }
    @GetMapping("/Admin/QuanLySanPham/Delete")
    public String delete(@RequestParam("id") long id,@RequestParam("page") String page){
        productService.delete(id);
        return "redirect:/Admin/QuanLySanPham?page="+page;
    }
    @GetMapping("/DanhGia/{sosao}/{binhluan}/{idUser}/{idSanpham}")
    @ResponseBody
    public List<DanhGia> danhGia(@PathVariable("sosao") int sosao,@PathVariable("binhluan") String binhluan,@PathVariable("idUser") long idUser, @PathVariable("idSanpham") long idSanpham){
        DanhGia danhGia = new DanhGia();
        danhGia.setSosao(sosao);
        danhGia.setDanhgia(binhluan);
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        danhGia.setDate(date);
        danhGia.setIduser(userService.findById(idUser));
        danhGia.setIdproduct(productService.findId(idSanpham));
        danhGiaService.save(danhGia);
        return danhGiaService.findByIdproduct_Id(idSanpham);

    }
}
