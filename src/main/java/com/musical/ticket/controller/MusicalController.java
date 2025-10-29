package com.musical.ticket.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.musical.ticket.dto.MusicalItemDto;


@Controller
public class MusicalController {

    @GetMapping("/musical-list")
    public String musicalListPage(Model model) {

        List<MusicalItemDto> musicalList = new ArrayList<>();

        musicalList.add(new MusicalItemDto(1L, "헤드윅", "2025.03.14 - 2025.06.30", "https://via.placeholder.com/300x420/ff69b4?text=Hedwig"));
        musicalList.add(new MusicalItemDto(2L, "레미제라블", "2025.04.01 - 2025.08.15", "https://via.placeholder.com/300x420/000080?text=Les+Mis"));
        musicalList.add(new MusicalItemDto(3L, "오페라의 유령", "2025.05.20 - 2025.10.10", "https://via.placeholder.com/300x420/333333?text=Phantom"));
        musicalList.add(new MusicalItemDto(4L, "시카고", "2025.06.01 - 2025.07.31", "https://via.placeholder.com/300x420/cc0000?text=Chicago"));
        musicalList.add(new MusicalItemDto(5L, "맘마미아!", "2025.07.15 - 2025.11.30", "https://via.placeholder.com/300x420/0099cc?text=Mamma+Mia"));
        musicalList.add(new MusicalItemDto(6L, "라이온 킹", "2025.08.01 - 2026.01.01", "https://via.placeholder.com/300x420/f9a602?text=Lion+King"));
        musicalList.add(new MusicalItemDto(7L, "위키드", "2025.09.10 - 2025.12.31", "https://via.placeholder.com/300x420/008000?text=Wicked"));

        model.addAttribute("musicalList", musicalList);

        return "musical-list";
    }
    
}
