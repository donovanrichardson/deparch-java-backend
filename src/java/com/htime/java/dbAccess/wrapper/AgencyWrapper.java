package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.AgencyRecord;

import static com.schema.tables.Agency.AGENCY;

public class AgencyWrapper extends AbstractTableWrapper<AgencyRecord> {


    public AgencyWrapper() {
        this.table = AGENCY;
        this.columns.put("agency_id", AGENCY.AGENCY_ID);
        this.columns.put("agency_name", AGENCY.AGENCY_NAME);
        this.columns.put("agency_timezone", AGENCY.AGENCY_TIMEZONE);
        this.columns.put("agency_url", AGENCY.AGENCY_URL);
    }



}
