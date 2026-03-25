package services;

import daos.ClientDao;
import dtos.ClientDto;

public class ClientService {

    private static final ClientService instance = new ClientService();
    private ClientService(){}
    public static ClientService getClientService(){
        return instance;
    }

    private ClientDao clientDao = ClientDao.getClientDao();

    public Integer createClient(String name, String phone, String email, String password){
        if (name == null || name.isEmpty() || phone == null || phone.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("One of the parameters is null");
        }
        ClientDto client = new ClientDto(email, password, phone, name);
        return clientDao.createClient(client).getId();
    }

    public Boolean exist(String email){
        if(email == null || email.isEmpty()){
            throw new RuntimeException("Email can`t be null");
        }
        return clientDao.existByEmail(email);
    }

    public Integer chekPassword(String email, String password){
        if(email == null || password == null){
            throw new RuntimeException("Email or password can`t be null");
        }
        ClientDto client = clientDao.getByEmail(email);
        if(client == null){
            return null;
        }
        if(client.getPassword().equals(password)){
            return client.getId();
        }else{
            return null;
        }
    }

}
