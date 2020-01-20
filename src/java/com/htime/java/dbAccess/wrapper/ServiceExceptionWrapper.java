package com.htime.java.dbAccess.wrapper;

import com.schema.tables.records.ServiceExceptionRecord;
import com.htime.java.dbAccess.wrapper.exception.OptionalTableException;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.schema.tables.Service.SERVICE;
import static com.schema.tables.ServiceException.SERVICE_EXCEPTION;

public class ServiceExceptionWrapper extends OptionalTableWrapper<ServiceExceptionRecord> {

    public ServiceExceptionWrapper() {
        this.table = SERVICE_EXCEPTION;

        this.columns.put("date", SERVICE_EXCEPTION.DATE);
        this.columns.put("exception_type", SERVICE_EXCEPTION.EXCEPTION_TYPE);
        this.columns.put("service_id", SERVICE_EXCEPTION.SERVICE_ID);


    }

    @Override
    public void dbImport(DSLContext dsl, InputStream inputStream) throws IOException, OptionalTableException {
        this.verifyNotNull(inputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] b = new byte[1024];
        int l;
        try {
            while ((l = inputStream.read(b)) > 0) {
                out.write(b, 0, l);
            }
            out.flush();
        }catch(EOFException e){
            System.out.println("there was a problem while processing calendar_dates.txt");
            throw e;
        }

        InputStream forService = new ByteArrayInputStream(out.toByteArray());
        InputStream forExceptions = new ByteArrayInputStream(out.toByteArray());


        forService.mark(2000);
        Scanner s = new Scanner(forService);
        String[] columns = s.nextLine().split("\"*\\s*,\\s*\"*"); //todo how to do this correctly
        ArrayList<String> formCol = new ArrayList<>();
        for (String c:columns){
            formCol.add(c.replaceAll("\"",""));
        }
        List<Field<?>> fields = new ArrayList();
        for (String col : formCol){
            if(col.equals("service_id")){
                fields.add(SERVICE.SERVICE_ID);
            } else {
                fields.add(null);
            }
        }
        forService.reset();
        dsl.loadInto(SERVICE).commitAfter(10000).onDuplicateKeyIgnore().loadCSV(forService).fields(fields).nullString("").execute();

        super.dbImport(dsl, forExceptions);
    }

}
