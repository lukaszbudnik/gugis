/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis.test;

import com.github.lukaszbudnik.gugis.GugisModule;
import com.github.lukaszbudnik.gugis.test.helpers.AggregatorServiceComposite;
import com.github.lukaszbudnik.gugis.test.helpers.ReportServiceComposite;
import com.github.lukaszbudnik.gugis.test.helpers.SplitterServiceComposite;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import org.junit.Assert;
import org.junit.Test;

public class ValidationTest {

    @Test
    public void shouldThrowGugisExceptionWhenImplementationsNotFound() {

        try {
            // fail fast, Guice injector will not be created at all
            Guice.createInjector(new GugisModule());
            Assert.fail("There should be an exception thrown by Gugis validation");
        } catch (CreationException e) {
            // the following errors should be detected:
            // 1) ReportServiceComposite does not have any implementations at all
            Assert.assertTrue(e.getCause().getMessage().contains("No implementations found for " + ReportServiceComposite.class));
            // 2) AggregatorServiceComposite does not have @Primary implementations
            Assert.assertTrue(e.getCause().getMessage().contains("Composite component " + AggregatorServiceComposite.class + " methods [public void aggregate()] marked with @Propagate(propagation = Propagation.PRIMARY) but no primary implementations found"));
            // 3) SplitterServiceComposite does not have @Secondary implementations
            Assert.assertTrue(e.getCause().getMessage().contains("Composite component " + SplitterServiceComposite.class + " methods [public void split()] marked with @Propagate(propagation = Propagation.SECONDARY) but no secondary implementations found"));
        } catch (Throwable t) {
            Assert.fail("CreationException expected but got " + t.getClass());
        }

    }

}
