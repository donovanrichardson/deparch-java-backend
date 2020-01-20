package com.htime.java.dbAccess.wrapper;

import com.htime.java.dbAccess.wrapper.exception.OptionalTableException;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class AbstractTableWrapper<R extends Record> implements TableWrapper {

    Table<R> table;
    Map<String,Field<?>> columns = new HashMap<>();

    @Override
    public void dbImport(DSLContext dsl, InputStream inputStream) throws IOException, OptionalTableException {
        this.verifyNotNull(inputStream);
        inputStream.mark(2000);
        Scanner sc = new Scanner(inputStream);
        String[] columns = sc.nextLine().split("[\\s\"]*,[\\s\"]*"); //todo how to do this correctly. namely, removing quotes
        List<String> formattedCols = new ArrayList<>();
        for (String st : columns){
            formattedCols.add(st.replaceAll("[\\s\\ufeff\"]",""));
        }
        List<Field<?>> fields = new ArrayList();
        for (String col : formattedCols){
            fields.add(this.columns.get(col));
        }
        inputStream.reset();
        try{
            dsl.loadInto(table).bulkAfter(10000).loadCSV(inputStream).fields(fields).nullString("").execute();
        } catch (Exception e){
            throw e;
        }finally{
            inputStream.close();
        }



    }

    void verifyNotNull(InputStream inputStream) throws OptionalTableException {
        if(inputStream == null){
            throw new NullPointerException("There is no zipfile associated with "+this.table.getName());
        }
    }

}
