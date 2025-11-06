package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.response.LoginResult;
import model.response.RegisterResult;

import java.util.UUID;

public class UserService {

    DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }

        dataAccess.createUser(user);

        String token = generateToken();
        AuthData authData = new AuthData(token, user.username());

        dataAccess.createAuth(authData);

        return new RegisterResult(user.username(), token);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {

        UserData user = dataAccess.getUser(loginRequest.username());

        if (user == null) {
            throw new DataAccessException("bad request");
        }

        if (!user.password().equals(loginRequest.password())) {
            throw new DataAccessException("bad request");
        }

        AuthData authData = new AuthData(loginRequest.username(), generateToken());
        dataAccess.createAuth(authData);

        return new LoginResult(user.username(), authData.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        dataAccess.deleteAuth(logoutRequest.authToken());
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
