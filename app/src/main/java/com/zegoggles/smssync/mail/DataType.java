package com.zegoggles.smssync.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.zegoggles.smssync.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum DataType {
    SMS     (R.string.sms,      R.string.sms_with_field,     PreferenceKeys.IMAP_FOLDER,          Defaults.SMS_FOLDER,     PreferenceKeys.BACKUP_SMS,      Defaults.SMS_BACKUP_ENABLED,     PreferenceKeys.RESTORE_SMS,     Defaults.SMS_RESTORE_ENABLED,     PreferenceKeys.MAX_SYNCED_DATE_SMS,      -1, Defaults.MULTISIM_POSTFIX, PreferenceKeys.IMAP_FOLDER_MULTISIM_POSTFIX),
    MMS     (R.string.mms,      R.string.mms_with_field,     PreferenceKeys.IMAP_FOLDER,          Defaults.SMS_FOLDER,     PreferenceKeys.BACKUP_MMS,      Defaults.MMS_BACKUP_ENABLED,     null,                           Defaults.MMS_RESTORE_ENABLED,     PreferenceKeys.MAX_SYNCED_DATE_MMS,      Build.VERSION_CODES.ECLAIR, Defaults.MULTISIM_POSTFIX, PreferenceKeys.IMAP_FOLDER_MULTISIM_POSTFIX),
    CALLLOG (R.string.calllog,  R.string.call_with_field,    PreferenceKeys.IMAP_FOLDER_CALLLOG,  Defaults.CALLLOG_FOLDER, PreferenceKeys.BACKUP_CALLLOG,  Defaults.CALLLOG_BACKUP_ENABLED, PreferenceKeys.RESTORE_CALLLOG, Defaults.CALLLOG_RESTORE_ENABLED, PreferenceKeys.MAX_SYNCED_DATE_CALLLOG,  -1, Defaults.MULTISIM_POSTFIX, PreferenceKeys.IMAP_FOLDER_MULTISIM_POSTFIX);

    public final int resId;
    public final int withField;
    public final String backupEnabledPreference;
    public final String restoreEnabledPreference;
    public final String folderPreference;
    public final String defaultFolder;
    public final int minSdkVersion;
    public final boolean backupEnabledByDefault;
    public final boolean restoreEnabledByDefault;
    public final String maxSyncedPreference;
    public final boolean multisimPostfixDefault;
    public final String multisimPostfix;

    DataType(int resId,
             int withField,
             String folderPreference,
             String defaultFolder,
             String backupEnabledPreference,
             boolean backupEnabledByDefault,
             String restoreEnabledPreference,
             boolean restoreEnabledByDefault,
             String maxSyncedPreference,
             int minSdkVersion,
             boolean multisimPostfixDefault,
             String multisimPostfix) {
        this.resId = resId;
        this.withField = withField;
        this.folderPreference = folderPreference;
        this.defaultFolder = defaultFolder;
        this.backupEnabledPreference = backupEnabledPreference;
        this.backupEnabledByDefault = backupEnabledByDefault;
        this.restoreEnabledPreference = restoreEnabledPreference;
        this.restoreEnabledByDefault = restoreEnabledByDefault;
        this.maxSyncedPreference = maxSyncedPreference;
        this.minSdkVersion = minSdkVersion;
        this.multisimPostfixDefault = multisimPostfixDefault;
        this.multisimPostfix = multisimPostfix;
    }

    public boolean isBackupEnabled(SharedPreferences preferences) {
        //noinspection SimplifiableIfStatement
        if (minSdkVersion > 0 && Build.VERSION.SDK_INT < minSdkVersion) {
            return false;
        } else {
            return preferences
                    .getBoolean(backupEnabledPreference, backupEnabledByDefault);
        }
    }

    public boolean useMultisimPostfix(SharedPreferences preferences) {
            return preferences
                    .getBoolean(multisimPostfix, multisimPostfixDefault);
    }

    public void setBackupEnabled(SharedPreferences preferences, boolean enabled) {
        preferences
            .edit()
            .putBoolean(backupEnabledPreference, enabled)
            .apply();
    }

    public boolean isRestoreEnabled(SharedPreferences preferences) {
        return restoreEnabledPreference != null &&
                preferences.getBoolean(restoreEnabledPreference, restoreEnabledByDefault);
    }

    public String getFolder(SharedPreferences preferences) {
        return preferences.getString(folderPreference, defaultFolder);
    }

    public String getFolder(SharedPreferences preferences, int simCardNumber) {
        String folder = getFolder(preferences);
        if (useMultisimPostfix(preferences))
            folder+="_"+Integer.toString(simCardNumber+1);
        return folder;
    }


    /**
     * @return returns the last synced date in milliseconds (epoch)
     */
    public long getMaxSyncedDate(SharedPreferences preferences) {
        long maxSynced = preferences.getLong(maxSyncedPreference, Defaults.MAX_SYNCED_DATE);
        if (this == MMS && maxSynced > 0) {
            return maxSynced * 1000L;
        } else {
            return maxSynced;
        }
    }

    public boolean setMaxSyncedDate(SharedPreferences prefs, long max) {
        return prefs.edit().putLong(maxSyncedPreference, max).commit();
    }

    public static EnumSet<DataType> enabled(SharedPreferences preferences) {
        List<DataType> enabledTypes = new ArrayList<DataType>();
        for (DataType t : values()) {
            if (t.isBackupEnabled(preferences)) {
                enabledTypes.add(t);
            }
        }
        return enabledTypes.isEmpty() ? EnumSet.noneOf(DataType.class) : EnumSet.copyOf(enabledTypes);
    }

    public static long getMostRecentSyncedDate(SharedPreferences preferences) {
        return Math.max(Math.max(
                SMS.getMaxSyncedDate(preferences),
                CALLLOG.getMaxSyncedDate(preferences)),
                MMS.getMaxSyncedDate(preferences));
    }

    public static void clearLastSyncData(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        for (DataType type : values()) {
            editor.remove(type.maxSyncedPreference);
        }
        editor.commit();
    }

    public static class PreferenceKeys {
        public static final String IMAP_FOLDER = "imap_folder";
        public static final String IMAP_FOLDER_CALLLOG = "imap_folder_calllog";
        public static final String IMAP_FOLDER_MULTISIM_POSTFIX = "imap_folder_multisim_postfix";

        public static final String BACKUP_SMS = "backup_sms";
        public static final String BACKUP_MMS = "backup_mms";
        public static final String BACKUP_CALLLOG = "backup_calllog";

        public static final String RESTORE_SMS = "restore_sms";
        public static final String RESTORE_CALLLOG = "restore_calllog";

        public static final String MAX_SYNCED_DATE_SMS = "max_synced_date";
        public static final String MAX_SYNCED_DATE_MMS = "max_synced_date_mms";
        public static final String MAX_SYNCED_DATE_CALLLOG = "max_synced_date_calllog";

        private PreferenceKeys() {}
    }

    /**
     * Defaults for various settings
     */
    public static class Defaults {
        public static final long   MAX_SYNCED_DATE = -1;
        public static final String SMS_FOLDER     = "SMS";
        public static final String CALLLOG_FOLDER = "Call log";

        public static final boolean SMS_BACKUP_ENABLED       = true;
        public static final boolean MMS_BACKUP_ENABLED       = true;
        public static final boolean CALLLOG_BACKUP_ENABLED   = false;

        public static final boolean SMS_RESTORE_ENABLED      = true;
        public static final boolean MMS_RESTORE_ENABLED      = false;
        public static final boolean CALLLOG_RESTORE_ENABLED  = true;
        public static final boolean MULTISIM_POSTFIX =  false;


        private Defaults() {}
    }
}
