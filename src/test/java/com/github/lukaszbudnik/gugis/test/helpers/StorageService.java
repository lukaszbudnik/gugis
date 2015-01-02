package com.github.lukaszbudnik.gugis.test.helpers;

public interface StorageService {

    int put(String item);

    String get(int id);

    void refresh(int id);

    void delete(int id);

}
