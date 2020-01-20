package com.htime.java.dbAccess.importer;

import com.htime.java.dbAccess.wrapper.*;
import com.htime.java.dbAccess.wrapper.exception.OptionalTableException;
import org.jooq.DSLContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GtfsImporter {

    DSLContext dsl;
    Map<String, AbstractTableWrapper> tableMap = new HashMap();

    public GtfsImporter(DSLContext dsl) {
        this.dsl = dsl;
        tableMap.put("stops.txt", new StopWrapper());
        tableMap.put("agency.txt", new AgencyWrapper());
        tableMap.put("routes.txt", new RouteWrapper());
        tableMap.put("calendar.txt", new ServiceWrapper());
        tableMap.put("calendar_dates.txt", new ServiceExceptionWrapper());
        tableMap.put("shapes.txt", new ShapeWrapper());
        tableMap.put("trips.txt", new TripWrapper());
        tableMap.put("frequencies.txt", new FrequencyWrapper());
        tableMap.put("stop_times.txt", new StopTimeWrapper());
    }

    public void addTxt(String txt, InputStream inputStream) throws IOException, OptionalTableException {

        this.tableMap.get(txt).dbImport(this.dsl, inputStream);

    }

}
