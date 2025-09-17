package com.example.multitenant.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TenantConnectionRegistry {

    private final List<TenantConnectionKey> recordedKeys = Collections.synchronizedList(new ArrayList<>());

    public void record(TenantConnectionKey key) {
        if (key != null) {
            recordedKeys.add(key);
        }
    }

    public List<TenantConnectionKey> getRecordedKeys() {
        synchronized (recordedKeys) {
            return new ArrayList<>(recordedKeys);
        }
    }

    public void clear() {
        recordedKeys.clear();
    }
}
