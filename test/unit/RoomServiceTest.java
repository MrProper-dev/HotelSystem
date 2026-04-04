package unit;

import daos.RoomDao;
import dtos.BuildingDto;
import dtos.RoomDto;
import dtos.RoomStatus;
import services.RoomService;
import utils.Test;

import static utils.AssertionChecker.assertThat;
import static utils.AssertionChecker.ExceptionChecker.assertThrows;
import static utils.AssertionChecker.ExceptionChecker.assertThrowsWithMessage;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;


public class RoomServiceTest implements Test{
    
    private MockRoomDao mockDao;
    private RoomService roomService;
    
    public RoomServiceTest() {}
    
    private void setupTestData() {
        mockDao.clear();
        
        BuildingDto building1 = new BuildingDto(1);
        BuildingDto building2 = new BuildingDto(2);
        
        mockDao.addTestRoom(new RoomDto(1, building1, 101, 1, 2, 100.0f, "room1.jpg", "Room 101"));
        mockDao.addTestRoom(new RoomDto(2, building1, 102, 1, 3, 150.0f, "room2.jpg", "Room 102"));
        mockDao.addTestRoom(new RoomDto(3, building1, 201, 2, 2, 120.0f, "room3.jpg", "Room 201"));
        mockDao.addTestRoom(new RoomDto(4, building2, 301, 3, 4, 200.0f, "room4.jpg", "Room 301"));
        mockDao.addTestRoom(new RoomDto(5, building2, 302, 3, 2, 180.0f, "room5.jpg", "Room 302"));
    }
    
    // ============= Тесты для createRoom =============
    
    public void testCreateRoom() {
        setupTestData();
        int initialCount = mockDao.getRoomsCountWithFilters(null, null);
        
        roomService.createRoom(401, 1, 1);
        
        int finalCount = mockDao.getRoomsCountWithFilters(null, null);
        assertThat(finalCount, "Количество комнат после создания")
            .isEqualTo(initialCount + 1);
        
        System.out.println("+ testCreateRoom passed");
    }
    
    // ============= Тесты для deleteRoomById =============
    
    public void testDeleteRoomById() {
        setupTestData();
        
        roomService.deleteRoomById(1);
        
        RoomDto deletedRoom = mockDao.getRoomById(1);
        assertThat(deletedRoom, "Удаленная комната").isNull();
        
        System.out.println("+ testDeleteRoomById passed");
    }
    
    // ============= Тесты для getPageCountForSuper =============
    
    public void testGetPageCountForSuper() {
        // Тест с точным делением
        Integer count1 = roomService.getPageCountForSuper(8);
        assertThat(count1, "8 комнат по 4 на страницу").isEqualTo(2);
        
        // Тест с остатком
        Integer count2 = roomService.getPageCountForSuper(10);
        assertThat(count2, "10 комнат по 4 на страницу").isEqualTo(3);
        
        // Тест с пустым списком
        Integer count3 = roomService.getPageCountForSuper(0);
        assertThat(count3, "0 комнат").isEqualTo(0);
        
        System.out.println("+ testGetPageCountForSuper passed");
    }
    
    // ============= Тесты для getRoomsCountForSuperWithFilters =============
    
    public void testGetRoomsCountForSuperWithFilters() {
        setupTestData();
        
        // Без фильтров
        Integer allCount = roomService.getRoomsCountForSuperWithFilters(null, null);
        assertThat(allCount, "Все комнаты").isEqualTo(5);
        
        // Фильтр по этажу
        Integer floor1Count = roomService.getRoomsCountForSuperWithFilters(1, null);
        assertThat(floor1Count, "Комнаты на 1 этаже").isEqualTo(2);
        
        // Фильтр по зданию
        Integer building1Count = roomService.getRoomsCountForSuperWithFilters(null, 1);
        assertThat(building1Count, "Комнаты в здании 1").isEqualTo(3);
        
        // Комбинированный фильтр
        Integer combinedCount = roomService.getRoomsCountForSuperWithFilters(3, 2);
        assertThat(combinedCount, "Комнаты этаж 3 в здании 2").isEqualTo(2);
        
        System.out.println("+ testGetRoomsCountForSuperWithFilters passed");
    }
    
