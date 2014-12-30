package io.github.lukaszbudnik.guice.multibindinginterceptor.ioc.test;

public interface StorageService {

    int put(String item);

    String get(int id);

    void refresh(int id);

    void delete(int id);

}
