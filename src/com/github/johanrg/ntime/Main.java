package com.github.johanrg.ntime;

import com.github.johanrg.parser.CommandLineParser;
import com.github.johanrg.parser.CommandLineParserException;

/**
 * @author Johan Gustafsson
 * @since 2016-05-31
 */
public class Main {
    public static void main(String[] args) {
        try {
            final double nanoSecond = 1000000000.0;

            CommandLineParser cmd = new CommandLineParser(args, 2,
                    "ntime v1.0: executes a program n times while calculating timing statistics\n\n" +
                    "Usage:\tntimer [-c <number of executions>] [-v] -p <program file> [program parameters]\n" +
                    "\t\t-v\tVerbose, redirect the output from the executed program to the terminal.");

            int executeCount = cmd.getInteger("-c", 1);
            if (executeCount <= 0) {
                throw new CommandLineParserException("-c must be greater than zero.");
            }
            boolean verbose = cmd.getParam("-v");

            String[] programToExecute = cmd.getStringArray("-p", 0);
            try {
                ProgramTimer programTimer = new ProgramTimer(programToExecute, executeCount);

                System.out.printf("Average:\t%.5fs\n", programTimer.getAverage() / nanoSecond);
                System.out.printf("Median: \t%.5fs\n", programTimer.getMedian() / nanoSecond);
                System.out.printf("Deviation: \t%.5fs\n", programTimer.getStandardDeviation() / nanoSecond);
                System.out.printf("Min:\t\t%.5fs\n", (double) programTimer.getMinTime() / nanoSecond);
                System.out.printf("Max:\t\t%.5fs\n", (double) programTimer.getMaxTime() / nanoSecond);
                System.out.printf("Total Time:\t%.5fs\n", programTimer.getTotalTime() / nanoSecond);

                if (verbose) {
                    System.out.println("\nOutput from program:");
                    System.out.println(programTimer.getProgramOutBuffer().toString());
                }

                String errorBuffer = programTimer.getProgramErrorBuffer().toString();
                if (errorBuffer.length() > 0) {
                    System.out.println("\nProgram reported the following errors (max 500 chars):");
                    System.out.println(programTimer.getProgramErrorBuffer().toString().substring(0, errorBuffer.length() > 500 ? 500 : errorBuffer.length()));
                }
            } catch (ProgramTimerException e) {
                System.err.printf("An error occurred: %s\n", e.getMessage());
            }
        } catch (CommandLineParserException e) {
            System.err.println(e.getMessage());
        }
    }
}