    // ============= Тесты для getRoomsForSuperWithFilters =============
    
    public void testGetRoomsForSuperWithFilters() {
        setupTestData();
        
        // Тест с пагинацией
        List<RoomDto> page1 = roomService.getRoomsForSuperWithFilters(null, null, 0);
        assertThat(page1.size(), "Первая страница").isEqualTo(4);
        
        List<RoomDto> page2 = roomService.getRoomsForSuperWithFilters(null, null, 1);
        assertThat(page2.size(), "Вторая страница").isEqualTo(1);
        
        // Тест с фильтром
        List<RoomDto> filtered = roomService.getRoomsForSuperWithFilters(1, 1, 0);
        assertThat(filtered.size(), "Отфильтрованные комнаты").isEqualTo(2);
        
        System.out.println("+ testGetRoomsForSuperWithFilters passed");
    }
    
    // ============= Тесты для updateRoom =============
    
    public void testUpdateRoomSuccess() {
        setupTestData();
        
        roomService.updateRoom(1, 2, 999, 5, 10, 500.0f, "new.jpg", "New description");
        
        RoomDto updatedRoom = mockDao.getRoomById(1);
        assertThat(updatedRoom, "Обновленная комната")
            .isNotNull();
        assertThat(updatedRoom.getBuilding().getId(), "ID здания").isEqualTo(2);
        assertThat(updatedRoom.getNumber(), "Номер комнаты").isEqualTo(999);
        assertThat(updatedRoom.getFloor(), "Этаж").isEqualTo(5);
        assertThat(updatedRoom.getSleepingPlaces(), "Спальные места").isEqualTo(10);
        assertThat(updatedRoom.getPrice(), "Цена").isEqualTo(500.0f);
        
        System.out.println("+ testUpdateRoomSuccess passed");
    }
    
    public void testUpdateRoomWithNullRoomId() {
        setupTestData();
        
        assertThrows(RuntimeException.class, () -> {
            roomService.updateRoom(null, 1, 101, 1, 2, 100.0f, "pic.jpg", "desc");
        });
        
        System.out.println("+ testUpdateRoomWithNullRoomId passed");
    }
    
    public void testUpdateRoomWithNullBuildingId() {
        setupTestData();
        
        assertThrows(RuntimeException.class, () -> {
            roomService.updateRoom(1, null, 101, 1, 2, 100.0f, "pic.jpg", "desc");
        });
        
        System.out.println("+ testUpdateRoomWithNullBuildingId passed");
    }
    
    // ============= Тесты для getRoomByIdWithStatus =============
    
    public void testGetRoomByIdWithStatus() {
        setupTestData();
        
        RoomDto room = roomService.getRoomByIdWithStatus(1);
        assertThat(room, "Найденная комната").isNotNull();
        assertThat(room.getId(), "ID комнаты").isEqualTo(1);
        
        RoomDto notFound = roomService.getRoomByIdWithStatus(999);
        assertThat(notFound, "Несуществующая комната").isNull();
        
        System.out.println("+ testGetRoomByIdWithStatus passed");
    }
    
    // ============= Тесты для getRoomById =============
    
    public void testGetRoomById() {
        setupTestData();
        
        RoomDto room = roomService.getRoomById(2);
        assertThat(room, "Найденная комната").isNotNull();
        assertThat(room.getNumber(), "Номер комнаты").isEqualTo(102);
        
        RoomDto notFound = roomService.getRoomById(999);
        assertThat(notFound, "Несуществующая комната").isNull();
        
        System.out.println("+ testGetRoomById passed");
    }
    
    // ============= Тесты для getMaxGuests =============
    
    public void testGetMaxGuests() {
        setupTestData();
        
        Integer maxGuests = roomService.getMaxGuests();
        assertThat(maxGuests, "Максимальное количество гостей").isEqualTo(4);
        
        System.out.println("+ testGetMaxGuests passed");
    }
    
    // ============= Тесты для getPageCount =============
    
    public void testGetPageCount() {
        Integer count1 = roomService.getPageCount(8);
        assertThat(count1, "8 комнат").isEqualTo(2);
        
        Integer count2 = roomService.getPageCount(10);
        assertThat(count2, "10 комнат").isEqualTo(3);
        
        Integer count3 = roomService.getPageCount(0);
        assertThat(count3, "0 комнат").isEqualTo(0);
        
        System.out.println("+ testGetPageCount passed");
    }
    
