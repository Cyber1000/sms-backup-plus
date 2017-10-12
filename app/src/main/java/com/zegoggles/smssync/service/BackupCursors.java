package com.zegoggles.smssync.service;

import android.database.Cursor;
import android.util.Log;
import com.zegoggles.smssync.mail.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.zegoggles.smssync.App.TAG;
import static com.zegoggles.smssync.service.BackupItemsFetcher.emptyCursor;

public class BackupCursors implements Iterator<BackupCursors.ExtendedCursor> {
    private Map<DataType, Cursor> cursorMap = new HashMap<DataType, Cursor>();
    private List<ExtendedCursor> extendedCursors = new ArrayList<ExtendedCursor>();

    private int index;

    public static class ExtendedCursor {
        final DataType type;
        final Cursor cursor;
        final int simCardNumber;

        public ExtendedCursor(DataType type, Cursor cursor, int simCardNumber) {
            this.type = type;
            this.cursor = cursor;
            this.simCardNumber = simCardNumber;
        }

        public boolean hasNext() {
            return cursor.getCount() > 0 && !cursor.isLast();
        }

        @Override public String toString() {
            return "ExtendedCursor{" +
                    "type=" + type +
                    ", cursor=" + cursor +
                    ", simCardNumber=" + simCardNumber +
                    '}';
        }

        public static ExtendedCursor empty() {
            return new ExtendedCursor(DataType.SMS, emptyCursor(), 0);
        }
    }

    BackupCursors() {
    }

    void add(DataType type, Cursor cursor, int simCardNumber) {
        extendedCursors.add(new ExtendedCursor(type, cursor, simCardNumber));
        cursorMap.put(type, cursor);
    }

    public int count() {
        int total = 0;
        for (ExtendedCursor ct : extendedCursors) {
            total += ct.cursor.getCount();
        }
        return total;
    }

    public int count(DataType type) {
        Cursor cursor = cursorMap.get(type);
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override public boolean hasNext() {
        return !extendedCursors.isEmpty() && (getCurrent().hasNext() || getNextNonEmptyIndex() != -1);
    }

    @Override public ExtendedCursor next() {
        if (extendedCursors.isEmpty()) throw new NoSuchElementException();

        if (getCurrent().hasNext()) {
            getCurrentCursor().moveToNext();
        } else if (getNextNonEmptyIndex() != -1) {
            index = getNextNonEmptyIndex();
            getCurrentCursor().moveToFirst();
        } else {
            throw new NoSuchElementException();
        }

        return getCurrent();
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        for (ExtendedCursor ct : extendedCursors) {
            try {
                ct.cursor.close();
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }
    }

    private int getNextNonEmptyIndex() {
        for (int i = index + 1; i < extendedCursors.size(); i++) {
            if (extendedCursors.get(i).hasNext()) {
                return i;
            }
        }
        return -1;
    }

    private ExtendedCursor getCurrent() {
        return index < extendedCursors.size() ? extendedCursors.get(index) : ExtendedCursor.empty();
    }


    private Cursor getCurrentCursor() {
        return getCurrent().cursor;
    }


}
