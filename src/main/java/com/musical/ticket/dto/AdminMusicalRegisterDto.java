package com.musical.ticket.dto;

import java.time.LocalDate;

public class AdminMusicalRegisterDto {
    //기본 생성자 추가
    public AdminMusicalRegisterDto(){
    }

    // 1. DTO의 최상위 필드
    private String title;
    private String posterUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
    private String description;
    
    private SeatInfo seats;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setDescription(String description) { this.description = description; }
    public SeatInfo getSeats() { return seats; }
    public void setSeats(SeatInfo seats) { this.seats = seats; }

    // (디버깅용) 콘솔 출력을 위한 toString() 메서드
    @Override
    public String toString() {
        return "AdminShowRegisterDto{" +
                "title='" + title + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", seats=" + seats + // 'seats' 필드도 toString() 호출
                '}';
    }

    public static class SeatInfo {
        private SeatGrade vip;
        private SeatGrade r;
        private SeatGrade s;
        private SeatGrade a;

        public SeatGrade getVip() { return vip; }
        public void setVip(SeatGrade vip) { this.vip = vip; }
        public SeatGrade getR() { return r; }
        public void setR(SeatGrade r) { this.r = r; }
        public SeatGrade getS() { return s; }
        public void setS(SeatGrade s) { this.s = s; }
        public SeatGrade getA() { return a; }
        public void setA(SeatGrade a) { this.a = a; }

        
        // (디버깅용)
        @Override
        public String toString() {
            return "SeatInfo{" +
                    "vip=" + vip +
                    ", r=" + r +
                    ", s=" + s +
                    ", a=" + a +
                    '}';
        }
    }

    public static class SeatGrade {
        private int price;
        private int total; // admin.js의 'total'과 이름 동일

        // --- Getters and Setters for SeatGrade ---
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        // (디버깅용)
        @Override
        public String toString() {
            return "SeatGrade{price=" + price + ", total=" + total + '}';
        }
    }
}