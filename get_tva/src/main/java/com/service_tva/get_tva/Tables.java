package com.service_tva.get_tva;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Tables
{
    private final ArrayList<Map<String, Object>> _table = new ArrayList<>();
    private final ArrayList<Object> _content = new ArrayList<>();

    public Tables(){}

    public Tables(ArrayList<Map<String, Object>> table)
    {
        this._table.addAll(table);
    }

    public Tables(Object ... obj)
    {
        this._content.addAll(Arrays.asList(obj));
    }

    public final ArrayList<Object> getContent()
    {
        return this._content;
    }

    public final ArrayList<Map<String, Object>> getTable()
    {
        return this._table;
    }

    public final void AddContent(Object obj)
    {
        this._content.add(obj);
    }

    public final static String Serialize(Tables table)
    {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(table);
    }

    public final static Tables Deseriliaze(String json)
    {
        try
        {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(json, Tables.class);
        }
        catch (JsonSyntaxException e)
        {
            return null;
        }
    }
}
