package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UsersTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTest {

    @Disabled("База поддерживает 4.5K GET-Rps в многопоток")
    @DBTest(nUsers = 4500)
    void concurrentGet(List<User> users) throws BrokenBarrierException, InterruptedException {
        final int N = users.size();
        final int nThreads = 100;
        final CyclicBarrier barrierOnStart = new CyclicBarrier(nThreads);
        final CyclicBarrier barrierOnEnd = new CyclicBarrier(nThreads + 1);
        final Runnable getRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(N * (double) part / nThreads);
                int to = (int) Math.floor(N * (double) (part + 1) / nThreads);
                List<User> sublist = users.subList(from, to);
                for (User user : sublist) {
                    UsersTable.getUserByUserId(user.getUserId());
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 1; i <= nThreads; ++i) {
            new Thread(getRunnable, "GetThread-" + i).start();
        }

        barrierOnEnd.await();
    }

    @Disabled("База поддерживает 20 PUT-Rps в многопоток")
    @DBTest(nUsers = 0)
    void concurrentPut(List<User> users) throws BrokenBarrierException, InterruptedException {
        final int N = 20;
        final int nThreads = 10;
        final CyclicBarrier barrierOnStart = new CyclicBarrier(nThreads);
        final CyclicBarrier barrierOnEnd = new CyclicBarrier(nThreads + 1);
        final Runnable putRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(N * (double) part / nThreads) + 1;
                int to = (int) Math.floor(N * (double) (part + 1) / nThreads) + 1;
                for (int i = from; i < to; ++i) {
                    User user = new User(i, "City" + i, UserState.DEFAULT.getId(), -1);
                    UsersTable.putUser(user);
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 1; i <= nThreads; ++i) {
            new Thread(putRunnable, "PutThread-" + i).start();
        }

        barrierOnEnd.await();
    }

    @Disabled("База поддерживает 100 DELETE-Rps в многопоток")
    @DBTest(nUsers = 100)
    void concurrentDelete(List<User> users) throws BrokenBarrierException, InterruptedException {
        final int N = users.size();
        final int nThreads = 10;
        final CyclicBarrier barrierOnStart = new CyclicBarrier(nThreads);
        final CyclicBarrier barrierOnEnd = new CyclicBarrier(nThreads + 1);
        final AtomicInteger userId = new AtomicInteger();
        final Runnable deleteRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(N * (double) part / nThreads);
                int to = (int) Math.floor(N * (double) (part + 1) / nThreads);
                List<User> sublist = users.subList(from, to);
                for (User user : sublist) {
                    UsersTable.deleteUser(user);
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 1; i <= nThreads; ++i) {
            new Thread(deleteRunnable, "DeleteThread-" + i).start();
        }

        barrierOnEnd.await();
    }

    @Disabled("Работяга умеет в конкурентность! 1 секунда, 4к операций, 54 потока...")
    @DBTest(nUsers = 4000 + 20 + 100)
    void concurrentCombined(List<User> users) throws BrokenBarrierException, InterruptedException {
        final int NGets = 4000;
        final int NPuts = 20;
        final int NDeletes = 100;
        final int nRunnables = 3;
        final int nThreads = 18 * nRunnables;
        final CyclicBarrier barrierOnStart = new CyclicBarrier(nThreads);
        final CyclicBarrier barrierOnEnd = new CyclicBarrier(nThreads + 1);
        final Runnable getRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(NGets * (double) part / nThreads);
                int to = (int) Math.floor(NGets * (double) (part + 1) / nThreads);
                List<User> sublist = users.subList(from, to);
                for (User user : sublist) {
                    UsersTable.getUserByUserId(user.getUserId());
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };
        final Runnable putRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(NPuts * (double) part / nThreads) + 1;
                int to = (int) Math.floor(NPuts * (double) (part + 1) / nThreads) + 1;
                for (int i = NGets + from; i < NGets + to; ++i) {
                    User user = new User(i, "City" + i, UserState.DEFAULT.getId(), -1);
                    UsersTable.putUser(user);
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };
        final Runnable deleteRunnable = () -> {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(NDeletes * (double) part / nThreads);
                int to = (int) Math.floor(NDeletes * (double) (part + 1) / nThreads);
                List<User> sublist = users.subList(NGets + NPuts + from, NGets + NPuts + to);
                for (User user : sublist) {
                    UsersTable.deleteUser(user);
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 1; i <= nThreads / nRunnables; ++i) {
            new Thread(getRunnable, "GetThread-" + i).start();
            new Thread(putRunnable, "PutThread-" + i).start();
            new Thread(deleteRunnable, "DeleteThread-" + i).start();
        }

        barrierOnEnd.await();
    }

}
