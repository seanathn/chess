package dataaccess;

public class BadRequestError extends DataAccessException {
    public BadRequestError() {
        super("Error: bad request");
    }
}
