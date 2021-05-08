package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sources.entity.Product;
import sources.model.Cart;
import sources.service.ProductService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class CartController {
    @Autowired
    ProductService productService;
    @GetMapping("/cart")
    public String giohang(){
        return "cart";
    }
    @RequestMapping(value = "/them")
    @ResponseBody
    public int them (HttpSession session, @RequestParam("id") long id) throws IOException {
        HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
        Optional<Product> product = productService.findById(id);
        if (cartItems == null) {
            cartItems = new HashMap<>();
        }
        if (product != null) {
            if (cartItems.containsKey(id)) {
                Cart item = cartItems.get(id);
                item.setProduct(product);
                if(item.getSoluong()<item.getProduct().get().getSoluong())
                item.setSoluong(item.getSoluong() + 1);
                cartItems.put(id, item);
            } else {
                Cart item = new Cart();
                item.setProduct(product);
                item.setSoluong(1);
                cartItems.put(id, item);
            }
        }
        session.setAttribute("myCartItems", cartItems);
        session.setAttribute("myCartTotal", totalPrice(cartItems));
        session.setAttribute("myCartNum", ""+cartItems.size());
        return cartItems.size();
    }
    @RequestMapping(value = "/add")
    @ResponseBody
    public List<Cart> add (HttpSession session, @RequestParam("id") long id) throws IOException {
        HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
        Optional<Product> product = productService.findById(id);
        if (cartItems == null) {
            cartItems = new HashMap<>();
        }
        if (product != null) {
            if (cartItems.containsKey(id)) {
                Cart item = cartItems.get(id);
                item.setProduct(product);
                if(item.getSoluong()<item.getProduct().get().getSoluong())
                item.setSoluong(item.getSoluong() + 1);
                cartItems.put(id, item);
            } else {
                Cart item = new Cart();
                item.setProduct(product);
                item.setSoluong(1);
                cartItems.put(id, item);
            }
        }
        Map<Long, Cart> map = cartItems;
        Collection<Cart> values = map.values();
        List<Cart> ls = new ArrayList<Cart>(values);
        session.setAttribute("myCartItems", cartItems);
        session.setAttribute("myCartTotal", totalPrice(cartItems));
        session.setAttribute("myCartNum", ""+cartItems.size());
        return ls;
    }
    @RequestMapping(value = "/giam")
    @ResponseBody
    public List<Cart> giam (HttpSession session, @RequestParam("id") long id) throws IOException {
        HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
        Optional<Product> product = productService.findById(id);
        if (cartItems == null) {
            cartItems = new HashMap<>();
        }
        if (product != null) {
            if (cartItems.containsKey(id)) {
                Cart item = cartItems.get(id);
                item.setProduct(product);
                if (item.getSoluong()>1) {
                    item.setSoluong(item.getSoluong() - 1);
                }
                cartItems.put(id, item);
            } else {
                Cart item = new Cart();
                item.setProduct(product);
                item.setSoluong(1);
                cartItems.put(id, item);
            }
        }
        Map<Long, Cart> map = cartItems;
        Collection<Cart> values = map.values();
        List<Cart> ls = new ArrayList<Cart>(values);
        session.setAttribute("myCartItems", cartItems);
        session.setAttribute("myCartTotal", totalPrice(cartItems));
        session.setAttribute("myCartNum", ""+cartItems.size());
        return ls;
    }
    @RequestMapping(value = "/delete",params = {"id"})
    public @ResponseBody List<Cart> delete( HttpSession session, @RequestParam("id") long id) {
        HashMap<Long, Cart> cartItems = (HashMap<Long, Cart>) session.getAttribute("myCartItems");
        if (cartItems == null) {
            cartItems = new HashMap<>();
        }
        if (cartItems.containsKey(id)) {
            cartItems.remove(id);
        }
        Map<Long, Cart> map = cartItems;
        Collection<Cart> values = map.values();
        List<Cart> ls = new ArrayList<Cart>(values);
        session.setAttribute("myCartItems", cartItems);
        session.setAttribute("myCartTotal", totalPrice(cartItems));
        session.setAttribute("myCartNum", ""+cartItems.size());
        return ls;
    }
    public double totalPrice(HashMap<Long, Cart> cartItems) {
        int count = 0;
        for (Map.Entry<Long, Cart> list : cartItems.entrySet()) {
            count += list.getValue().getProduct().get().getGia() * list.getValue().getSoluong();
        }
        return count;
    }
}
