package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Composite;
import com.github.lukaszbudnik.gugis.Replicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Composite
public class QueueServiceComposite implements QueueService {

    @Replicate
    @Override
    public void publish(String item) {
    }

    @Replicate(allowFailure = true)
    @Override
    public String consume() {
        return null;
    }

    @Replicate(allowFailure = true)
    @Override
    public void delete(String item) {
    }
}
