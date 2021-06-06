package sources.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import sources.entity.*;
import sources.model.Cart;
import sources.service.ChiTietDonHangService;
import sources.service.DonHangService;
import sources.service.ProductService;
import sources.thanhtoan.PaypalPaymentIntent;
import sources.thanhtoan.PaypalPaymentMethod;
import sources.thanhtoan.service.PaypalService;
import sources.thanhtoan.util.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PaymentController {
    @Autowired
    DonHangService donHangService;
    @Autowired
    ChiTietDonHangService chiTietDonHangService;
    @Autowired
    ProductService productService;
    public static final String URL_PAYPAL_SUCCESS = "pay/success";
    public static final String URL_PAYPAL_CANCEL = "pay/cancel";

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaypalService paypalService;

    @PostMapping("/pay")
    public String pay(HttpServletRequest request, @RequestParam("price") double price, @ModelAttribute("DonHang") DonHang donHang,@RequestParam("selector") String thanhtoan,HttpSession session ) throws Exception {
        String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
        String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
        if (thanhtoan.equals("paypal")) {
            try {
                Payment payment = paypalService.createPayment(
                        price,
                        "USD",
                        PaypalPaymentMethod.paypal,
                        PaypalPaymentIntent.sale,
                        "payment description",
                        cancelUrl,
                        successUrl);
                for (Links links : payment.getLinks()) {
                    if (links.getRel().equals("approval_url")) {
                        return "redirect:" + links.getHref();
                    }
                }
            } catch (PayPalRESTException e) {
                log.error(e.getMessage());
            }
            return "redirect:/";
        }else if (thanhtoan.equals("noPaypal")){
            HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
            User a = (User) session.getAttribute("user");
            if (cartItems == null) {
                cartItems = new HashMap<>();
            }
            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            String ngay = ""+date;
            String[] listdate = ngay.split("-");
            donHang.setMonth(Integer.parseInt(listdate[1]));
            donHang.setYear(Integer.parseInt(listdate[0]));
            donHang.setDate(date);
            donHang.setTinhtrang("Đang xử lý");
            donHang.setIdUser(a);
            donHang.setLoaithanhtoan("thanh toán khi nhận hàng");
            donHang.setPrice(totalPrice(cartItems) + (totalPrice(cartItems) * 0.1));
            donHangService.save(donHang);
            for (Map.Entry<Long, Cart> entry : cartItems.entrySet()) {
                ChiTietDonHang chiTietHoaDon = new ChiTietDonHang();
                chiTietHoaDon.setMahoadon(donHang);
                chiTietHoaDon.setMasanpham(productService.findId(entry.getValue().getProduct().get().getId()));
                chiTietHoaDon.setDongia(entry.getValue().getProduct().get().getGia() * entry.getValue().getSoluong());
                chiTietHoaDon.setSoluong(entry.getValue().getSoluong());
                chiTietDonHangService.save(chiTietHoaDon);
            }
            session.setAttribute("myCartItems",null);
            session.setAttribute("myCartTotal", 0);
            session.setAttribute("myCartNum", ""+0);
            return "redirect:/";
        }else {
            return "redirect:/checkout";
        }
    }

    @GetMapping(URL_PAYPAL_CANCEL)
    public String cancelPay(){
        return "redirect:/checkout";
    }

    @GetMapping(URL_PAYPAL_SUCCESS)
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, DonHang donHang, HttpSession session){
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){
                HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
                User a = (User) session.getAttribute("user");
                    if (cartItems == null) {
                        cartItems = new HashMap<>();
                    }
                    long millis = System.currentTimeMillis();
                    java.sql.Date date = new java.sql.Date(millis);
                String ngay = ""+date;
                String[] listdate = ngay.split("-");
                donHang.setMonth(Integer.parseInt(listdate[1]));
                donHang.setYear(Integer.parseInt(listdate[0]));
                donHang.setDate(date);
                donHang.setTinhtrang("Đang xử lý");
                donHang.setIdUser(a);
                donHang.setName(a.getUsername());
                donHang.setPhone(a.getPhone());
                donHang.setAddress(a.getAddress());
                donHang.setLoaithanhtoan("đã thanh toán qua paypal");
                donHang.setPrice(totalPrice(cartItems) + (totalPrice(cartItems) * 0.1));
                donHangService.save(donHang);
                    for (Map.Entry<Long, Cart> entry : cartItems.entrySet()) {
                        ChiTietDonHang chiTietHoaDon = new ChiTietDonHang();
                        chiTietHoaDon.setMahoadon(donHang);
                        chiTietHoaDon.setMasanpham(productService.findId(entry.getValue().getProduct().get().getId()));
                        chiTietHoaDon.setDongia(entry.getValue().getProduct().get().getGia() * entry.getValue().getSoluong());
                        chiTietHoaDon.setSoluong(entry.getValue().getSoluong());
                        chiTietDonHangService.save(chiTietHoaDon);
                    }
                session.setAttribute("myCartItems",null);
                session.setAttribute("myCartTotal", 0);
                session.setAttribute("myCartNum", ""+0);
                return "redirect:/";
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/checkout";
    }
    public long totalPrice(HashMap<Long, Cart> cartItems) {
        int count = 0;
        for (Map.Entry<Long, Cart> list : cartItems.entrySet()) {
            count += list.getValue().getProduct().get().getGia() * list.getValue().getSoluong();
        }
        return count;
    }
    @GetMapping("/DonHang")
    public String ordersManger(Model model,HttpSession session){
        User user = (User) session.getAttribute("user");
        model.addAttribute("chitietdonhangs",chiTietDonHangService.findByMahoadon_IdUser(user.getId()));
        model.addAttribute("donhangs",donHangService.findByIdUser(user.getId()));

        return "ordersManagement";
    }
    @GetMapping("/Admin/QuanLyDonHang")
    public String quanlydonhang(Model model,@RequestParam(required = false, value = "page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        int pagePrivious = page.orElse(1);
        pagePrivious--;
        int pageMax = page.orElse(1);
        pageMax++;
        model.addAttribute("pagePrivious", pagePrivious);

        Page<DonHang> donHangPage=donHangService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("donhangs", donHangPage);

        int totalPages = donHangPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            if (pageMax > pageNumbers.get(pageNumbers.size() - 1)) {
                pageMax = 0;
            }
        }
        model.addAttribute("pageMax", pageMax);
        return "quanLyDonHang";
    }
    @GetMapping("/QuanLyDonHang/{tinhtrang}/{id}")
    @ResponseBody
    public List<List> sapxep(@PathVariable("tinhtrang") String tinhtrang, @PathVariable("id") long id){
        List<List> result = new ArrayList<List>();
        if (tinhtrang.equals("all")){
            result.add(donHangService.findByIdUser(id));
            result.add(chiTietDonHangService.findByMahoadon_IdUser(id));
            return result;
        }
        result.add(donHangService.findByIdUserAndTinhTrang(id,tinhtrang));
        result.add(chiTietDonHangService.findByMahoadon_IdUser_IdAndMahoadon_Tinhtrang(id,tinhtrang));
        return result;
    }
    @GetMapping("/QuanLyDonHang/Huy/{id}")
    @ResponseBody
    public List<String> huydonhang(@PathVariable("id") long id) throws Exception {
        DonHang donHang = donHangService.findById(id);
        donHang.setTinhtrang("Đã hủy");
        donHangService.update(donHang);
        List<String> result = new ArrayList<>();
        result.add("Đã hủy");
        return result;

    }
}
