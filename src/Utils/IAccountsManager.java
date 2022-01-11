package Utils;


public interface IAccountsManager {
    int NOT_REGISTED = -1;
    int INVALID_CREDENTIALS = 0;
    int NORMAL_ACCOUNT = 1;
    int ADMINISTRATOR_ACCOUNT = 2;

    void createAccount(String username, String password);

    int login(String username, String password);

    boolean isAdmin(String username);
}
