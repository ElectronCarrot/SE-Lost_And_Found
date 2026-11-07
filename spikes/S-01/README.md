# S-01 keyword search test

The question was whether 95% of searches through 8,000 records could finish within 2 seconds.

The program uses Java with 8,000 fake records in memory on one machine. The records are lowercased before timing. It runs 50 warm-up searches without timing them, then times 200 searches with System.nanoTime(). The p95 is the 190th time after sorting them.

My run got:
median: 1.011 ms
p95: 1.610 ms
max: 2.137 ms
average: 1.053 ms

All 200 searches finished under 2 seconds. This only tested searching records in memory, not a database, network, or multiple users.

The code is in src/SearchLatencySpike.java and the output is in results/results.txt.
