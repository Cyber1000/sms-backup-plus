package com.zegoggles.smssync.service;

import android.content.Context;
import android.database.Cursor;
import android.telephony.SubscriptionManager;

import com.zegoggles.smssync.contacts.ContactGroupIds;
import com.zegoggles.smssync.mail.DataType;
import com.zegoggles.smssync.utils.SimCardHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BulkFetcher {

    private BackupItemsFetcher itemsFetcher;

    public BulkFetcher(BackupItemsFetcher itemsFetcher) {
        this.itemsFetcher = itemsFetcher;
    }

    public @NotNull BackupCursors fetch(final @NotNull EnumSet<DataType> types,
                                        final @Nullable ContactGroupIds groups,
                                        final int maxItems, Context context) {

        int max = maxItems;
        BackupCursors cursors = new BackupCursors();
        for (DataType type : types) {
            for (int simCardNumber = -1; simCardNumber<SimCardHelper.getSimCardCount(context); simCardNumber++) {
                Cursor cursor = itemsFetcher.getItemsForDataType(type, groups, max, simCardNumber);
                cursors.add(type, cursor, simCardNumber);

                if (max > 0) {
                    max = Math.max(max - cursor.getCount(), 0);
                }

                if (max == 0) break;
            }
        }
        return cursors;
    }

}
