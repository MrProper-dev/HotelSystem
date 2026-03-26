package dtos;

import java.time.LocalDate;

public class GuestDto {

    private Integer id;
    private BookingDto booking;
    private String fullName;
    private LocalDate birthDate;
    private String seriesAndNumber;
    
    public GuestDto() {
    }

    public GuestDto(String fullName, LocalDate birthDate, String seriesAndNumber) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.seriesAndNumber = seriesAndNumber;
    }

    public GuestDto(Integer id, BookingDto booking, String fullName, LocalDate birthDate, String seriesAndNumber) {
        this.id = id;
        this.booking = booking;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.seriesAndNumber = seriesAndNumber;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public BookingDto getBooking() {
        return booking;
    }
    public void setBooking(BookingDto booking) {
        this.booking = booking;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public String getSeriesAndNumber() {
        return seriesAndNumber;
    }
    public void setSeriesAndNumber(String seriesAndNumber) {
        this.seriesAndNumber = seriesAndNumber;
    }  

}
