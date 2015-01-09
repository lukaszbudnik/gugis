/**
 * Copyright (C) 2015 Łukasz Budnik <lukasz.budnik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.lukaszbudnik.gugis;

import java.util.List;

public class GugisCreationException extends RuntimeException {

    private final String detailedMessage;

    public GugisCreationException(List<String> validationErrors) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("The following creation errors were found:");
        for (int i = 0; i < validationErrors.size(); i++) {
            messageBuilder.append("\n\n");
            messageBuilder.append(i + 1);
            messageBuilder.append(") ");
            messageBuilder.append(validationErrors.get(i));
        }
        fillInStackTrace();
        detailedMessage = messageBuilder.toString();
    }

    public String getMessage() {
        return detailedMessage;
    }

}
