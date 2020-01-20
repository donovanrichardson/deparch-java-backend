package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.ShapeRecord;

import static com.schema.tables.Shape.SHAPE;

public class ShapeWrapper extends OptionalTableWrapper<ShapeRecord> {
    public ShapeWrapper() {
        this.table = SHAPE;

        this.columns.put("shape_dist_traveled", SHAPE.SHAPE_DIST_TRAVELED);
        this.columns.put("shape_id", SHAPE.SHAPE_ID);
        this.columns.put("shape_pt_lat", SHAPE.SHAPE_PT_LAT);
        this.columns.put("shape_pt_lon", SHAPE.SHAPE_PT_LON);
        this.columns.put("shape_pt_sequence", SHAPE.SHAPE_PT_SEQUENCE);



    }
}
