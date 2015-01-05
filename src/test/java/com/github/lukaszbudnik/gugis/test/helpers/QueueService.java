package com.github.lukaszbudnik.gugis.test.helpers;

public interface QueueService {

    void publish(String item);

    String consume();

    void delete(String item);

}
