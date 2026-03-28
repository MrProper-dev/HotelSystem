package dtos;

public class RoomDto {

    private Integer id;
    private BuildingDto building;
    private Integer number;
    private Integer floor;
    private Integer sleepingPlaces;
    private Float price;
    private String picture;
    private String description;
    private RoomStatus status;

    public RoomDto(Integer id) {
        this.id = id;
    }

    public RoomDto(BuildingDto building, Integer number, Integer floor, String picture) {
        this.building = building;
        this.number = number;
        this.floor = floor;
        this.picture = picture;
    }

    public RoomDto(Integer id, BuildingDto building, Integer number, Integer floor, Integer sleepingPlaces, Float price) {
        this.id = id;
        this.building = building;
        this.number = number;
        this.floor = floor;
        this.sleepingPlaces = sleepingPlaces;
        this.price = price;
    }

    public RoomDto(BuildingDto building, Integer number, Integer floor, Integer sleepingPlaces, Float price,
            String picture) {
        this.building = building;
        this.number = number;
        this.floor = floor;
        this.sleepingPlaces = sleepingPlaces;
        this.price = price;
        this.picture = picture;
    }

    public RoomDto(Integer id, BuildingDto building, Integer number, Integer floor, Integer sleepingPlaces, Float price,
            String picture, String description) {
        this.id = id;
        this.building = building;
        this.number = number;
        this.floor = floor;
        this.sleepingPlaces = sleepingPlaces;
        this.price = price;
        this.picture = picture;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public BuildingDto getBuilding() {
        return building;
    }
    public void setBuilding(BuildingDto building) {
        this.building = building;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public Integer getFloor() {
        return floor;
    }
    public void setFloor(Integer floor) {
        this.floor = floor;
    }
    public Integer getSleepingPlaces() {
        return sleepingPlaces;
    }
    public void setSleepingPlaces(Integer sleepingPlaces) {
        this.sleepingPlaces = sleepingPlaces;
    }
    public Float getPrice() {
        return price;
    }
    public void setPrice(Float price) {
        this.price = price;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public RoomStatus getStatus() {
        return status;
    }
    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
