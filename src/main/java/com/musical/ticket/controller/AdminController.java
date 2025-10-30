package com.musical.ticket.controller;

import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.musical.ticket.dto.AdminMusicalRegisterDto;
import com.musical.ticket.dto.AdminMusicalUpdateDto;
import com.musical.ticket.entity.Musical;
import com.musical.ticket.service.MusicalService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MusicalService musicalService;

    public AdminController(MusicalService musicalService) {
        this.musicalService = musicalService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("musicalDto", new AdminMusicalRegisterDto());
        return "admin/admin_register_musical"; 
    }

    @GetMapping("/list")
    public String musicalList(Model model) {
        List<Musical> musicals = musicalService.findAllMusicals();
        model.addAttribute("musicals", musicals);
        return "admin/admin_musical_list"; 
    }

    // 2. POST 요청: @RequestBody -> @ModelAttribute로 변경
    @PostMapping("/register")
    public String registerMusical(
            @RequestParam("posterFile") MultipartFile posterFile,
            @ModelAttribute("musicalDto") AdminMusicalRegisterDto musicalDto
    ) {
        try {
            musicalService.registerMusical(musicalDto, posterFile);
            return "redirect:/admin/list"; // 성공 시 관리자 목록 페이지로 리다이렉트
            
        } catch (Exception e) {
            System.err.println("공연 등록 중 오류 발생:" + e.getMessage());
            e.printStackTrace(); // 
            // 
            // 
            // --- 오류 로그를 자세히 보기 위해 e.printStackTrace() 추가
            return "admin/admin_register_musical"; 
        }
    }

    // 공연 수정(get)
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable("id") Long musicalId,
            Model model) {
        try {
            Musical musical = musicalService.findMusicalById(musicalId);
            model.addAttribute("musical", musical);
            return "admin/admin_edit_musical";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/list";
        }
    }
    
    // 공연 수정(post)
    @PostMapping("/edit/{id}")
    public String updateMusical(
            @PathVariable("id") Long musicalId,
            @ModelAttribute AdminMusicalUpdateDto updateDto
    ) {
        if (!musicalId.equals(updateDto.getId())) {
            return "redirect:/admin/list";
        }
        try {
            musicalService.updateMusical(musicalId, updateDto);
            return "redirect:/admin/list";
        } catch (Exception e) {
            return "redirect:/admin/edit/" + musicalId;
        }
    }

    // 공연 삭제
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteMusical(@PathVariable("id") Long musicalId) {

        try {
            musicalService.deleteMusical(musicalId); 
            return ResponseEntity.ok("공연이 성공적으로 삭제되었습니다.");

        } catch (Exception e) {
            System.err.println("공연 삭제 중 오류 발생:" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("공연 삭제 중 오류가 발생했습니다 : " + e.getMessage());
        }
    }
}