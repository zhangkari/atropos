package com.class100.atropos.generic;

import android.os.SystemClock;

public class AtFreqClick extends AtAbilityAdapter {
    private final int count;
    private final long duration;
    private long[] hits;

    public AtFreqClick(int count, long duration) {
        if (count < 2) {
            count = 2;
        }
        if (duration < 100) {
            duration = 100;
        }

        this.count = count;
        hits = new long[count];
        this.duration = duration;
    }

    public void onClick(Runnable trigger) {
        detectFreqClick(count, duration, trigger);
    }

    private void detectFreqClick(int count, long duration, Runnable task) {
        System.arraycopy(hits, 1, hits, 0, hits.length - 1);
        hits[hits.length - 1] = SystemClock.elapsedRealtime();
        if (SystemClock.elapsedRealtime() <= hits[0] + duration) {
            hits = new long[count];
            if (task != null) {
                task.run();
            }
        }
    }
}
