package com.service_checkout.service_checkout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class Table
{
    private final ArrayList<Map<String, Object>> _table = new ArrayList<>();

    public final ArrayList<Map<String, Object>> getTable()
    {
        return this._table;
    }

    public final static Table Deseriliaze(String json)
    {
        try
        {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(json, Table.class);
        }
        catch (JsonSyntaxException e)
        {
            return null;
        }
    }
}
