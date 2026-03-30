package services;

import java.util.List;

import daos.BuildingDao;
import dtos.BuildingDto;

public class BuildingService {

    private final Integer PAGE_SIZE = 4;

    private final BuildingDao buildingDao = BuildingDao.getBuildingDao();

    private static final BuildingService instance = new BuildingService();
    private BuildingService(){}
    public static BuildingService getBuildingService(){
        return instance;
    }

    public void createBuilding(String name, String address, Integer floors){
        BuildingDto building = new BuildingDto();
        building.setName(name);
        building.setAddress(address);
        building.setFloors(floors);

        buildingDao.addBuilding(building);
    }

    public void deleteBuilding(Integer buildingId){
        buildingDao.deleteBuilding(buildingId);
    }

    public void updateBuilding(Integer buildingId, String name, String address, Integer floors) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Building id cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Building name cannot be null or empty");
        }
        if (floors == null) {
            throw new IllegalArgumentException("Building floors cannot be null");
        }
        if (floors <= 0) {
            throw new IllegalArgumentException("Building floors must be greater than 0");
        }
        BuildingDto building = new BuildingDto();
        building.setId(buildingId);
        building.setName(name.trim());
        building.setAddress(address);
        building.setFloors(floors);
        buildingDao.updateBuilding(building);
    }

    public Integer getPageCount(Integer buildingsCount){
        Integer pageCount = buildingsCount / PAGE_SIZE;
        if(buildingsCount % PAGE_SIZE != 0){
            pageCount++;
        }
        return pageCount;
    }

    public Integer getBuildingsCount(){
        return buildingDao.getBuildingsCount();
    }

    public List<BuildingDto> getPageBuildings(Integer page){
        return buildingDao.getAllBuildingsWithPagination(page, PAGE_SIZE);
    }

    public Integer getMaxFloor(){
        return buildingDao.getMaxFloor();
    }

    public List<BuildingDto> getBuildings(){
        return buildingDao.getAllWithoutAddressAndFloors();
    }

    public List<BuildingDto> getBuildingsWithoutAddress(){
        return buildingDao.getAllBuildingsWithoutAddress();
    }

}
