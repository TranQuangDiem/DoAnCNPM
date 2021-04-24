package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sources.model.SendMail;
import sources.service.UserService;

import javax.servlet.http.HttpSession;

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

    @GetMapping("/logout")
    public String logout (HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
