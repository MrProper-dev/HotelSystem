package services;

import java.util.List;

import daos.AdminDao;
import dtos.AdminDto;

public class AdminService {

    private final Integer PAGE_SIZE = 4;

    private AdminDao adminDao = AdminDao.getAdminDao();

    private static final AdminService instance = new AdminService();
    private AdminService(){}
    public static AdminService getAdminService(){
        return instance;
    }

    public void deleteAdmin(Integer adminId){
        adminDao.deleteAdministrator(adminId);
    }

    public void updateAdmin(Integer adminId, String login, String password, String fullName, String phone){
        AdminDto admin = new AdminDto(adminId, login, password, fullName, phone);
        adminDao.updateAdministrator(admin);
    }

    public void createAdmin(String login, String password, String fullName, String phone){
        AdminDto adminDto = new AdminDto(login, password, fullName, phone);
        adminDao.addAdministrator(adminDto);
    }

    public Integer getPageCount(Integer adminsCount){
        Integer pageCount = adminsCount / PAGE_SIZE;
        if(adminsCount % PAGE_SIZE != 0){
            pageCount++;
        }
        return pageCount;
    }

    public Integer getAdminsCountWithFilters(String login, String fullName, String phone){
        if(login != null && login.isEmpty()){
            login = null;
        }
        if(fullName != null && fullName.isEmpty()){
            fullName = null;
        }
        if(phone != null && phone.isEmpty()){
            phone = null;
        }
        return adminDao.getAdminsCountWithFilters(login, fullName, phone);
    }

    public List<AdminDto> getAdminsWithFilters(String login, String fullName, String phone, Integer page){
        if(login != null && login.isEmpty()){
            login = null;
        }
        if(fullName != null && fullName.isEmpty()){
            fullName = null;
        }
        if(phone != null && phone.isEmpty()){
            phone = null;
        }
        return adminDao.getAdminsWithFilters(login, fullName, phone, page, PAGE_SIZE);
    }

    public void updateAdminLastLogin(Integer adminId){
        adminDao.updateAdminLastLogin(adminId);
    }

    public Integer getAdmintId(String login, String password){
        if (login == null || password == null) {
            throw new RuntimeException("Login and password cannot be null");
        }
        if(login.isEmpty() || password.isEmpty()){
            throw new RuntimeException("Login and password cannot be empty");
        }
        AdminDto admin = adminDao.getAdminByLoginAndPassword(login, password);
        if(admin == null){
            return null;
        }else{
            return admin.getId();
        }
    }

}
