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

// Mockito and EasyMock are swallowing annotations when creating mocks
// thus a home made workaround for tracing if method was called
public class AbstractTestService {
    private boolean called;

    public void called() {
        called = true;
    }

    public boolean wasCalled() {
        return called;
    }

    public void reset() {
        called = false;
    }
}
