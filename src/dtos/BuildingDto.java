package dtos;

public class BuildingDto {
    private Integer id;
    private String name;
    private String address;
    private Integer floors;

    public BuildingDto(Integer id) {
        this.id = id;
    }

    public BuildingDto(String name) {
        this.name = name;
    }

    public BuildingDto(Integer id, String name, String address, Integer floors) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.floors = floors;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getFloors() {
        return floors;
    }
    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    @Override
    public String toString() {
        return "BuildingDto [id=" + id + ", name=" + name + ", address=" + address + ", floors=" + floors + "]";
    }
}
