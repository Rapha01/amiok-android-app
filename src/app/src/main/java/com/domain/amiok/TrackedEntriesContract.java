package com.domain.amiok;

/**
 * Created by user on 06.01.2018.
 */

import android.provider.BaseColumns;

public class TrackedEntriesContract {

    public static final class TrackedEntry implements BaseColumns {

        public static final String TABLE_NAME = "trackedEntry";
        public static final String COLUMN_ENTRY_ID = "entryId";
    }
}
