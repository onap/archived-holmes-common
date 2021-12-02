/**
 * Copyright 2021 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

    final static private Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    final static private String LINE_SEPARATOR = System.getProperty ("line.separator");

    static public String readTextFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }
            return sb.substring(0, sb.length() - 1);
        } catch (FileNotFoundException e) {
            LOGGER.warn("No file found: {}", path);
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to read file contents from '%s'.", path), e);
        } catch (Exception e) {
            LOGGER.warn("Unknown exception occurred!", e);
        }
        return null;
    }
}
