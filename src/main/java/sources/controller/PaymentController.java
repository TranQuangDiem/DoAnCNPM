package sources.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import sources.entity.ChiTietDonHang;
import sources.entity.DonHang;
import sources.entity.Product;
import sources.entity.User;
import sources.model.Cart;
import sources.service.ChiTietDonHangService;
import sources.service.DonHangService;
import sources.service.ProductService;
import sources.thanhtoan.PaypalPaymentIntent;
import sources.thanhtoan.PaypalPaymentMethod;
import sources.thanhtoan.service.PaypalService;
import sources.thanhtoan.util.Utils;

import java.util.HashMap;
import java.util.Map;

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
                    donHang.setDate(date);
                donHang.setTinhtrang("Đang xử lý");
                donHang.setIdUser(a);
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
}
