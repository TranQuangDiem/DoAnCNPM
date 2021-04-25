package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sources.entity.QuenMatKhau;
import sources.entity.User;
import sources.model.SendMail;
import sources.service.UserService;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    SendMail sendMail;
    @GetMapping("/")
    public String home(){
        return "index";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String dangNhap(Model model, @RequestParam(value = "email") String email, @RequestParam(value = "pass") String pass, HttpSession session){
        if(userService.checkLogin(email,pass)!=null){
            session.setAttribute("user",userService.checkLogin(email,pass));
            return "redirect:/";
        }else {
            model.addAttribute("error","Email hoặc password không đúng");
            return "login";
        }

    }
    @RequestMapping(value = "/dangky",method = RequestMethod.GET)
    public String dangkyget(){
        return "register";
    }
    @RequestMapping(value = "/dangky",method = RequestMethod.POST)
    public String dangky(@ModelAttribute("User") User user,HttpSession session,Model model) throws Exception {
        String subject = "Vui lòng xác thực email";
        String content = "truy cập vào http://localhost:8080/dangky?  để xác thực";
        if (userService.findByEmail(user.getEmail())!=null){
            model.addAttribute("errorEmail","Email đã được sử dụng");
            return "register";
        }else {
            sendMail.sendEmail(user.getEmail(), subject, content);
            userService.save(user);
            session.setAttribute("user", user);
            return "redirect:/";
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
            if (userService.checkotp(quenMatKhau1)!=null){
                User user =userService.findByEmail(quenMatKhau.getEmail());
                user.setPass(pass);
                userService.save(user);
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
    @GetMapping("/logout")
    public String logout (HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
