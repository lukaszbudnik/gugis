package com.github.lukaszbudnik.gugis;

public enum Propagation {
    ALL, // ALL = all primaries and all secondaries
    PRIMARY, // PRIMARY = all primaries
    SECONDARY, // SECONDARY = all secondaries
    ANY // ANY = either primary or secondary, just one, good for reads
}
