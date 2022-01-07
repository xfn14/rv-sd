package Servidor;

import java.util.concurrent.locks.ReentrantLock;

public class DailyBooking {
    private int ocupation;
    private final int capacity;
    private final int day;

    public ReentrantLock lock = new ReentrantLock();

    public DailyBooking(int day, int capacity){
        this.ocupation = 0;
        this.capacity = capacity;
        this.day = day;
    }

    public void addBooking(){
        this.ocupation++;
    }

    public boolean checkAvailabity(){
        return ocupation < capacity;
    }

    public void removeBooking() {
        this.ocupation--;
    }

    public int getDay() {
        return day;
    }
}
