package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sources.entity.QuenMatKhau;
import sources.entity.Role;
import sources.entity.User;
import sources.model.SendMail;
import sources.service.ProductService;
import sources.service.UserService;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    SendMail sendMail;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("product",productService.findAllLimit(true,8));
        model.addAttribute("productSale",productService.findBySaleLimit(true,7));
        return "index";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String dangNhap(Model model, Principal principal, @RequestParam(value = "email") String email, @RequestParam(value = "pass") String pass, HttpSession session){
        User loginedUser = (User) ((Authentication) principal).getPrincipal();
        session.setAttribute("user",loginedUser);
        System.out.println(loginedUser);
        User user= userService.checkLogin(email,pass);
        if(user!=null&&user.getActive()==1&&user.getLevel().getId()==1){
            session.setAttribute("user",userService.checkLogin(email,pass));
            return "redirect:/";
        }else if (user!=null&&user.getActive()==1&&user.getLevel().getId()==2){
            return "redirect:/admin";
        }else if (user==null){
            model.addAttribute("error","Email ho???c password kh??ng ????ng");
            return "login";
        }else if (user.getActive()==0){
            model.addAttribute("error","T??i kho???n c???a b???n ???? b??? kh??a");
            return "login";
        }else {
            return "login";
        }

    }
    @RequestMapping(value = "/dangky",method = RequestMethod.GET)
    public String dangkyget(){
        return "register";
    }
    @RequestMapping(value = "/dangky",method = RequestMethod.POST)
    public String dangky(@ModelAttribute("User") User user,HttpSession session,Model model) throws Exception {
        if (user.getEmail()==""||user.getEmail()==null||user.getUsername()==""||user.getUsername()==null||user.getPass()==""||user.getPass()==null)
            return "register";
        int code = (int) Math.floor(((Math.random() * 899999) + 10000000));
        user.setActive(code);
        user.setLevel(new Role(1,"user"));
        user.setPass(passwordEncoder.encode(user.getPass()));
        String subject = "X??c minh ?????a ch??? email c???a b???n";
        String content = "X??c minh ?????a ch??? email c???a b???n\n" +
                "????? ho??n t???t vi???c thi???t l???p t??i kho???n, ch??ng t??i ch??? c???n ?????m b???o r???ng ?????a ch??? email n??y l?? c???a b???n.\n" +
                "????? x??c minh ?????a ch??? email c???a b???n, h??y s??? d???ng m?? b???o m???t n??y: "+code+"\n" +
                "N???u b???n kh??ng y??u c???u m?? n??y, b???n c?? th??? b??? qua email n??y m???t c??ch an to??n. Ai ???? c?? th??? ???? nh???p nh???m ?????a ch??? email c???a b???n.\n" +
                "C???m ??n,\n";
        if (userService.findByEmail(user.getEmail())!=null){
            model.addAttribute("errorEmail","Email ???? ???????c s??? d???ng");
            return "register";
        }else {
            sendMail.sendEmail(user.getEmail(), subject, content);
            userService.save(user);
            model.addAttribute("email",user.getEmail());
//            session.setAttribute("user", user);
            return "xacthucEmail";
        }
    }
    @PostMapping("/verifyEmail")
    public String xacthuc(@Param("maxacthuc") int maxacthuc,@Param("email") String email,Model model,HttpSession session){
        User user = userService.findByEmail(email);
        if (user!=null){
            if(maxacthuc==user.getActive()){
                user.setActive(1);
                userService.save(user);
                session.setAttribute("user", user);
                return "redirect:/";
            }else {
                model.addAttribute("errorMaxacthuc","M?? x??c th???c kh??ng ????ng");
                return "xacthucEmail";
            }
        }else {
            model.addAttribute("errorEmail","Email kh??ng ????ng");
            return "xacthucEmail";
        }

    }
    @GetMapping("/kiemtramail")
    @ResponseBody
    public String kiemtramail(@RequestParam("email") String email){
        if (userService.findByEmail(email)!=null){
            return "Email ???? ???????c s??? d???ng";
        }else {
            return " ";
        }
    }
    @RequestMapping(value = "/quenmatkhau", method = RequestMethod.GET)
    public String quenmatkhau() {
        return "quenmatkhau";
    }
    @RequestMapping(value = "/doimatkhau", method = RequestMethod.GET)
    public String doimatkhau() {
        return "doimatkhau";
    }
    @RequestMapping(value = "/quenmatkhau",method = RequestMethod.POST)
    public String quenmatkhau(@RequestParam("email") String email, QuenMatKhau quenMatKhau,Model model) throws Exception {
        if (userService.findByEmail(email)!=null){
            quenMatKhau.setEmail(email);
            userService.quenmatkhau(quenMatKhau);

            return "doimatkhau";
        }else {
            model.addAttribute("error","email kh??ng t???n t???i trong h??? th???ng");
            return "quenmatkhau";
        }
    }
    @RequestMapping(value = "/doimatkhau",method = RequestMethod.POST)
    public String doimatkhau(@ModelAttribute("QuenMatKhau") QuenMatKhau quenMatKhau,@RequestParam("pass") String pass,Model model){
        if(userService.findByEmailAndOtp(quenMatKhau.getEmail(),quenMatKhau.getOtp())!=null){
            QuenMatKhau quenMatKhau1=userService.findByEmailAndOtp(quenMatKhau.getEmail(),quenMatKhau.getOtp());
            User user =userService.findByEmail(quenMatKhau.getEmail());
            if(pass.equals(user.getPass())){
                model.addAttribute("error","Password ???? ???????c s??? d???ng");
                return "doimatkhau";
            }
            if (userService.checkotp(quenMatKhau1)!=null){
//                user.setPass(pass);
                user.setPass(passwordEncoder.encode(pass));
                userService.save(user);
                quenMatKhau.setOtp(0);
                quenMatKhau.setNgaytao(null);
                userService.deleteQuenPass(quenMatKhau);
                return "redirect:/";
            }else {
                model.addAttribute("errorotp","m?? otp ???? h???t h???n");
                return "doimatkhau";
            }
        }else {
            model.addAttribute("error","email ho???c m?? otp kh??ng ????ng");
            return "doimatkhau";
        }
    }
    @GetMapping("/checkout")
    public String checkout(){
        return "checkout";
    }
    @GetMapping("/logout")
    public String logout (HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("/QuanLyThongTin")
    public String quanLyThongTin(){
        return "changeInfomation";
    }
    @PostMapping("/QuanLyThongTin")
    public String changeInformation (@ModelAttribute("User") User user, HttpSession session){
        User user1 = (User) session.getAttribute("user");
        user1.setUsername(user.getUsername());
        user1.setPhone(user.getPhone());
        user1.setAddress(user.getAddress());
        session.setAttribute("user",user1);
        userService.save(user1);
        return "redirect:/QuanLyThongTin";
    }
    @GetMapping("/Admin/QuanLyUser")
    public String quanlyuser(Model model, @RequestParam(required = false, value = "page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        int pagePrivious = page.orElse(1);
        pagePrivious--;
        int pageMax = page.orElse(1);
        pageMax++;
        model.addAttribute("pagePrivious", pagePrivious);

        Page<User> userPage=userService.findPaginated(PageRequest.of(currentPage - 1, pageSize), 1);
        model.addAttribute("users", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            if (pageMax > pageNumbers.get(pageNumbers.size() - 1)) {
                pageMax = 0;
            }
        }
        model.addAttribute("pageMax", pageMax);
        return "quanLyUser";
    }
    @GetMapping("/Admin/QuanLyUser/Block")
    @ResponseBody
    public List<String> block (@RequestParam("block") int block,@RequestParam("id") long id){
        User user = userService.findById(id);
        user.setActive(block);
        userService.save(user);
        List<String> result = new ArrayList<>();
        result.add("true");
        return result;
    }
    @GetMapping("/403")
    public String err403(){
        return "403";
    }
}