    // ============= Тесты для getCount =============
    
    public void testGetCountWithValidParams() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("guests", new String[]{"3"});
        params.put("floor", new String[]{"1"});
        params.put("building", new String[]{"1"});
        
        Integer count = roomService.getCount(params);
        assertThat(count, "Количество комнат с фильтрами").isEqualTo(1); // Только комната 2
        
        System.out.println("+ testGetCountWithValidParams passed");
    }
    
    public void testGetCountWithInvalidNumberFormat() {
        Map<String, String[]> params = new HashMap<>();
        params.put("guests", new String[]{"invalid"});
        
        assertThrowsWithMessage(RuntimeException.class, 
            "One of the parameters is incorrect", 
            () -> roomService.getCount(params));
        
        System.out.println("+ testGetCountWithInvalidNumberFormat passed");
    }
    
    public void testGetCountWithEmptyParams() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        Integer count = roomService.getCount(params);
        assertThat(count, "Все комнаты").isEqualTo(5);
        
        System.out.println("+ testGetCountWithEmptyParams passed");
    }
    
    // ============= Тесты для getPage =============
    
    public void testGetPageWithDefaultPageNumber() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("guests", new String[]{"2"});
        
        List<RoomDto> page = roomService.getPage(params);
        assertThat(page.size(), "Первая страница").isEqualTo(4);
        
        System.out.println("+ testGetPageWithDefaultPageNumber passed");
    }
    
    public void testGetPageWithCustomPageNumber() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("page", new String[]{"1"});
        params.put("guests", new String[]{"2"});
        
        List<RoomDto> page = roomService.getPage(params);
        assertThat(page.size(), "Вторая страница").isEqualTo(1);
        
        System.out.println("+ testGetPageWithCustomPageNumber passed");
    }
    
    public void testGetPageWithInvalidParams() {
        Map<String, String[]> params = new HashMap<>();
        params.put("price_min", new String[]{"invalid"});
        
        assertThrows(RuntimeException.class, () -> roomService.getPage(params));
        
        System.out.println("+ testGetPageWithInvalidParams passed");
    }
    
    // ============= Тесты для getRoomsPageForAdmin =============
    
    public void testGetRoomsPageForAdminWithValidParams() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("page", new String[]{"0"});
        params.put("guests", new String[]{"2"});
        params.put("status", new String[]{"FREE"});
        
        List<RoomDto> page = roomService.getRoomsPageForAdmin(params);
        assertThat(page, "Страница для админа").isNotNull();
        
        System.out.println("+ testGetRoomsPageForAdminWithValidParams passed");
    }
    
    public void testGetRoomsPageForAdminWithInvalidDateRange() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("checkin", new String[]{"2024-12-10"});
        params.put("checkout", new String[]{"2024-12-05"}); // checkout раньше checkin
        
        List<RoomDto> result = roomService.getRoomsPageForAdmin(params);
        assertThat(result, "Результат при некорректных датах").isNotNull();
        assertThat(result.isEmpty(), "Пустой список").isTrue();
        
        System.out.println("+ testGetRoomsPageForAdminWithInvalidDateRange passed");
    }
    
    public void testGetRoomsPageForAdminWithInvalidStatus() {
        Map<String, String[]> params = new HashMap<>();
        params.put("status", new String[]{"INVALID"});
        
        assertThrowsWithMessage(RuntimeException.class,
            "Invalid status value. Use FREE or BUSY",
            () -> roomService.getRoomsPageForAdmin(params));
        
        System.out.println("+ testGetRoomsPageForAdminWithInvalidStatus passed");
    }
    
    // ============= Тесты для getRoomsCountForAdmin =============
    
    public void testGetRoomsCountForAdminWithValidParams() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("guests", new String[]{"3"});
        params.put("floor", new String[]{"1"});
        
        Integer count = roomService.getRoomsCountForAdmin(params);
        assertThat(count, "Количество комнат для админа").isEqualTo(1);
        
        System.out.println("+ testGetRoomsCountForAdminWithValidParams passed");
    }
    
    public void testGetRoomsCountForAdminWithInvalidDateRange() {
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("checkin", new String[]{"2024-12-10"});
        params.put("checkout", new String[]{"2024-12-05"});
        
        Integer count = roomService.getRoomsCountForAdmin(params);
        assertThat(count, "При некорректных датах").isEqualTo(0);
        
        System.out.println("+ testGetRoomsCountForAdminWithInvalidDateRange passed");
    }
    
    public void testGetRoomsCountForAdminWithInvalidParams() {
        Map<String, String[]> params = new HashMap<>();
        params.put("price_min", new String[]{"not-a-number"});
        
        assertThrows(RuntimeException.class, 
            () -> roomService.getRoomsCountForAdmin(params));
        
        System.out.println("+ testGetRoomsCountForAdminWithInvalidParams passed");
    }
    
    // ============= Запуск всех тестов =============
    
    public void runAllTests() {
        this.mockDao = new MockRoomDao();
        this.roomService = RoomService.getRoomService();
        try {
            Field daoField = RoomService.class.getDeclaredField("roomDao");
            daoField.setAccessible(true);
            daoField.set(roomService, mockDao);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Запуск модульных тестов RoomService ===\n");
        
        testCreateRoom();
        testDeleteRoomById();
        testGetPageCountForSuper();
        testGetRoomsCountForSuperWithFilters();
        testGetRoomsForSuperWithFilters();
        testUpdateRoomSuccess();
        testUpdateRoomWithNullRoomId();
        testUpdateRoomWithNullBuildingId();
        testGetRoomByIdWithStatus();
        testGetRoomById();
        testGetMaxGuests();
        testGetPageCount();
        testGetCountWithValidParams();
        testGetCountWithInvalidNumberFormat();
        testGetCountWithEmptyParams();
        testGetPageWithDefaultPageNumber();
        testGetPageWithCustomPageNumber();
        testGetPageWithInvalidParams();
        testGetRoomsPageForAdminWithValidParams();
        testGetRoomsPageForAdminWithInvalidDateRange();
        testGetRoomsPageForAdminWithInvalidStatus();
        testGetRoomsCountForAdminWithValidParams();
        testGetRoomsCountForAdminWithInvalidDateRange();
        testGetRoomsCountForAdminWithInvalidParams();
        
        System.out.println("\n=== Все модульные тесты RoomService успешно пройдены! ===\n");
    }

    private static class MockRoomDao extends RoomDao {
    
        private List<RoomDto> rooms = new ArrayList<>();
        private Map<Integer, RoomDto> roomsById = new HashMap<>();
        private int nextId = 1;
        
        public void addTestRoom(RoomDto room) {
            rooms.add(room);
            roomsById.put(room.getId(), room);
            if (room.getId() >= nextId) {
                nextId = room.getId() + 1;
            }
        }
        
        public void clear() {
            rooms.clear();
            roomsById.clear();
            nextId = 1;
        }
        
        @Override
        public void createRoom(Integer roomNumber, Integer floor, Integer buildingId) {
            RoomDto room = new RoomDto(
                nextId++,
                new BuildingDto(buildingId),
                roomNumber,
                floor,
                2,  // sleepingPlaces по умолчанию
                100.0f, // цена по умолчанию
                "default.jpg",
                "Default description"
            );
            rooms.add(room);
            roomsById.put(room.getId(), room);
        }
        
        @Override
        public void deleteRoom(Integer roomId) {
            rooms.removeIf(room -> room.getId().equals(roomId));
            roomsById.remove(roomId);
        }
        
        @Override
        public Integer getRoomsCountWithFilters(Integer floorFilter, Integer buildingIdFilter) {
            return (int) rooms.stream()
                .filter(room -> floorFilter == null || room.getFloor().equals(floorFilter))
                .filter(room -> buildingIdFilter == null || room.getBuilding().getId().equals(buildingIdFilter))
                .count();
        }
        
        @Override
        public List<RoomDto> getRoomsWithFilters(Integer floorFilter, Integer buildingIdFilter, 
                                                Integer pageNumber, Integer pageSize) {
            return rooms.stream()
                .filter(room -> floorFilter == null || room.getFloor().equals(floorFilter))
                .filter(room -> buildingIdFilter == null || room.getBuilding().getId().equals(buildingIdFilter))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .toList();
        }
        
        @Override
        public void updateRoom(RoomDto room) {
            roomsById.put(room.getId(), room);
            int index = rooms.indexOf(roomsById.get(room.getId()));
            if (index >= 0) {
                rooms.set(index, room);
            }
        }
        
        @Override
        public RoomDto getRoomByIdWithStatus(Integer roomId) {
            RoomDto room = roomsById.get(roomId);
            if (room != null) {
                // Создаем копию с статусом для тестов
                return new RoomDto(
                    room.getId(),
                    room.getBuilding(),
                    room.getNumber(),
                    room.getFloor(),
                    room.getSleepingPlaces(),
                    room.getPrice(),
                    room.getPicture(),
                    room.getDescription()
                );
            }
            return null;
        }
        
        @Override
        public RoomDto getRoomById(Integer roomId) {
            return roomsById.get(roomId);
        }
        
        @Override
        public Integer getMaxSleepingPlaces() {
            return rooms.stream()
                .mapToInt(RoomDto::getSleepingPlaces)
                .max()
                .orElse(0);
        }
        
        @Override
        public Integer getCout(LocalDate checkin, LocalDate checkout, Integer guests, 
                            Integer floor, Integer buildingId, Float minPrice, Float maxPrice) {
            return (int) rooms.stream()
                .filter(room -> guests == null || room.getSleepingPlaces() >= guests)
                .filter(room -> floor == null || room.getFloor().equals(floor))
                .filter(room -> buildingId == null || room.getBuilding().getId().equals(buildingId))
                .filter(room -> minPrice == null || room.getPrice() >= minPrice)
                .filter(room -> maxPrice == null || room.getPrice() <= maxPrice)
                .count();
        }
        
        @Override
        public List<RoomDto> getPage(Integer pageNumber, Integer pageSize, LocalDate checkin, 
                                    LocalDate checkout, Integer guests, Integer floor, 
                                    Integer buildingId, Float minPrice, Float maxPrice) {
            return rooms.stream()
                .filter(room -> guests == null || room.getSleepingPlaces() >= guests)
                .filter(room -> floor == null || room.getFloor().equals(floor))
                .filter(room -> buildingId == null || room.getBuilding().getId().equals(buildingId))
                .filter(room -> minPrice == null || room.getPrice() >= minPrice)
                .filter(room -> maxPrice == null || room.getPrice() <= maxPrice)
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .toList();
        }
        
        @Override
        public List<RoomDto> getPageForAdmin(Integer pageNumber, Integer pageSize, LocalDate checkin,
                                            LocalDate checkout, Integer guests, Integer floor,
                                            Integer buildingId, Float minPrice, Float maxPrice,
                                            RoomStatus statusFilter) {
            return rooms.stream()
                .filter(room -> guests == null || room.getSleepingPlaces() >= guests)
                .filter(room -> floor == null || room.getFloor().equals(floor))
                .filter(room -> buildingId == null || room.getBuilding().getId().equals(buildingId))
                .filter(room -> minPrice == null || room.getPrice() >= minPrice)
                .filter(room -> maxPrice == null || room.getPrice() <= maxPrice)
                .filter(room -> statusFilter == null || 
                    (statusFilter == RoomStatus.FREE && room.getStatus() == null) ||
                    (statusFilter == RoomStatus.BUSY && room.getStatus() != null))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .toList();
        }
        
        @Override
        public Integer getCountForAdmin(LocalDate checkin, LocalDate checkout, Integer guests,
                                    Integer floor, Integer buildingId, Float minPrice, 
                                    Float maxPrice, RoomStatus status) {
            return (int) rooms.stream()
                .filter(room -> guests == null || room.getSleepingPlaces() >= guests)
                .filter(room -> floor == null || room.getFloor().equals(floor))
                .filter(room -> buildingId == null || room.getBuilding().getId().equals(buildingId))
                .filter(room -> minPrice == null || room.getPrice() >= minPrice)
                .filter(room -> maxPrice == null || room.getPrice() <= maxPrice)
                .filter(room -> status == null || 
                    (status == RoomStatus.FREE && room.getStatus() == null) ||
                    (status == RoomStatus.BUSY && room.getStatus() != null))
                .count();
        }
    }

}