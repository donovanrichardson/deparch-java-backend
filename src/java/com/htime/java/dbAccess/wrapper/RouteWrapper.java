package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.RouteRecord;

import static com.schema.tables.Route.ROUTE;

public class RouteWrapper extends AbstractTableWrapper<RouteRecord> {
    public RouteWrapper() {

        this.table = ROUTE;

        this.columns.put("agency_id", ROUTE.AGENCY_ID);
        this.columns.put("route_short_name", ROUTE.DEFAULT_NAME);
        this.columns.put("route_color", ROUTE.ROUTE_COLOR);
        this.columns.put("route_desc", ROUTE.ROUTE_DESC);
        this.columns.put("route_id", ROUTE.ROUTE_ID);
        this.columns.put("route_long_name", ROUTE.ROUTE_LONG_NAME);
        this.columns.put("route_sort_order", ROUTE.ROUTE_SORT_ORDER);
        this.columns.put("route_text_color", ROUTE.ROUTE_TEXT_COLOR);
        this.columns.put("route_type", ROUTE.ROUTE_TYPE);
        this.columns.put("agency_url", ROUTE.ROUTE_URL);

    }
}
