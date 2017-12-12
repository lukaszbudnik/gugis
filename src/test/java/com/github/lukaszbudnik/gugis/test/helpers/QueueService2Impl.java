/**
 * Copyright (C) 2015-2017 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis.test.helpers;

import com.github.lukaszbudnik.gugis.Primary;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Slf4j
@Singleton
@Primary
public class QueueService2Impl extends AbstractTestService implements QueueService {

    @Override
    public void publish(String item) {
        log.trace("publish = " + item);
        called();
        throw new RuntimeException(this.getClass().getSimpleName() + " exception in publish!");
    }

    @Override
    public String consume() {
        log.trace("consume");
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }

    @Override
    public void delete(String item) {
        log.trace("delete " + item);
        called();
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " exception in delete!");
    }

    @Override
    public int stats() {
        log.trace("stats");
        called();
        throw new RuntimeException(this.getClass().getCanonicalName());
    }

    @Override
    public String permissions() {
        log.trace("permissions");
        called();
        return "usera:write;userb:read";
    }
}
