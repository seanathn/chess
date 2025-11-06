import server.Server;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService chessService = new UserService();
        Server server = new Server(chessService);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}