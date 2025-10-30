package com.musical.ticket.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.musical.ticket.entity.Musical;
import com.musical.ticket.service.MusicalService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MusicalController {

    private final MusicalService musicalService;

    // 고객용 뮤지컬 리스트 뷰를 호출
    @GetMapping("/musical-list")
    public String showMusicalList(Model model) {
        List<Musical> musicalList = musicalService.findAllMusicals();
        model.addAttribute("musicals", musicalList);
        return "musical-list";
    }

}
