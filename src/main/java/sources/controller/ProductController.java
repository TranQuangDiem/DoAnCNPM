package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sources.entity.Product;
import sources.service.ProductService;

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
}
