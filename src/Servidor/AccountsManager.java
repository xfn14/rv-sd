package Servidor;

import Utils.IAccountsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class AccountsManager implements IAccountsManager {

    private Map<String, Account> accounts;
    private ReentrantLock lock = new ReentrantLock();

    public AccountsManager(){
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

    public int login(String user, String pass) {
        this.lock.lock();
        try{
            Account acc= this.accounts.get(user);
            if (acc == null)
                return NOT_REGISTED;

            if (user.equals("admin") && pass.equals("admin"))
                return ADMINISTRATOR_ACCOUNT;

            if (acc.getPassword().equals(pass)) {
                return NORMAL_ACCOUNT;
            }
        }finally {
            this.lock.unlock();
        }

        return INVALID_CREDENTIALS;
    }
}
