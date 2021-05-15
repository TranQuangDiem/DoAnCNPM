package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sources.service.ThongKeService;

@Controller
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;
    @GetMapping("/admin")
    public String trangchu(Model model){
        model.addAttribute("tongdoanhthu",thongKeService.tongDoanhThu());
        model.addAttribute("tongUser",thongKeService.tongUser());
        model.addAttribute("doanhthuhomnay",thongKeService.doanhThuHomNay());
        return "admin-index";
    }
    @GetMapping("/chart")
    public String a (){
        return "ui-chart";
    }
}
