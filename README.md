#NTIME v1.0
Executes a program 1 or more times (specified with the -c switch) while calculating timing statistics.

All times are calculated in nano seconds to be as precise as we can. The program is written in Java but the inner code
between the start and stop time is very small so it shouldn't affect the result much.

How to run:
You need a Java runtime environment installed, to run the program type:
java -jar ntime
or use the shell script ntime located in the root to run it.

Observe that a low number of executions will not give an accurate timing representation. Do multile 1000's of executions to get
better statistics if possible.

