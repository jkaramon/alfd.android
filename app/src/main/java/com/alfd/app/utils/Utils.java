/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alfd.app.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;

import com.alfd.app.activities.MainActivity;
import com.alfd.app.activities.NewProductActivity;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Objects;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {};


    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(NewProductActivity.class, 1)
                        .setClassInstanceLimit(MainActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    public static boolean isBlank(String val) {
        return val == null || val.trim() == "";
    }

    public static String transliterate(String val) {
        if (isBlank(val)) {
            return val;
        }
        String normalized = Normalizer.normalize(val, Normalizer.Form.NFKC);
        return normalized.toLowerCase();
    }
    private static HashMap<Character, Character> tranliterateMap;

    private static char lookupTransliteratedChar(char c) {
        if (tranliterateMap == null) {
            initTransliterateMap();
        }
        if (tranliterateMap.containsKey(c)) {
            return tranliterateMap.get(c);
        }
        return c;
    }

    private static void initTransliterateMap() {
        tranliterateMap = new HashMap<Character, Character>();
    }

    public static String belrusToEngTranlit (String text){
        char[] abcCyr = {'a','б','в','г','д','ё','ж','з','и','к','л','м','н','п','р','с','т','у','ў','ф','х','ц','ш','щ','ы','э','ю','я'};
        String[] abcLat = {"a","b","v","g","d","jo","zh","z","i","k","l","m","n","p","r","s","t","u","w","f","h","ts","sh","sch","","e","ju","ja"};

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            builder.append(lookupTransliteratedChar(c));
        }
        return builder.toString();
    }
}
