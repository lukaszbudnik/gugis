/**
 * Copyright (C) 2015-2017 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.Failure;
import com.github.lukaszbudnik.gugis.Success;
import com.github.lukaszbudnik.gugis.Try;
import org.junit.Assert;
import org.junit.Test;

public class TryMonadTest {

    @Test
    public void successShouldReturnValue() {
        Try<String> success = new Success<String>("test");

        Assert.assertTrue(success.isSuccess());
        Assert.assertFalse(success.isFailure());
        Assert.assertEquals("test", success.get());
    }

    @Test(expected = IllegalStateException.class)
    public void successShouldThrowIllegalStateWhenAccessingFailure() {
        Try<String> success = new Success<String>("test");

        success.failure();
    }

    @Test
    public void failureShouldReturnException() {
        Exception exception = new Exception("test");
        Try<String> failure = new Failure<>(exception);

        Assert.assertTrue(failure.isFailure());
        Assert.assertFalse(failure.isSuccess());
        Assert.assertEquals(failure.failure(), exception);
    }

    @Test(expected = IllegalStateException.class)
    public void successShouldThrowIllegalStateWhenAccessingGet() {
        Exception exception = new Exception("test");
        Try<String> failure = new Failure<>(exception);

        failure.get();
    }

}
