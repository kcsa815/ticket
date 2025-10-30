package com.musical.ticket.controller;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.musical.ticket.dto.MemberFormDto;
import com.musical.ticket.repository.MemberRepository;
import com.musical.ticket.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    //1. 회원가입 폼(html)을 보여주는 메서드
    @GetMapping("/new")
    public String showMemberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "members/createMemberForm"; // templates/members/createMemberForm.html 파일을 의미
    }

    //2. 회원가입 폼에서 '가입하기' 버튼을 눌렀을 때 처리하는 메서드
    @PostMapping("/new")
    public String createMember(@Valid MemberFormDto memberFormDto,
                                BindingResult bindingResult,
                                Model model){
         if (bindingResult.hasErrors()) {

            model.addAttribute("memberFormDto", memberFormDto);
            return "members/createMemberForm";
         }  
         
         try{
            //서비스 로직 호출(비밀번호 암호화 및 저장)
            memberService.join(memberFormDto.getUsername(),
                 memberFormDto.getPassword(),
                  memberFormDto.getName()
            );
         } catch(IllegalArgumentException e){
            //서비스에서 아이디 중복 예외가 발생한 경우
            model.addAttribute("errorMessage", e.getMessage()); // 에러 메시지를 모델에 담음
            return "members/createMemberForm";
         }

         //회원가입 성공 시 메인 페이지로 리다이렉트
         return "redirect:/";
    }

    // 로그인 페이지를 보여주는 매서드
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error",required = false)boolean error){
        
        if (error) {
            model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        } 
        return "members/loginForm";
      }
    
}
