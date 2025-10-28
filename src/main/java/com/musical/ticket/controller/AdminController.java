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
import com.musical.ticket.dto.AdminShowRegisterDto;
import com.musical.ticket.dto.AdminShowUpdateDto;
import com.musical.ticket.entity.Show;
import com.musical.ticket.service.ShowService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin") // "/admin"으로 시작하는 모든 요청을 이 컨트롤러가 처리
public class AdminController {

    private final ShowService showService;

    public AdminController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "admin/admin_register_show";
    }

    @GetMapping("/list")
    public String showList(Model model) {
        List<Show> shows = showService.findAllShows();
        model.addAttribute("shows", shows);

        return "admin/admin_show_list";
    }

    // post 공연 등록
    @PostMapping("/register")
    @ResponseBody // 페이지가 html이 아닌 json을 반환한다는 의미.
    public ResponseEntity<String> registerShow(
            @RequestBody AdminShowRegisterDto showDto // @RequestBody : js가 보낸 json을 dto로 변환
    ) {
        try {
            showService.registerShow(showDto);
            return ResponseEntity.ok("공연이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            System.err.println("공연 등록 중 오류 발생:" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500에러
                    .body("공연 등록 중 오류가 발생했습니다 : " + e.getMessage());
        }
    }

    // 공연 수정(get)
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable("id") Long showId, // 💡 URL의 {id} 값을 가져옴
            Model model) {
        try {
            Show show = showService.findShowById(showId);
            model.addAttribute("show", show);
            return "admin/admin_edit_show";

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return "redirect:/admin/list";
        }
    }
    // 공연 수정(post)
    @PostMapping("/edit/{id}")
    public String updateShow(
            @PathVariable("id") Long showId,
            @ModelAttribute AdminShowUpdateDto updateDto // 💡 [4] Show -> AdminShowUpdateDto
    ) {
        // (ID 검증)
        if (!showId.equals(updateDto.getId())) {
            return "redirect:/admin/list";
        }

        try {
            // (디버깅 로그)
            System.out.println("===== 폼에서 받은 DTO 데이터 =====");
            System.out.println(updateDto.toString()); // (DTO에 toString()을 만드셔도 좋습니다)
            System.out.println("DTO Start Date (String): " + updateDto.getStartDate());
            System.out.println("===============================");

            // 💡 [5] DTO를 서비스로 전달
            showService.updateShow(showId, updateDto); 
            
            return "redirect:/admin/list";

        } catch (DateTimeParseException e) {
            // (날짜 변환 실패 시)
            System.err.println("공연 수정 중 날짜 변환 오류: " + e.getMessage());
            return "redirect:/admin/edit/" + showId;
        } catch (Exception e) {
            System.err.println("공연 수정 중 오류 발생: " + e.getMessage());
            return "redirect:/admin/edit/" + showId;
        }
    }

    // 공연 삭제
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteShow(@PathVariable("id") Long showId) {
        try {
            showService.deleteShow(showId);
            return ResponseEntity.ok("공연이 성공적으로 삭제되었습니다.");

        } catch (Exception e) {
            System.err.println("공연 삭제 중 오류 발생:" + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500에러
                    .body("공연 삭제 중 오류가 발생했습니다 : " + e.getMessage());
        }
    }

}