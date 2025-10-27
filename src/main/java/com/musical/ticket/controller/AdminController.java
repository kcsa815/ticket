package com.musical.ticket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.musical.ticket.dto.AdminShowRegisterDto;
import com.musical.ticket.service.ShowService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/admin") // "/admin"으로 시작하는 모든 요청을 이 컨트롤러가 처리
public class AdminController {

    private final ShowService showService;

    public AdminController(ShowService showService){
        this.showService = showService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "admin/admin_register_show";
    }

    @GetMapping("/list")
    public String showList() {
        return "admin/admin_show_list";
    }

    // post 공연 등록 처리, POST /admin/register
    @PostMapping("/register")
    @ResponseBody // 페이지가 html이 아닌 json을 반환한다는 의미.
    public ResponseEntity<String> registerShow(
        @RequestBody AdminShowRegisterDto showDto //@RequestBody : js가 보낸 json을 dto로 변환
    ){
        try {
            showService.registerShow(showDto);
            return ResponseEntity.ok("공연이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            System.err.println("공연 등록 중 오류 발생:" + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) //500에러
                .body("공연 등록 중 오류가 발생했습니다 : " + e.getMessage());
        }
    }
}