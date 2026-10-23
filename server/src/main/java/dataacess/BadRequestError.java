package dataacess;

public class BadRequestError extends DataAccessException {
    public BadRequestError() {
        super("Error: bad request");
    }
}
