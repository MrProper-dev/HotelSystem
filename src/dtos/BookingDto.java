package dtos;

import java.time.LocalDate;
import java.util.List;

public class BookingDto {

    private Integer id;
    private ClientDto client;
    private RoomDto room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Float totalPrice;
    private BookingStatus status;
    private List<GuestDto> guests;

    public BookingDto(Integer id, ClientDto client, RoomDto room, LocalDate checkInDate, LocalDate checkOutDate,
            Float totalPrice, BookingStatus status) {
        this.id = id;
        this.client = client;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public ClientDto getClient() {
        return client;
    }
    public void setClient(ClientDto client) {
        this.client = client;
    }
    public RoomDto getRoom() {
        return room;
    }
    public void setRoom(RoomDto room) {
        this.room = room;
    }
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    public Float getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }
    public BookingStatus getStatus() {
        return status;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    public List<GuestDto> getGuests() {
        return guests;
    }
    public void setGuests(List<GuestDto> guests) {
        this.guests = guests;
    }

}
