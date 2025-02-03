/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.Random;

public class RandomWalk {
    
    private int x = 0;
    private int y = 0;
    private final Random random = new Random();

    /**
     * Method to compute the distance from the origin (the lamp-post) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        // d = sqrt(x^2 + y^2)
        return Math.sqrt((double) x * x + (double) y * y);
    }

    /**
     * Private method to move the current position, i.e., the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        for (int i = 0; i < m; i++) {
            randomMove();
        }
    }

    /**
     * Private method to generate a random move according to the rules:
     */
    private void randomMove() {
        boolean ns = random.nextBoolean();          
        int step = random.nextBoolean() ? 1 : -1;   
        move(ns ? step : 0, ns ? 0 : step);
    }

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance += walk.distance();
        }
        return totalDistance / n;
    }

    /**
     * The main method serves as the entry point to the RandomWalk program.
     *
     */
    public static void main(String[] args) {
        int[] mValues = {100, 200, 300, 400, 500, 600, 700, 800, 900};
        int n = 10000;

        System.out.println("m\tmeanDistance");
        for (int m : mValues) {
            double meanDistance = randomWalkMulti(m, n);
            System.out.printf("%d\t%.4f%n", m, meanDistance);
        }
    }

}
