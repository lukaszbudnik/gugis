/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
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

@Primary
@Singleton
@Slf4j
public class StorageService2Impl extends AbstractTestService implements StorageService {

    @Override
    public int put(String item) {
        log.trace(this.getClass().getSimpleName() + ".put = " + item);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        called();
        return 2;
    }

    @Override
    public String get(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".get = " + id);
        return "null 2";
    }

    @Override
    public void refresh(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".refresh = " + id);
    }

    @Override
    public void delete(int id) {
        called();
        log.trace(this.getClass().getSimpleName() + ".delete = " + id);
    }
}
