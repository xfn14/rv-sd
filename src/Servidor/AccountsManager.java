package Servidor;

import Utils.IAccountsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AccountsManager implements IAccountsManager {
    private final Map<String, Account> accounts;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AccountsManager() {
        this.accounts = new HashMap<>();
        this.accounts.put("admin", new Account("admin", "admin"));
    }

    public boolean createAccount(String username, String password) {
        this.lock.writeLock().lock();
        try {
            if (!this.accounts.containsKey(username)) {
                this.accounts.put(username, new Account(username, password));
                return true;
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        return false;
    }

    public int login(String user, String pass) {
        this.lock.readLock().lock();
        try {
            Account acc = this.accounts.get(user);
            System.out.println("AMServer" + user + "-" + pass);

            if (acc == null)
                return NOT_REGISTED;

            if (isAdmin(user) && pass.equals("admin"))
                return ADMINISTRATOR_ACCOUNT;

            if (acc.getPassword().equals(pass))
                return NORMAL_ACCOUNT;
        } finally {
            this.lock.readLock().unlock();
        }
        return INVALID_CREDENTIALS;
    }

    public boolean isAdmin(String username) {
        return username.equals("admin");
    }
}
