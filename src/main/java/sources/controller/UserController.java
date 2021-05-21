package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    SendMail sendMail;
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
    public String dangNhap(Model model, @RequestParam(value = "email") String email, @RequestParam(value = "pass") String pass, HttpSession session){
       User user= userService.checkLogin(email,pass);
        if(user!=null&&user.getActive()==1&&user.getLevel().getId()==1){
            session.setAttribute("user",userService.checkLogin(email,pass));
            return "redirect:/";
        }else if (user!=null&&user.getActive()==1&&user.getLevel().getId()==2){
            return "redirect:/admin";
        }else if (user==null){
            model.addAttribute("error","Email hoặc password không đúng");
            return "login";
        }else if (user.getActive()==0){
            model.addAttribute("error","Tài khoản của bạn đã bị khóa");
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
        String subject = "Xác minh địa chỉ email của bạn";
        String content = "Xác minh địa chỉ email của bạn\n" +
                "Để hoàn tất việc thiết lập tài khoản, chúng tôi chỉ cần đảm bảo rằng địa chỉ email này là của bạn.\n" +
                "Để xác minh địa chỉ email của bạn, hãy sử dụng mã bảo mật này: "+code+"\n" +
                "Nếu bạn không yêu cầu mã này, bạn có thể bỏ qua email này một cách an toàn. Ai đó có thể đã nhập nhầm địa chỉ email của bạn.\n" +
                "Cảm ơn,\n";
        if (userService.findByEmail(user.getEmail())!=null){
            model.addAttribute("errorEmail","Email đã được sử dụng");
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
                model.addAttribute("errorMaxacthuc","Mã xác thực không đúng");
                return "xacthucEmail";
            }
        }else {
            model.addAttribute("errorEmail","Email không đúng");
            return "xacthucEmail";
        }

    }
    @GetMapping("/kiemtramail")
    @ResponseBody
    public String kiemtramail(@RequestParam("email") String email){
        if (userService.findByEmail(email)!=null){
            return "Email đã được sử dụng";
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
            model.addAttribute("error","email không tồn tại trong hệ thống");
            return "quenmatkhau";
        }
    }
    @RequestMapping(value = "/doimatkhau",method = RequestMethod.POST)
    public String doimatkhau(@ModelAttribute("QuenMatKhau") QuenMatKhau quenMatKhau,@RequestParam("pass") String pass,Model model){
        if(userService.findByEmailAndOtp(quenMatKhau.getEmail(),quenMatKhau.getOtp())!=null){
            QuenMatKhau quenMatKhau1=userService.findByEmailAndOtp(quenMatKhau.getEmail(),quenMatKhau.getOtp());
            User user =userService.findByEmail(quenMatKhau.getEmail());
            if(pass.equals(user.getPass())){
                model.addAttribute("error","Password đã được sử dụng");
                return "doimatkhau";
            }
            if (userService.checkotp(quenMatKhau1)!=null){
                user.setPass(pass);
                userService.save(user);
                quenMatKhau.setOtp(0);
                quenMatKhau.setNgaytao(null);
                userService.deleteQuenPass(quenMatKhau);
                return "redirect:/";
            }else {
                model.addAttribute("errorotp","mã otp đã hết hạn");
                return "doimatkhau";
            }
        }else {
            model.addAttribute("error","email hoặc mã otp không đúng");
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
}
