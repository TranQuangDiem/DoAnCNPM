package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sources.entity.Product;
import sources.model.MyUploadForm;
import sources.service.ProductService;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/chitietsanpham")
    public String chitiet(Model model, @RequestParam("id") long id) {
        if (productService.existsById(id)) {
            model.addAttribute("product", productService.findById(id));
            model.addAttribute("productSale", productService.findBySaleLimit(true, 4));
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

//    @GetMapping("/Admin/QuanLySanPham/Them")
//    public String themsanpham(){
//        return "form-xem-them-product";
//    }


    // GET: Hiển thị trang form upload
    @RequestMapping(value = "/Admin/QuanLySanPham/Them", method = RequestMethod.GET)
    public String uploadOneFileHandler(Model model) {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        return "form-xem-them-product";
    }

    // POST: Sử lý Upload
    @RequestMapping(value = "/uploadOneFile", method = RequestMethod.POST)
    public String uploadOneFileHandlerPOST(HttpServletRequest request, Model model,@ModelAttribute("myUploadForm") MyUploadForm myUploadForm) {
        return this.doUpload(request, model, myUploadForm);
    }
    private String doUpload(HttpServletRequest request, Model model,MyUploadForm myUploadForm) {

//        String description = myUploadForm.getDescription();
//        System.out.println("Description: " + description);
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

        // Thư mục gốc upload file.
        String uploadRootPath = "D:\\DoAnCNPM\\src\\main\\resources\\static\\img";
        System.out.println("uploadRootPath=" + uploadRootPath);

        File uploadRootDir = new File(uploadRootPath);
        // Tạo thư mục gốc upload nếu nó không tồn tại.
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();
        //
        List<File> uploadedFiles = new ArrayList<File>();
        List<String> failedFiles = new ArrayList<String>();
        for (MultipartFile fileData : fileDatas) {

            // Tên file gốc tại Client.
            String name = fileData.getOriginalFilename();
            product.setImg("/img/"+name);
            System.out.println("Client File Name = " + name);

            if (name != null && name.length() > 0) {
                try {
                    // Tạo file tại Server.
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);

                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(fileData.getBytes());
                    stream.close();
                    //
                    uploadedFiles.add(serverFile);
                    System.out.println("Write file: " + serverFile);
                    productService.save(product);
                } catch (Exception e) {
                    System.out.println("Error Write file: " + name);
                    failedFiles.add(name);
                }
            }
        }
        return "redirect:/Admin/QuanLySanPham";
    }
    @GetMapping("/Admin/QuanLySanPham/Delete")
    public String delete(@RequestParam("id") long id){

        return "redirect:/Admin/QuanLySanPham";
    }
}
