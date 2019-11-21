package org.ajeet.learnings.concurrency.parkinglot;

import java.util.concurrent.Semaphore;

public final class ParkingLotSemaphore {

    private static class Parking {
        private final Semaphore slots;
        private final int numOfParkingSlots;

        private Parking(int numOfParkingSlots) {
            this.numOfParkingSlots = numOfParkingSlots;
            this.slots = new Semaphore(numOfParkingSlots);
        }

        private void bookSlot(Vehicle vehicle){
            if(slots.availablePermits() == 0){
                System.out.println(" ================== Parking is full =================== ");
                System.out.println(vehicle.vehicleNumber + " is waiting for parking.");
            }
            try {
                slots.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void releaseSlot(Vehicle vehicle){
            slots.release();
            if(slots.availablePermits() == numOfParkingSlots){
                System.out.println(" ================== Parking is empty =================== ");
            }
        }
    }

    private static class Vehicle  extends Thread {
        private final Parking parking;
        private final int vehicleNumber;

        private Vehicle(Parking parking, int vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
            this.parking = parking;
        }

        @Override
        public void run() {
            park();
            try {
                //Just to replicate parked time.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            unpark();
        }

        private void park(){
            System.out.println("Vehicle " + vehicleNumber + " is in queue for parking !!!");
            parking.bookSlot(this);
            try {
                System.out.println("Vehicle " + vehicleNumber + " is moving in to parking !!!");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Vehicle " + vehicleNumber + " has been parked !!!");
        }

        private void unpark() {
            try {
                System.out.println("Vehicle " + vehicleNumber + " is moving from parking ");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            parking.releaseSlot(this);
            System.out.println("Vehicle " + vehicleNumber + " freed parking !!!");
        }
    }

    public static void main(String[] args) {
        Parking parking = new Parking(4);
        System.out.println("Parking has capacity for " + parking.numOfParkingSlots + " vehicles");
        for (int i=1; i< 8; i++){
            new Vehicle(parking, i).start();
        }
    }
}
