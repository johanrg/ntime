package com.github.johanrg.ntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class times the execution of a program and calculates statistics from the time it took to run it.
 *
 * @author Johan Gustafsson
 * @since 6/2/2016.
 */
class ProgramTimer {
    private final List<Long> allTimes = new ArrayList<>();
    private final int executeCount;
    private final String[] programToExecute;
    private final StringBuilder programOutBuffer = new StringBuilder();
    private final StringBuilder programErrorBuffer = new StringBuilder();
    private long minTime = Long.MAX_VALUE;
    private long maxTime = 0;
    private long totalTime = 0;

    /**
     * The class instance can only be used for one program, new instances needs to be made to test another program
     * or even to rerun the same test again.
     *
     * @param programToExecute the path to the program to time with optional parameters.
     * @param executeCount     number of times to run the ntime, the more times the better... up to a point.
     * @throws ProgramTimerException
     */
    ProgramTimer(String[] programToExecute, int executeCount) throws ProgramTimerException {
        this.executeCount = executeCount;
        this.programToExecute = programToExecute;
        for (int i = 0; i < executeCount; ++i) {
            long elapsedTime = executeProgram();

            totalTime += elapsedTime;
            allTimes.add(elapsedTime);
            minTime = Math.min(minTime, elapsedTime);
            maxTime = Math.max(maxTime, elapsedTime);
        }

        Collections.sort(allTimes);
    }


    /**
     * The method that executes the program and and returns the time for one execution in nano seconds.
     *
     * @return time in nano seconds to run the program once.
     * @throws ProgramTimerException
     */
    private long executeProgram() throws ProgramTimerException {
        try {
            ProcessBuilder pb = new ProcessBuilder(programToExecute);
            try {
                long start = System.nanoTime();
                Process p = pb.start();
                p.waitFor();
                long time = System.nanoTime() - start;

                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = buffer.readLine()) != null) {
                        programOutBuffer.append(line).append(System.getProperty("line.separator"));
                    }
                } catch (IOException e) {
                    System.out.println("Error while reading buffered program output");
                }
                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                    String line;
                    while ((line = buffer.readLine()) != null) {
                        programErrorBuffer.append(line).append(System.getProperty("line.separator"));
                    }
                } catch (IOException e) {
                    System.out.println("Error while reading buffered program error");
                }


                return time;
            } catch (InterruptedException e) {
                throw new ProgramTimerException(e.getMessage());
            }
        } catch (IOException e) {
            throw new ProgramTimerException(e.getMessage());
        }
    }

    /**
     * Total run time for all executions together.
     *
     * @return time in nano seconds.
     */
    long getTotalTime() {
        return totalTime;
    }


    /**
     * The average time for all executions of the program.
     *
     * @return time in nano seconds.
     */
    double getAverage() {
        return (double) (totalTime / executeCount);
    }


    /**
     * The shortest time it took for one of the executions of the program.
     *
     * @return time in nano seconds.
     */
    long getMinTime() {
        return minTime;
    }

    /**
     * The longest time it took for one of the executions of the program.
     *
     * @return time in nano seconds.
     */
    long getMaxTime() {
        return maxTime;
    }

    /**
     * The median time of all the executions.
     *
     * @return time in nano seconds.
     */
    double getMedian() {
        double median;
        if (allTimes.size() % 2 == 0) {
            int half = allTimes.size() / 2 - 1;
            median = (double) (allTimes.get(half) + allTimes.get(half + 1)) / 2;
        } else {
            int half;
            half = (allTimes.size() - 1) / 2;
            median = (double) allTimes.get(half);
        }

        return median;
    }

    /**
     * The standard deviation time of all the executions of the program.
     *
     * @return time in nano seconds.
     */
    double getStandardDeviation() {
        List<Double> varianceList = allTimes.stream().map(s -> Math.pow(s - getAverage(), 2)).collect(Collectors.toList());
        double variance = varianceList.stream().mapToDouble(f -> f).sum();

        if (executeCount > 1) {
            variance /= (executeCount - 1);
        }

        return Math.sqrt(variance);
    }

    public StringBuilder getProgramOutBuffer() {
        return programOutBuffer;
    }

    public StringBuilder getProgramErrorBuffer() {
        return programErrorBuffer;
    }
}
