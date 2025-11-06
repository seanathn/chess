package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.response.LoginResult;
import model.response.RegisterResult;

public class UserService {

    DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        UserData user = new UserData("placeholder", "placeholder", "placeholder");

        dataAccess.createUser(user);

        return new RegisterResult();
    }

    public LoginResult login(LoginRequest loginRequest) {


        return new LoginResult();
    }

    public void logout(LogoutRequest logoutRequest) {

    }
}
