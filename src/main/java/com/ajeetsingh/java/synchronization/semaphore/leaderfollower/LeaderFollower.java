package com.ajeetsingh.java.synchronization.semaphore.leaderfollower;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * For example, imagine that threads represent ballroom dancers and that two
 * kinds of dancers, leaders and followers, wait in two queues before entering the
 * dance floor. When a leader arrives, it checks to see if there is a follower waiting.
 * If so, they can both proceed. Otherwise it waits.
 * Similarly, when a follower arrives, it checks for a leader and either proceeds
 * or waits, accordingly.
 *
 */
public final class LeaderFollower {
    private final LinkedList<String> leaders  = new LinkedList<>();
    private final LinkedList<String> followers  = new LinkedList<>();

    private final Lock floorPermission =  new ReentrantLock();

    private final Condition leaderIsAvailable =  floorPermission.newCondition();
    private final Condition followerIsAvailable =  floorPermission.newCondition();

    private static final AtomicInteger atomicInteger = new AtomicInteger();

    public void leaderArrivedForDance(String leaderName){
        System.out.println("Leader arrived for dance '" + leaderName + "'");
        floorPermission.lock();
        try {
            leaders.add(leaderName);
            leaderIsAvailable.signalAll();
            if(followers.isEmpty()){
                followerIsAvailable.await();
            }
            dance();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            floorPermission.unlock();
        }
    }

    public void followerArrivedForDance(String followerName){
        System.out.println("Follower arrived for dance '" + followerName + "'");

        floorPermission.lock();
        try {
            followers.add(followerName);
            followerIsAvailable.signalAll();
            if(leaders.isEmpty()){
                leaderIsAvailable.await();
            }
            dance();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            floorPermission.unlock();
        }
    }

    private void dance() {
        if(! leaders.isEmpty() & ! followers.isEmpty()){
            String leader = leaders.removeLast();
            String follower = followers.removeLast();
            System.out.println("*************** Leader: '" + leader + "' is dancing with follower: '" + follower + "' **************");
        }
        System.out.println("Nobody is avilable for dance. Waiting follower: " + followers + ", Waiting leaders: " + leaders);
     }

    public static void main(String[] args) {
        LeaderFollower leaderFollower = new LeaderFollower();

        for (int i =0; i< 5; i++){
            Thread leader = new Thread(() -> leaderFollower.leaderArrivedForDance("Leader_" + atomicInteger.getAndIncrement()));
            Thread follower = new Thread(() -> leaderFollower.followerArrivedForDance("Follower_" + atomicInteger.getAndIncrement()));

            leader.start();
            follower.start();
        }
    }
}
