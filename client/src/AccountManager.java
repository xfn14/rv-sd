import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AccountManager {

    private Map<String,Account> accounts;
    private ReentrantLock lock = new ReentrantLock();

    public void createAccount(String username, String password){
        this.lock.lock();
        try {
            this.accounts.put(username, new Account(username, password));
        }finally {
            this.lock.unlock();
        }

    }
}
