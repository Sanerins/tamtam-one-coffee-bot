package one.coffee.sql.user;

public class UserService {

    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }
}
