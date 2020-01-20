package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.ServiceRecord;

import static com.schema.tables.Service.SERVICE;

public class ServiceWrapper extends OptionalTableWrapper<ServiceRecord> {
    public ServiceWrapper() {
        this.table = SERVICE;

        this.columns.put("service_id", SERVICE.SERVICE_ID);
        this.columns.put("monday", SERVICE.MONDAY);
        this.columns.put("tuesday", SERVICE.TUESDAY);
        this.columns.put("wednesday", SERVICE.WEDNESDAY);
        this.columns.put("thursday", SERVICE.THURSDAY);
        this.columns.put("friday", SERVICE.FRIDAY);
        this.columns.put("saturday", SERVICE.SATURDAY);
        this.columns.put("sunday", SERVICE.SUNDAY);
        this.columns.put("start_date", SERVICE.START_DATE);
        this.columns.put("end_date", SERVICE.END_DATE);


    }
}
