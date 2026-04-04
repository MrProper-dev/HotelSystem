package integration;

import daos.utils.ConnectionProviderFactory;
import daos.RoomDao;
import daos.utils.ConnectionProvider;
import dtos.RoomDto;
import services.RoomService;
import utils.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static utils.AssertionChecker.assertThat;
import static utils.AssertionChecker.ExceptionChecker.assertThrows;

public class RoomServiceIntegrationTest implements Test{
    
    private RoomService roomService;
    private ConnectionProvider connectionProvider;
    
    // Константы для тестовых данных
    private static final int TEST_BUILDING_ID = 1000;
    private static final int TEST_ROOM_START_ID = 10000;
    private static final int TEST_CLIENT_ID = 2000;
    private static final int TEST_BOOKING_ID = 3000;
    
    public RoomServiceIntegrationTest() {}

    private Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }
    
    /**
     * Подготовка тестовых данных в БД
     */
    private void setupTestData() throws SQLException {
        clearTestData();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Вставка тестового здания
                String insertBuilding = "INSERT INTO buildings (id, name, address, floors) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertBuilding)) {
                    pstmt.setInt(1, TEST_BUILDING_ID);
                    pstmt.setString(2, "Test Building");
                    pstmt.setString(3, "Test Address 123");
                    pstmt.setInt(4, 5);
                    pstmt.executeUpdate();
                }
                
                // Вставка тестовых комнат
                String insertRoom = """
                    INSERT INTO rooms (id, building_id, number, floor, sleeping_places, price, picture, description) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertRoom)) {
                    Object[][] rooms = {
                        {TEST_ROOM_START_ID + 1, TEST_BUILDING_ID, 101, 1, 2, 100.00, "room101.jpg", "Standard room with city view"},
                        {TEST_ROOM_START_ID + 2, TEST_BUILDING_ID, 102, 1, 3, 150.00, "room102.jpg", "Superior room with park view"},
                        {TEST_ROOM_START_ID + 3, TEST_BUILDING_ID, 201, 2, 2, 120.00, "room201.jpg", "Standard room"},
                        {TEST_ROOM_START_ID + 4, TEST_BUILDING_ID, 301, 3, 4, 200.00, "room301.jpg", "Family suite"},
                        {TEST_ROOM_START_ID + 5, TEST_BUILDING_ID, 302, 3, 2, 180.00, "room302.jpg", "Deluxe double room"},
                        {TEST_ROOM_START_ID + 6, TEST_BUILDING_ID, 401, 4, 5, 300.00, "room401.jpg", "Presidential suite"}
                    };
                    
                    for (Object[] room : rooms) {
                        pstmt.setInt(1, (Integer) room[0]);
                        pstmt.setInt(2, (Integer) room[1]);
                        pstmt.setInt(3, (Integer) room[2]);
                        pstmt.setInt(4, (Integer) room[3]);
                        pstmt.setInt(5, (Integer) room[4]);
                        pstmt.setDouble(6, (Double) room[5]);
                        pstmt.setString(7, (String) room[6]);
                        pstmt.setString(8, (String) room[7]);
                        pstmt.executeUpdate();
                    }
                }
                
                // Вставка тестового клиента для бронирований
                String insertClient = """
                    INSERT INTO clients (id, email, password, phone, name, is_blocked) 
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertClient)) {
                    pstmt.setInt(1, TEST_CLIENT_ID);
                    pstmt.setString(2, "test@example.com");
                    pstmt.setString(3, "hashed_password");
                    pstmt.setString(4, "+1234567890");
                    pstmt.setString(5, "Test Client");
                    pstmt.setBoolean(6, false);
                    pstmt.executeUpdate();
                }
                
                // Вставка тестовых бронирований
                String insertBooking = """
                    INSERT INTO bookings (id, client_id, room_id, check_in_date, check_out_date, total_price, status) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertBooking)) {
                    LocalDate today = LocalDate.now();
                    
                    // Бронирование для комнаты 10001 (занята)
                    pstmt.setInt(1, TEST_BOOKING_ID + 1);
                    pstmt.setInt(2, TEST_CLIENT_ID);
                    pstmt.setInt(3, TEST_ROOM_START_ID + 1);
                    pstmt.setDate(4, Date.valueOf(today.plusDays(1)));
                    pstmt.setDate(5, Date.valueOf(today.plusDays(3)));
                    pstmt.setDouble(6, 200.00);
                    pstmt.setString(7, "CONFIRMED");
                    pstmt.executeUpdate();
                    
                    // Бронирование для комнаты 10002 (занята)
                    pstmt.setInt(1, TEST_BOOKING_ID + 2);
                    pstmt.setInt(2, TEST_CLIENT_ID);
                    pstmt.setInt(3, TEST_ROOM_START_ID + 2);
                    pstmt.setDate(4, Date.valueOf(today.plusDays(2)));
                    pstmt.setDate(5, Date.valueOf(today.plusDays(5)));
                    pstmt.setDouble(6, 450.00);
                    pstmt.setString(7, "CONFIRMED");
                    pstmt.executeUpdate();
                    
                    // Бронирование для комнаты 10004 (занята)
                    pstmt.setInt(1, TEST_BOOKING_ID + 3);
                    pstmt.setInt(2, TEST_CLIENT_ID);
                    pstmt.setInt(3, TEST_ROOM_START_ID + 4);
                    pstmt.setDate(4, Date.valueOf(today.plusDays(1)));
                    pstmt.setDate(5, Date.valueOf(today.plusDays(4)));
                    pstmt.setDouble(6, 600.00);
                    pstmt.setString(7, "CONFIRMED");
                    pstmt.executeUpdate();
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Очистка тестовых данных
     */
    private void clearTestData() throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Удаляем тестовых гостей (сначала, из-за внешних ключей)
                String deleteGuests = """
                    DELETE FROM guests WHERE booking_id IN 
                    (SELECT id FROM bookings WHERE id >= ?)
                    """;
                try (PreparedStatement pstmt = conn.prepareStatement(deleteGuests)) {
                    pstmt.setInt(1, TEST_BOOKING_ID);
                    pstmt.executeUpdate();
                }
                
                // Удаляем тестовые бронирования
                String deleteBookings = "DELETE FROM bookings WHERE id >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteBookings)) {
                    pstmt.setInt(1, TEST_BOOKING_ID);
                    pstmt.executeUpdate();
                }
                
                // Удаляем тестовых клиентов
                String deleteClients = "DELETE FROM clients WHERE id >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteClients)) {
                    pstmt.setInt(1, TEST_CLIENT_ID);
                    pstmt.executeUpdate();
                }
                
                // Удаляем тестовые комнаты (каскадно удалит связанные данные)
                String deleteRooms = "DELETE FROM rooms WHERE id >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteRooms)) {
                    pstmt.setInt(1, TEST_ROOM_START_ID);
                    pstmt.executeUpdate();
                }
                
                // Удаляем тестовые здания
                String deleteBuildings = "DELETE FROM buildings WHERE id >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteBuildings)) {
                    pstmt.setInt(1, TEST_BUILDING_ID);
                    pstmt.executeUpdate();
                }
                
                conn.commit();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Проверка существования комнаты в БД
     */
    private boolean roomExists(Integer roomId) throws SQLException {
        String query = "SELECT COUNT(*) FROM rooms WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * Получение количества комнат без тестовых данных
     */
    private int getNonTestRoomsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM rooms WHERE id < ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, TEST_ROOM_START_ID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Получение ID последней созданной комнаты
     */
    private int getLastCreatedRoomId() throws SQLException {
        String query = "SELECT MAX(id) FROM rooms";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : -1;
        }
    }
    
    // ==================== Интеграционные тесты ====================
    
    /**
     * Тест создания комнаты
     */
    public void testCreateRoomIntegration() throws SQLException {
        
        setupTestData();
        int initialCount = getNonTestRoomsCount();
        
        // Создаем новую комнату
        roomService.createRoom(999, TEST_BUILDING_ID, 5);
        
        int finalCount = getNonTestRoomsCount();
        assertThat(finalCount, "Количество комнат после создания")
            .isEqualTo(initialCount + 1);
        
        // Получаем ID последней созданной комнаты
        int newRoomId = getLastCreatedRoomId();
        
        // Проверяем, что комната действительно создалась с правильными данными
        RoomDto createdRoom = roomService.getRoomById(newRoomId);
        assertThat(createdRoom, "Созданная комната").isNotNull();
        assertThat(createdRoom.getNumber(), "Номер комнаты").isEqualTo(999);
        assertThat(createdRoom.getFloor(), "Этаж").isEqualTo(5);
        assertThat(createdRoom.getBuilding().getId(), "ID здания").isEqualTo(TEST_BUILDING_ID);
        
        System.out.println("+ testCreateRoomIntegration passed");
    }
    
    /**
     * Тест удаления комнаты (с проверкой каскадного удаления)
     */
    public void testDeleteRoomByIdIntegration() throws SQLException {
        
        setupTestData();
        
        int testRoomId = TEST_ROOM_START_ID + 1;
        
        // Проверяем, что комната существует
        assertThat(roomExists(testRoomId), "Комната существует").isTrue();
        
        // Проверяем, что есть связанные бронирования
        String checkBookings = "SELECT COUNT(*) FROM bookings WHERE room_id = ?";
        int bookingsCount;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkBookings)) {
            pstmt.setInt(1, testRoomId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            bookingsCount = rs.getInt(1);
            assertThat(bookingsCount > 0, "Есть связанные бронирования").isTrue();
        }
        
        // Удаляем комнату
        roomService.deleteRoomById(testRoomId);
        
        // Проверяем, что комната удалена
        assertThat(roomExists(testRoomId), "Комната удалена").isFalse();
        
        // Проверяем, что связанные бронирования также удалены (ON DELETE CASCADE)
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkBookings)) {
            pstmt.setInt(1, testRoomId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            assertThat(rs.getInt(1), "Бронирования удалены").isEqualTo(0);
        }
        
        System.out.println("+ testDeleteRoomByIdIntegration passed");
    }
    
    /**
     * Тест обновления комнаты
     */
    public void testUpdateRoomIntegration() throws SQLException {
        
        setupTestData();
        
        int testRoomId = TEST_ROOM_START_ID + 1;
        
        // Обновляем комнату
        roomService.updateRoom(testRoomId, TEST_BUILDING_ID, 888, 10, 6, 500.0f, "updated.jpg", "Updated description");
        
        // Проверяем обновленные данные
        RoomDto updatedRoom = roomService.getRoomById(testRoomId);
        assertThat(updatedRoom, "Обновленная комната").isNotNull();
        assertThat(updatedRoom.getBuilding().getId(), "ID здания").isEqualTo(TEST_BUILDING_ID);
        assertThat(updatedRoom.getNumber(), "Номер комнаты").isEqualTo(888);
        assertThat(updatedRoom.getFloor(), "Этаж").isEqualTo(10);
        assertThat(updatedRoom.getSleepingPlaces(), "Спальные места").isEqualTo(6);
        assertThat(updatedRoom.getPrice(), "Цена").isEqualTo(500.0f);
        assertThat(updatedRoom.getPicture(), "Картинка").isEqualTo("updated.jpg");
        assertThat(updatedRoom.getDescription(), "Описание").isEqualTo("Updated description");
        
        System.out.println("+ testUpdateRoomIntegration passed");
    }
    
    /**
     * Тест обновления с null параметрами
     */
    public void testUpdateRoomWithNullParamsIntegration() throws SQLException {
        
        setupTestData();
        
        // Проверяем выброс исключения при null roomId
        assertThrows(RuntimeException.class, () -> {
            roomService.updateRoom(null, TEST_BUILDING_ID, 101, 1, 2, 100.0f, "pic.jpg", "desc");
        });
        
        // Проверяем выброс исключения при null buildingId
        assertThrows(RuntimeException.class, () -> {
            roomService.updateRoom(TEST_ROOM_START_ID + 1, null, 101, 1, 2, 100.0f, "pic.jpg", "desc");
        });
        
        System.out.println("+ testUpdateRoomWithNullParamsIntegration passed");
    }
    
    /**
     * Тест получения комнаты с статусом
     */
    public void testGetRoomByIdWithStatusIntegration() throws SQLException {
        
        setupTestData();
        
        // Комната с активным бронированием
        RoomDto busyRoom = roomService.getRoomByIdWithStatus(TEST_ROOM_START_ID + 1);
        assertThat(busyRoom, "Комната с бронированием").isNotNull();
        
        // Свободная комната (без бронирований) - комната 10003
        RoomDto freeRoom = roomService.getRoomByIdWithStatus(TEST_ROOM_START_ID + 3);
        assertThat(freeRoom, "Свободная комната").isNotNull();
        
        System.out.println("+ testGetRoomByIdWithStatusIntegration passed");
    }
    
    /**
     * Тест получения максимального количества гостей
     */
    public void testGetMaxGuestsIntegration() throws SQLException {
        
        setupTestData();
        
        Integer maxGuests = roomService.getMaxGuests();
        assertThat(maxGuests, "Максимум гостей").isEqualTo(5); // Комната 10006 имеет 5 мест
        
        System.out.println("+ testGetMaxGuestsIntegration passed");
    }
    
    /**
     * Тест получения количества комнат с фильтрами для супервайзера
     */
    public void testGetRoomsCountForSuperWithFiltersIntegration() throws SQLException {
        
        setupTestData();
        
        // Без фильтров (только тестовые комнаты)
        Integer allCount = roomService.getRoomsCountForSuperWithFilters(null, null);
        assertThat(allCount, "Все тестовые комнаты").isEqualTo(6);
        
        // Фильтр по этажу
        Integer floor1Count = roomService.getRoomsCountForSuperWithFilters(1, null);
        assertThat(floor1Count, "Комнаты на 1 этаже").isEqualTo(2);
        
        // Фильтр по зданию
        Integer buildingCount = roomService.getRoomsCountForSuperWithFilters(null, TEST_BUILDING_ID);
        assertThat(buildingCount, "Комнаты в тестовом здании").isEqualTo(6);
        
        // Комбинированный фильтр
        Integer combinedCount = roomService.getRoomsCountForSuperWithFilters(3, TEST_BUILDING_ID);
        assertThat(combinedCount, "Комнаты этаж 3 в тестовом здании").isEqualTo(2);
        
        System.out.println("+ testGetRoomsCountForSuperWithFiltersIntegration passed");/**
     * Тест получения комнат с фильтрами для супервайзера (с пагинацией)
     */
    }
    
    
    public void testGetRoomsForSuperWithFiltersIntegration() throws SQLException {
        setupTestData();
        
        // Первая страница (PAGE_SIZE = 4)
        List<RoomDto> page1 = roomService.getRoomsForSuperWithFilters(null, null, 0);
        assertThat(page1.size(), "Первая страница").isEqualTo(4);
        
        // Вторая страница
        List<RoomDto> page2 = roomService.getRoomsForSuperWithFilters(null, null, 1);
        assertThat(page2.size(), "Вторая страница").isEqualTo(2);
        
        // Страница с фильтром
        List<RoomDto> filteredPage = roomService.getRoomsForSuperWithFilters(1, TEST_BUILDING_ID, 0);
        assertThat(filteredPage.size(), "Отфильтрованная страница").isEqualTo(2);
        
        // Проверерка
        for (RoomDto room : filteredPage) {
            assertThat(room.getFloor(), "Этаж").isEqualTo(1);
            assertThat(room.getBuilding().getId(), "Здание").isEqualTo(TEST_BUILDING_ID);
        }
        
        System.out.println("+ testGetRoomsForSuperWithFiltersIntegration passed");
    }
    
    /**
     * Тест получения количества комнат с динамическими фильтрами
     */
    public void testGetCountIntegration() throws SQLException {
        
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        
        // Фильтр по количеству гостей
        params.put("guests", new String[]{"3"});
        Integer count1 = roomService.getCount(params);
        assertThat(count1, "Комнаты на 3+ гостей").isEqualTo(3); // комнаты с 3,4,5 спальными местами
        
        // Фильтр по цене
        params.clear();
        params.put("price_min", new String[]{"150"});
        params.put("price_max", new String[]{"250"});
        Integer count2 = roomService.getCount(params);
        assertThat(count2, "Комнаты с ценой 150-250").isEqualTo(3); // 150, 180, 200
        
        // Фильтр по этажу
        params.clear();
        params.put("floor", new String[]{"3"});
        Integer count3 = roomService.getCount(params);
        assertThat(count3, "Комнаты на 3 этаже").isEqualTo(2);
        
        // Фильтр по зданию
        params.clear();
        params.put("building", new String[]{String.valueOf(TEST_BUILDING_ID)});
        Integer count4 = roomService.getCount(params);
        assertThat(count4, "Комнаты в тестовом здании").isEqualTo(6);
        
        // Все фильтры вместе
        params.clear();
        params.put("guests", new String[]{"2"});
        params.put("floor", new String[]{"1"});
        params.put("building", new String[]{String.valueOf(TEST_BUILDING_ID)});
        Integer count5 = roomService.getCount(params);
        assertThat(count5, "Комнаты с комплексными фильтрами").isEqualTo(2); // 10001, 10002
        
        System.out.println("+ testGetCountIntegration passed");
    }
    
    /**
     * Тест получения страницы комнат с динамическими фильтрами
     */
    public void testGetPageIntegration() throws SQLException {
        
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("page", new String[]{"0"});
        params.put("guests", new String[]{"2"});
        
        List<RoomDto> page = roomService.getPage(params);
        assertThat(page.size(), "Размер страницы").isEqualTo(4);
        
        // Проверяем, что все комнаты на странице соответствуют фильтру
        for (RoomDto room : page) {
            assertThat(room.getSleepingPlaces() >= 2, "Спальных мест >= 2").isTrue();
        }
        
        // Проверяем вторую страницу
        params.put("page", new String[]{"1"});
        List<RoomDto> page2 = roomService.getPage(params);
        assertThat(page2.size(), "Размер второй страницы").isEqualTo(2);
        
        System.out.println("+ testGetPageIntegration passed");
    }
    
    /**
     * Тест получения комнат для админа с фильтрами по статусу
     */
    public void testGetRoomsPageForAdminIntegration() throws SQLException {
        
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("page", new String[]{"0"});
        params.put("status", new String[]{"FREE"});
        
        List<RoomDto> freeRooms = roomService.getRoomsPageForAdmin(params);
        assertThat(freeRooms, "Свободные комнаты").isNotNull();
        
        // Проверяем фильтр по статусу BUSY
        params.put("status", new String[]{"BUSY"});
        List<RoomDto> busyRooms = roomService.getRoomsPageForAdmin(params);
        assertThat(busyRooms, "Занятые комнаты").isNotNull();
        
        // Тест с некорректными датами (должен вернуть пустой список)
        params.clear();
        params.put("checkin", new String[]{LocalDate.now().plusDays(5).toString()});
        params.put("checkout", new String[]{LocalDate.now().plusDays(3).toString()});
        List<RoomDto> emptyResult = roomService.getRoomsPageForAdmin(params);
        assertThat(emptyResult.isEmpty(), "Пустой результат при некорректных датах").isTrue();
        
        // Тест с некорректным статусом
        params.clear();
        params.put("status", new String[]{"INVALID_STATUS"});
        
        assertThrows(RuntimeException.class, 
            () -> roomService.getRoomsPageForAdmin(params));
        
        System.out.println("+ testGetRoomsPageForAdminIntegration passed");
    }
    
    /**
     * Тест получения количества комнат для админа
     */
    public void testGetRoomsCountForAdminIntegration() throws SQLException {
        
        setupTestData();
        
        Map<String, String[]> params = new HashMap<>();
        params.put("status", new String[]{"FREE"});
        
        Integer freeCount = roomService.getRoomsCountForAdmin(params);
        assertThat(freeCount, "Количество свободных комнат").isGreaterThan(0);
        
        params.put("status", new String[]{"BUSY"});
        Integer busyCount = roomService.getRoomsCountForAdmin(params);
        assertThat(busyCount, "Количество занятых комнат").isGreaterThan(0);
        
        // Тест с некорректными датами (должен вернуть 0)
        params.clear();
        params.put("checkin", new String[]{LocalDate.now().plusDays(5).toString()});
        params.put("checkout", new String[]{LocalDate.now().plusDays(3).toString()});
        Integer zeroCount = roomService.getRoomsCountForAdmin(params);
        assertThat(zeroCount, "Ноль при некорректных датах").isEqualTo(0);
        
        System.out.println("+ testGetRoomsCountForAdminIntegration passed");
    }
    
    /**
     * Тест обработки некорректных параметров
     */
    public void testInvalidParametersIntegration() throws SQLException {
        
        setupTestData();
        
        // Некорректный формат числа
        Map<String, String[]> invalidNumberParams = new HashMap<>();
        invalidNumberParams.put("guests", new String[]{"invalid"});
        
        assertThrows(RuntimeException.class, 
            () -> roomService.getCount(invalidNumberParams));
        
        // Некорректный статус
        Map<String, String[]> invalidStatusParams = new HashMap<>();
        invalidStatusParams.put("status", new String[]{"INVALID_STATUS"});
        
        assertThrows(RuntimeException.class, 
            () -> roomService.getRoomsPageForAdmin(invalidStatusParams));
        
        System.out.println("+ testInvalidParametersIntegration passed");
    }
    
    /**
     * Тест пагинации с разными размерами страниц
     */
    public void testPaginationIntegration() throws SQLException {
        
        setupTestData();
        
        // Проверяем, что getPageCountForSuper работает корректно с реальными данными
        int totalRooms = 6; // Количество тестовых комнат
        Integer pageCount = roomService.getPageCountForSuper(totalRooms);
        
        int expectedPages = (int) Math.ceil(totalRooms / 4.0);
        assertThat(pageCount, "Количество страниц").isEqualTo(expectedPages);
        
        // Проверяем getPageCount
        Integer pageCount2 = roomService.getPageCount(totalRooms);
        assertThat(pageCount2, "getPageCount").isEqualTo(expectedPages);
        
        System.out.println("+ testPaginationIntegration passed");
    }
    
    public void runAllTests() {
        try {
            this.roomService = RoomService.getRoomService();
            Field conProv = RoomService.class.getDeclaredField("roomDao");
            conProv.setAccessible(true);
            conProv.set(roomService, RoomDao.getRoomDao());
            this.connectionProvider = ConnectionProviderFactory.getConnectionProvider();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("\n=== Запуск интеграционных тестов RoomService ===\n");
        
        try {
            testCreateRoomIntegration();
            testDeleteRoomByIdIntegration();
            testUpdateRoomIntegration();
            testUpdateRoomWithNullParamsIntegration();
            testGetRoomByIdWithStatusIntegration();
            testGetMaxGuestsIntegration();
            testGetRoomsCountForSuperWithFiltersIntegration();
            testGetRoomsForSuperWithFiltersIntegration();
            testGetCountIntegration();
            testGetPageIntegration();
            testGetRoomsPageForAdminIntegration();
            testGetRoomsCountForAdminIntegration();
            testInvalidParametersIntegration();
            testPaginationIntegration();
            
            System.out.println("\n=== Все интеграционные тесты RoomService успешно пройдены! ===\n");
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении интеграционных тестов: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clearTestData();
            } catch (SQLException e) {
                System.err.println("Ошибка при очистке тестовых данных: " + e.getMessage());
            }
        }
    }
    
}