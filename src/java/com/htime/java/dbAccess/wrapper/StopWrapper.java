package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.StopRecord;

import static com.schema.tables.Stop.STOP;

public class StopWrapper extends AbstractTableWrapper<StopRecord> {
    public StopWrapper() {
        this.table = STOP;

        this.columns.put("stop_id", STOP.STOP_ID);
        this.columns.put("location_type", STOP.LOCATION_TYPE);
        this.columns.put("parent_station", STOP.PARENT_STATION);
        this.columns.put("stop_code", STOP.STOP_CODE);
        this.columns.put("stop_desc", STOP.STOP_DESC);
        this.columns.put("stop_lat", STOP.STOP_LAT);
        this.columns.put("stop_lon", STOP.STOP_LON);
        this.columns.put("stop_name", STOP.STOP_NAME);
        this.columns.put("stop_url", STOP.STOP_URL);
        this.columns.put("wheelchair_boarding", STOP.WHEELCHAIR_BOARDING);

    }
}
