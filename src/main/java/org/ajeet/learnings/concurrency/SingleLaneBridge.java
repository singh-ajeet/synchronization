package org.ajeet.learnings.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Problem statement:
 * There is a bridge which is aligned along the east-west direction. This bridge is too narrow to allow cars to go in both directions.
 * Hence, cars must alternate going across the bridge.The bridge is also not strong enough to hold more than three cars at a time.
 * Find a solution to this problem which does not cause starvation. That is, cars that want to get across should eventually get across.
 * However, we want to maximize use of the bridge.Cars should travel across to the maximum capacity of the bridge (that is, three cars should go at one time).
 * If a car leaves the bridge going east and there are no westbound cars, then the next eastbound car should be allowed to cross.
 * We don't want a solution which moves cars across the bridge three at a time, i.e.,
 * eastbound cars that are waiting should not wait until all three cars that are eastboung and crossing the bridge have crossed before being permitted to cross.
 *
 * https://www.cs.umd.edu/~hollings/cs412/s96/synch/eastwest.html
 * https://pages.mtu.edu/~shene/NSF-3/e-Book/MONITOR/Bridge/MON-example-bridge.html
 * https://users.cs.duke.edu/~chase/cps210-archive/cps110-probs-sol.pdf
 *
 */
public final class SingleLaneBridge {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    public static void main(String[] args) {
        Bridge bridge = new Bridge(4);
        for (int i=0; i< 2; i++){
            Thread leftThread = new Thread(createVehicleTask(bridge, Direction.LEFT));
            leftThread.start();
            try {
                TimeUnit.NANOSECONDS.sleep(100);
            } catch (InterruptedException e) {
                //Do Nothing
            }
            Thread rightThread = new Thread(createVehicleTask(bridge, Direction.RIGHT));
            rightThread.start();
        }
    }

    private static Runnable createVehicleTask(Bridge bridge, Direction direction) {
        return () -> {
            Vehicle vehicle = new Vehicle("Vehicle" + ATOMIC_INTEGER.getAndAdd(1));
            try {
                bridge.enter(vehicle,direction);
                bridge.cross(vehicle, direction);
                bridge.exit(vehicle, direction);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
    private static class Bridge {
        private final Lock LOCK = new ReentrantLock();
        private final Condition[] DIRECTION_SIGNALS = {LOCK.newCondition(), LOCK.newCondition()};

        private Direction currentDirection = Direction.LEFT;
        private int vehiclesOnBridge = 0;

        private final int capacity;

        private Bridge(int capacity) {
            this.capacity = capacity;
        }


        private void enter(Vehicle vehicle, Direction direction) throws InterruptedException {
            try{
                LOCK.lock();
                while(!canCross(direction)){
                    System.out.println("######====" + direction + "===="+ vehicle + "=====WAITING===######");
                    DIRECTION_SIGNALS[direction.ordinal()].await(); //Wait for signal to move
                }
                currentDirection = direction;
                vehiclesOnBridge++;
            } finally {
                LOCK.unlock();
            }
        }

        private void cross(Vehicle vehicle, Direction direction){
            System.out.println(">>>>====" + direction + "====="+ vehicle + "===CROSSING===>>>>>>>>>>");
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                //Do Nothing
            }
        }
        private boolean canCross(Direction direction) {
            if (!currentDirection.equals(direction))
                return false;
            else if (vehiclesOnBridge >= capacity)
                return false;
            return true;
        }

        private void exit(Vehicle vehicle, Direction direction){
            try{
                LOCK.lock();
                vehiclesOnBridge--;
                System.out.println(">>>>====" + direction + "====="+ vehicle + "===CROSSED===<<<<<<<");
                if (vehiclesOnBridge == 0) {
                    Direction oppositeDirection = direction.equals(Direction.LEFT) ? Direction.RIGHT: Direction.LEFT;
                    currentDirection  = oppositeDirection;
                    DIRECTION_SIGNALS[oppositeDirection.ordinal()].signalAll();
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

    private static enum Direction {
        LEFT,
        RIGHT;
    }

    /**
     * In real business scenarios this class will contain more attributes/fields
     */
    private static class Vehicle {
        private final String number;

        private Vehicle(String number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "Vehicle [" + number + ']';
        }
    }
}
