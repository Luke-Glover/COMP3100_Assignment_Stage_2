# DS-Sim Client Stage 2

This project is a Java implementation of the client side simulator of DS-Sim.

## Usage

java Client [ -v ] [ -d ] [ -c CONFIGURATION_PATH ] [ -ip IP_ADDRESS] [ -port PORT_NUMBER] [ -a ALGORITHM_NAME ]

-v enables a verbose mode, which prints output matching the reference implementation of DS-Sim client.

-d enables debug mode, which enables extra output useful for debugging.

-a selects the algorithm used for scheduling. The only valid option is allToLargest, which is the default.

-c passes the configuration path, i.e. the path to the ds-system.xml file generated by ds-server. By default, the client looks in the current directory. If a configuration is not found, the client will exit.

-ip is the IP address of the DS-Sim server. This is usually 127.0.0.1, which is the default.

-port is the port that DS-Sim server is listening on. The default is 50000.

## Algorithms

### "Perfect" Fit Algorithm

This version of the client implements an efficiency optimising scheduling 
algorithm. The algorithm performs a preliminary scheduling decision whereby 
a job is assigned to the smallest server that will fit the job (this is 
similar to best fit, except that it ignores any running or scheduled jobs).
Then the algorithm performs a second scheduling, whereby the new job is 
"combined" with the already running or waiting job, and this new "super job" 
is scheduled again if possible.

For example, imagine a system with two dual-core servers and one quad core 
server. A job requiring two cores is already running on the first server.

Server A - 0 / 2 core available <br>
Server B - 2 / 2 cores available <br>
Server C - 4 / 4 cores available <br>

A new job requiring two cores would initially be scheduled onto server A. 
However, because a job is already running, the algorithm combines both two 
core jobs into a four core job and schedules it on server C, migrating jobs 
as necessary. 