package Cliente;

import Utils.UserNaoExisteException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AccountManager {

    private Map<String,Account> accounts;
    private ReentrantLock lock = new ReentrantLock();

    public AccountManager(){
        this.accounts = new HashMap<>();
    }

    public void createAccount(String username, String password){
        this.lock.lock();
        try {
            this.accounts.put(username, new Account(username, password));
        }finally {
            this.lock.unlock();
        }
    }

    public boolean login(String user, String pass) throws UserNaoExisteException{
        this.lock.lock();
        try{
            Account acc= this.accounts.get(user);
            if (acc == null)
                throw new UserNaoExisteException();

            return acc.getPassword().equals(pass);
        }finally {
            this.lock.unlock();
        }
    }
}
