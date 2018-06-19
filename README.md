# Vector clock
From Wikipedia (https://en.wikipedia.org/wiki/Vector_clock)

A vector clock is an algorithm for generating a partial ordering of events in a distributed system and detecting causality violations. Just as in Lamport timestamps, interprocess messages contain the state of the sending process's logical clock.  A vector clock of a system of _N_ processes is an array/vector of _N_ logical clocks, one clock per process; a local "smallest possible values" copy of the global clock-array is kept in each process, with the following rules for clock updates:
1. Initially, all clocks are zero.
1. Each time a process experiences an internal event, it increments its own logical clock in the vector by one.
1. Each time a process sends a message, it increments its own logical clock in the vector by one (as in the bullet above) and then sends a copy of its own vector.
1. Each time a process receives a message, it increments its own logical clock in the vector by one and updates each element in its vector by taking the maximum of the value in its own vector clock and the value in the vector in the received message (for every element).

# Implementation
This project provides a basic implementation of Vector Clock in Java.  It comes in two flavours a simple version that uses `String`s to identify nodes and `long` as the version number.  A generic version is also available where it allows customizable versions for both the node labelling and version.  Both flavours provide the same functionality.
