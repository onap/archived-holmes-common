/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.holmes.common.utils;

import java.util.List;

public class JudgeNullUtil {

    private JudgeNullUtil() {

    }

    public static boolean isEmpty( short[] shorts ) {
        return shorts == null || shorts.length == 0;
    }

    public static boolean isEmpty( int[] ints ) {
        return ints == null || ints.length == 0;
    }

    public static boolean isEmpty( long[] longs ) {
        return longs == null || longs.length == 0;
    }

    public static boolean isEmpty( Object[] obj ) {
        return obj == null || obj.length == 0;
    }

    public static boolean isEmpty( String str ) {
        return str == null || "".equals( str.trim() );
    }

    public static boolean isEmpty( List < ? > list ) {
        return list == null || list.isEmpty();
    }
}
