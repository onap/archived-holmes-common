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

package org.onap.holmes.common;

import org.apache.commons.lang3.StringUtils;
import org.onap.holmes.common.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileScanner {
    public synchronized Map<String, String> scan(String path, String fileNamePattern) {
        File dir = new File(path);
        Map<String, String> ret = new HashMap();
        if (!dir.isDirectory()) {
            ret.putAll(readFile(dir));
        } else {
            for (File file : dir.listFiles(pathname -> StringUtils.isBlank(fileNamePattern) ?
                    true : pathname.getName().contains(fileNamePattern))) {
                if (!file.isDirectory()) {
                    ret.putAll(readFile(file));
                }
            }
        }
        return ret;
    }

    public synchronized Map<String, String> scan(String path) {
        return scan(path, null);
    }

    private Map<String, String> readFile(File file) {
        Map<String, String> ret = new HashMap();
        String contents = FileUtils.readTextFile(file.getAbsolutePath());
        if (StringUtils.isNotBlank(contents)) {
            ret.put(file.getAbsolutePath(), contents);
        }
        return ret;
    }
}
