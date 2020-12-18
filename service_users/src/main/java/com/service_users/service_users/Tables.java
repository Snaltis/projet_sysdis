package com.service_users.service_users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class Tables
{
    private final ArrayList<Map<String, Object>> _table = new ArrayList<>();
    private final ArrayList<Object> _content = new ArrayList<>();

    public final ArrayList<Object> getContent()
    {
        return this._content;
    }

    public final ArrayList<Map<String, Object>> getTable()
    {
        return this._table;
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
