package sources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sources.service.ThongKeService;

import java.util.ArrayList;
import java.util.Map;

@Controller
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;
    @GetMapping("/admin")
    public String trangchu(Model model){
        model.addAttribute("tongdoanhthu",thongKeService.tongDoanhThu());
        model.addAttribute("tongUser",thongKeService.tongUser());
        model.addAttribute("doanhthuhomnay",thongKeService.doanhThuHomNay());
        model.addAttribute("years",thongKeService.getByYear());
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        String ngay = ""+date;
        String[] listdate = ngay.split("-");
        model.addAttribute("months",thongKeService.getMonthByYear());
        model.addAttribute("soluongban",thongKeService.soLuongBanTheoLoai(Integer.parseInt(listdate[1]),Integer.parseInt(listdate[0])));
        model.addAttribute("surveyMap",thongKeService.doanhthuthangtheonam(Integer.parseInt(listdate[0])));
        return "trangchu";
    }
    @GetMapping("/chart")
    public String a (){
        return "ui-chart";
    }

    @GetMapping("/doanhthu/{nam}/{timtheo}")
    @ResponseBody
    public ArrayList<ArrayList> doanhthu(@PathVariable("nam") int nam,@PathVariable("timtheo") String timtheo){
        if (timtheo.equals("loai")){
            return thongKeService.doanhthutheoloai(nam);
        }
        return thongKeService.doanhthutheonam(nam);
    }
    @GetMapping("/soluongban/{month}/{year}/{timtheo}")
    @ResponseBody
    public ArrayList<ArrayList> soluongban(@PathVariable("month") int month,@PathVariable("year") int year,@PathVariable("timtheo") String timtheo){
        if (timtheo.equals("giatri")){
            return thongKeService.doanhthuBanTheoLoai(month,year);
        }

        return thongKeService.soLuongBanTheoLoai(month,year);
    }
}
