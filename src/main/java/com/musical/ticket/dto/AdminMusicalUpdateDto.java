package com.musical.ticket.dto;

import java.time.LocalDate;

// (Getter, Setter가 모두 필요합니다)
public class AdminMusicalUpdateDto {

    private Long id; // 💡 ID 필드 (필수)
    private String title;
    private String posterUrl;
    
    // 👇👇👇 (중요) 날짜를 String으로 받습니다.
    private String startDate; 
    private String endDate;
    
    private String description;
    
    // 👇👇👇 (중요) 좌석 정보도 DTO에 맞게 필드명 변경
    private int vipPrice;
    private int vipTotalSeats;
    private int rPrice;
    private int rTotalSeats;
    private int sPrice;
    private int sTotalSeats;
    private int aPrice;
    private int aTotalSeats;


    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    
    // (날짜 Getter/Setter)
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // (좌석 Getter/Setter)
    public int getVipPrice() { return vipPrice; }
    public void setVipPrice(int vipPrice) { this.vipPrice = vipPrice; }
    public int getVipTotalSeats() { return vipTotalSeats; }
    public void setVipTotalSeats(int vipTotalSeats) { this.vipTotalSeats = vipTotalSeats; }

    public int getRPrice() { return rPrice; }
    public void setRPrice(int rPrice) { this.rPrice = rPrice; }
    public int getRTotalSeats() { return rTotalSeats; }
    public void setRTotalSeats(int rTotalSeats) { this.rTotalSeats = rTotalSeats; }

    public int getSPrice() { return sPrice; }
    public void setSPrice(int sPrice) { this.sPrice = sPrice; }
    public int getSTotalSeats() { return sTotalSeats; }
    public void setSTotalSeats(int sTotalSeats) { this.sTotalSeats = sTotalSeats; }

    public int getAPrice() { return aPrice; }
    public void setAPrice(int aPrice) { this.aPrice = aPrice; }
    public int getATotalSeats() { return aTotalSeats; }
    public void setATotalSeats(int aTotalSeats) { this.aTotalSeats = aTotalSeats; }

}