package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.user.UsersTable;
import org.junit.jupiter.api.Disabled;

import java.sql.SQLException;
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
        final ConcurrentTestRunnable getRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, N, users, user -> UsersTable.getUserByUserId(user.getId()));
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
        final ConcurrentTestRunnable putRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, N, users, UsersTable::putUser);
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
        final ConcurrentTestRunnable deleteRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, N, users, UsersTable::deleteUser);
        for (int i = 1; i <= nThreads; ++i) {
            new Thread(deleteRunnable, "DeleteThread-" + i).start();
        }
        barrierOnEnd.await();
    }

    @Disabled("Работяга умеет в конкурентность! 1 секунда, 4к операций, 54 потока...")
    @DBTest(nUsers = 4000)
    void concurrentCombined(List<User> users) throws BrokenBarrierException, InterruptedException {
        final int NGets = 4000;
        final int NPuts = 20;
        final int NDeletes = 100;
        final int nRunnables = 3;
        final int nThreads = 18 * nRunnables;
        final CyclicBarrier barrierOnStart = new CyclicBarrier(nThreads);
        final CyclicBarrier barrierOnEnd = new CyclicBarrier(nThreads + 1);
        final ConcurrentTestRunnable getRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, NGets, users, user -> UsersTable.getUserByUserId(user.getId()));
        final ConcurrentTestRunnable putRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, NPuts, users, UsersTable::putUser);
        final ConcurrentTestRunnable deleteRunnable
                = new ConcurrentTestRunnable(barrierOnStart, barrierOnEnd, nThreads, NDeletes, users, UsersTable::deleteUser);
        for (int i = 1; i <= nThreads / nRunnables; ++i) {
            new Thread(getRunnable, "GetThread-" + i).start();
            new Thread(putRunnable, "PutThread-" + i).start();
            new Thread(deleteRunnable, "DeleteThread-" + i).start();
        }

        barrierOnEnd.await();
    }

    static class ConcurrentTestRunnable implements Runnable {

        private final CyclicBarrier barrierOnStart;
        private final CyclicBarrier barrierOnEnd;
        private final int nThreads;
        private final int N;
        private final List<User> users;
        private final SQLOperation operation;

        ConcurrentTestRunnable(CyclicBarrier barrierOnStart, CyclicBarrier barrierOnEnd, int nThreads, int N,
                               List<User> users, SQLOperation operation) {
            this.barrierOnStart = barrierOnStart;
            this.barrierOnEnd = barrierOnEnd;
            this.nThreads = nThreads;
            this.N = N;
            this.users = users;
            this.operation = operation;
        }

        @Override
        public void run() {
            try {
                barrierOnStart.await();
                long part = Thread.currentThread().getId() % nThreads;
                int from = (int) Math.floor(N * (double) part / nThreads);
                int to = (int) Math.floor(N * (double) (part + 1) / nThreads);
                List<User> sublist = users.subList(from, to);
                for (User user : sublist) {
                    operation.execute(user);
                }
                barrierOnEnd.await();
            } catch (InterruptedException | BrokenBarrierException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface SQLOperation {
        void execute(User user) throws SQLException;
    }

}
