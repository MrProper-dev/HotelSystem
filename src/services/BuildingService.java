package services;

import java.util.List;

import daos.BuildingDao;
import dtos.BuildingDto;

public class BuildingService {

    private static final BuildingService instance = new BuildingService();
    private BuildingService(){}
    public static BuildingService getBuildingService(){
        return instance;
    }

    private final BuildingDao buildingDao = BuildingDao.getBuildingDao();

    public Integer getMaxFloor(){
        return buildingDao.getMaxFloor();
    }

    public List<BuildingDto> getBuildings(){
        return buildingDao.getAllWithoutAddressAndFloors();
    }

}
