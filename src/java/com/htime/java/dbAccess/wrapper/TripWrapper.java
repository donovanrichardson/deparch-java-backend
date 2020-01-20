package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.TripRecord;

import static com.schema.tables.Trip.TRIP;

public class TripWrapper extends AbstractTableWrapper<TripRecord> {
    public TripWrapper() {
        this.table = TRIP;

        this.columns.put("bikes_allowed", TRIP.BIKES_ALLOWED);
        this.columns.put("block_id", TRIP.BLOCK_ID);
        this.columns.put("direction_id", TRIP.DIRECTION_ID);
        this.columns.put("route_id", TRIP.ROUTE_ID);
        this.columns.put("service_id", TRIP.SERVICE_ID);
        this.columns.put("shape_id", TRIP.SHAPE_ID);
        this.columns.put("trip_headsign", TRIP.TRIP_HEADSIGN);
        this.columns.put("trip_id", TRIP.TRIP_ID);
        this.columns.put("trip_short_name", TRIP.BLOCK_ID);
        this.columns.put("wheelchair_accessible", TRIP.WHEELCHAIR_ACCESSIBLE);
    }
}
