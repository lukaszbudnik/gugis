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

import com.github.lukaszbudnik.gugis.Composite;
import com.github.lukaszbudnik.gugis.Propagate;
import com.github.lukaszbudnik.gugis.Propagation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Composite
public class QueueServiceComposite implements QueueService {

    @Propagate
    @Override
    public void publish(String item) {
    }

    @Propagate(allowFailure = true)
    @Override
    public String consume() {
        return null;
    }

    @Propagate(allowFailure = true)
    @Override
    public void delete(String item) {
    }

    @Propagate(propagation = Propagation.FASTEST, allowFailure = true)
    @Override
    public int stats() {
        return 0;
    }

    @Propagate(propagation = Propagation.RANDOM, allowFailure = true)
    @Override
    public String permissions() {
        return null;
    }
}
