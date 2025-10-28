package com.musical.ticket.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.musical.ticket.dto.AdminShowRegisterDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "shows")
public class Show {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    @Column(name = "show_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String posterUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob //Large Object : 대용량 텍스트를 위함
    @Column(columnDefinition = "TEXT")
    private String description;

    //좌석 정보
    @Column(name = "vip_price")
    private int vipPrice;
    @Column(name = "vip_total_seats")
    private int vipTotalSeats;

    @Column(name = "r_price")
    private int rPrice;
    @Column(name = "r_total_seats")
    private int rTotalSeats;

    @Column(name = "s_price")
    private int sPrice;
    @Column(name = "s_total_seats")
    private int sTotalSeats;

    @Column(name = "a_price")
    private int aPrice;
    @Column(name = "a_total_seats")
    private int aTotalSeats;

    //jpa는 엔티티 객체를 생성할 때 기본 생성자가 필요함.
    public Show(){
    }

    public static Show createShow(AdminShowRegisterDto dto){
        Show show = new Show();

        show.title = dto.getTitle();
        show.posterUrl = dto.getPosterUrl();
        show.startDate = dto.getStartDate();
        show.endDate = dto.getEndDate();
        show.description = dto.getDescription();

        AdminShowRegisterDto.SeatInfo seats = dto.getSeats();
        if(seats != null){
            if (seats.getVip() !=null) {
                show.vipPrice = seats.getVip().getPrice();
                show.vipTotalSeats = seats.getVip().getTotal();
            }
            if (seats.getR() !=null) {
                show.rPrice = seats.getR().getPrice();
                show.rTotalSeats = seats.getR().getTotal();
            }
            if (seats.getS() !=null) {
                show.sPrice = seats.getS().getPrice();
                show.sTotalSeats = seats.getS().getTotal();
            }
            if (seats.getA() !=null) {
                show.aPrice = seats.getA().getPrice();
                show.aTotalSeats = seats.getA().getTotal();
            }
        }   
        return show;
    }
    //Getters 추가
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public LocalDate getStartDate() {
        return startDate; 
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public int getVipPrice() {
        return vipPrice;
    }

    public int getVipTotalSeats() {
        return vipTotalSeats;
    }

    public int getRPrice() {
        return rPrice;
    }

    public int getRTotalSeats() {
        return rTotalSeats;
    }

    public int getSPrice() {
        return sPrice;
    }

    public int getSTotalSeats() {
        return sTotalSeats;
    }

    public int getAPrice() {
        return aPrice;
    }

    public int getATotalSeats() {
        return aTotalSeats;
    }
    //Setters 추가
    public void setTitle(String title) {
        this.title = title;
    }
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setVipPrice(int vipPrice) {
        this.vipPrice = vipPrice;
    }
    public void setVipTotalSeats(int vipTotalSeats) {
        this.vipTotalSeats = vipTotalSeats;
    }
    public void setRPrice(int rPrice) {
        this.rPrice = rPrice;
    }
    public void setRTotalSeats(int rTotalSeats) {
        this.rTotalSeats = rTotalSeats;
    }
    public void setSPrice(int sPrice) {
        this.sPrice = sPrice;
    }
    public void setSTotalSeats(int sTotalSeats) {
        this.sTotalSeats = sTotalSeats;
    }
    public void setAPrice(int aPrice) {
        this.aPrice = aPrice;
    }
    public void setATotalSeats(int aTotalSeats) {
        this.aTotalSeats = aTotalSeats;
    }

    // 내용 수정(post)가 안되서 디버깅용
    @Override
    public String toString() {
        return "Show{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", vipPrice=" + vipPrice +
                ", rPrice=" + rPrice +
                // ... (다른 좌석 정보) ...
                '}';
    }

}
