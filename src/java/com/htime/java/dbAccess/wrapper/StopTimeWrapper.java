package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.StopTimeRecord;

import static com.schema.tables.StopTime.STOP_TIME;

public class StopTimeWrapper extends AbstractTableWrapper<StopTimeRecord> {
    public StopTimeWrapper() {
        this.table = STOP_TIME;

        this.columns.put("arrival_time", STOP_TIME.ARRIVAL_TIME);
        this.columns.put("departure_time", STOP_TIME.DEPARTURE_TIME);
        this.columns.put("drop_off_type", STOP_TIME.DROP_OFF_TYPE);
        this.columns.put("pickup_type", STOP_TIME.PICKUP_TYPE);
        this.columns.put("shape_dist_traveled", STOP_TIME.SHAPE_DIST_TRAVELED);
        this.columns.put("stop_headsign", STOP_TIME.STOP_HEADSIGN);
        this.columns.put("stop_id", STOP_TIME.STOP_ID);
        this.columns.put("stop_sequence", STOP_TIME.STOP_SEQUENCE);
        this.columns.put("timepoint", STOP_TIME.TIMEPOINT);
        this.columns.put("trip_id", STOP_TIME.TRIP_ID);
    }
}
