package services;

import daos.SuperDao;
import dtos.SuperDto;

public class SuperService {

    private SuperDao superDao = SuperDao.getSuperDao();

    private static final SuperService instance = new SuperService();
    private SuperService(){}
    public static SuperService getSuperService(){
        return instance;
    }

    public Integer chekPassword(String login, String password){
        SuperDto superUser = superDao.getSuperUserByLoginAndPassword(login, password);
        if(superUser == null){
            return null;
        }else{
            return superUser.getId();
        }
    } 

    public void updateSuperLasttLogIn(Integer superId){
        superDao.updateSuperUserLastLogin(superId);
    }

}
