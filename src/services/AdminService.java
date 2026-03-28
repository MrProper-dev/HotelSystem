package services;

import daos.AdminDao;
import dtos.AdminDto;

public class AdminService {

    private AdminDao adminDao = AdminDao.getAdminDao();

    private static final AdminService instance = new AdminService();
    private AdminService(){}
    public static AdminService getAdminService(){
        return instance;
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
