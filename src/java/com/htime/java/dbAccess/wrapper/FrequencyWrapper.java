package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.FrequencyRecord;

import static com.schema.tables.Frequency.FREQUENCY;

public class FrequencyWrapper extends OptionalTableWrapper<FrequencyRecord> {

    public FrequencyWrapper() {
        this.table = FREQUENCY;

        this.columns.put("end_time", FREQUENCY.END_TIME);
        this.columns.put("exact_times", FREQUENCY.EXACT_TIMES);
        this.columns.put("headway_secs", FREQUENCY.HEADWAY_SECS);
        this.columns.put("start_time", FREQUENCY.START_TIME);
        this.columns.put("trip_id", FREQUENCY.TRIP_ID);

    }

}
