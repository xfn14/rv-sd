package Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer {

    private final Connection conn;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<Long, FrameValue> map = new HashMap<>();
    private IOException exception = null;

    private class FrameValue {
        int waiters = 0;
        Queue<byte[]> queue = new ArrayDeque<>();
        Condition cond = lock.newCondition();
    }

    public Demultiplexer(Connection conn) {
        this.conn = conn;
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Connection.Frame frame = conn.receive();
                    lock.lock();
                    try {
                        FrameValue fv = map.get(frame.threadId);
                        if (fv == null) {
                            fv = new FrameValue();
                            map.put(frame.threadId, fv);
                        }
                        fv.queue.add(frame.data);
                        fv.cond.signal();
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }
            catch (IOException e) {
                exception = e;
            }
        }).start();
    }

    public void send(Connection.Frame frame) throws IOException {
        conn.send(frame);
    }

    public void send(int tag, ByteArrayOutputStream byteArray) throws IOException {
        conn.send(tag, byteArray);
    }

    public void send(int tag, byte[] data) throws IOException {
        conn.send(tag, data);
    }

    public byte[] receive() throws IOException {
        lock.lock();
        FrameValue fv;
        try {
            long threadId = Thread.currentThread().getId();
            fv = map.get(threadId);
            if (fv == null) {
                fv = new FrameValue();
                map.put(threadId, fv);
            }
            fv.waiters++;
            while(true) {
                if(! fv.queue.isEmpty()) {
                    fv.waiters--;
                    byte[] reply = fv.queue.poll();
                    if (fv.waiters == 0 && fv.queue.isEmpty())
                        map.remove(threadId);
                    return reply;
                }
                if (exception != null) {
                    throw exception;
                }
                fv.cond.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return null;
    }


    public void close() throws IOException {
        conn.close();
    }
}