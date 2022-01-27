package com.example.trackerapplication.utils.context;

import android.content.Context;
import android.graphics.Path;

import java.util.Optional;

public class ContextUtils {

    private static Context context;

    public ContextUtils(Context c) {
        context = c;
    }

    public static Optional<Context> getInstance() {
        return Optional.of(context);
    }
}
