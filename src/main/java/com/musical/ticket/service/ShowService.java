package com.musical.ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musical.ticket.dto.AdminShowRegisterDto;
import com.musical.ticket.entity.Show;
import com.musical.ticket.repository.ShowRepository;

@Service
@Transactional
public class ShowService {
    
    private final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository){
        this.showRepository = showRepository;
    }

    public Show registerShow(AdminShowRegisterDto dto){
        Show show = Show.createShow(dto);

        Show saveShow = showRepository.save(show);

        return saveShow;
    }
}
