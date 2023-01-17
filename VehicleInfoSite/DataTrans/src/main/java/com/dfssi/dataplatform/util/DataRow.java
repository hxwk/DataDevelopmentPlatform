package com.dfssi.dataplatform.util;


import java.util.HashMap;

public class DataRow extends HashMap
{
    public void set(String name, String value)
    {
        if ((name == null) || (name.equals(""))) {
            return;
        }
        if (value == null)
            put(name, "");
        else
            put(name, value);
    }

    public void set(String name, int value)
    {
        put(name, new Integer(value));
    }

    public void set(String name, boolean value)
    {
        put(name, new Boolean(value));
    }

    public void set(String name, long value)
    {
        put(name, new Long(value));
    }

    public void set(String name, float value)
    {
        put(name, new Float(value));
    }

    public void set(String name, double value)
    {
        put(name, new Double(value));
    }

    public void set(String name, Object value)
    {
        put(name, value);
    }

    public String getString(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return "";
        }
        String value = "";
        if (!(containsKey(name)))
            return "";
        Object obj = get(name);
        if (obj != null)
            value = obj.toString();
        obj = null;

        return value;
    }

    public int getInt(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return 0;
        }
        int value = 0;
        if (!(containsKey(name))) {
            return 0;
        }
        Object obj = get(name);
        if (obj == null) {
            return 0;
        }
        if (!(obj instanceof Integer))
        {
            try
            {
                value = Integer.parseInt(obj.toString());
            }
            catch (Exception ex)
            {
                value = 0;
            }
        }
        else
        {
            value = ((Integer)obj).intValue();
            obj = null;
        }

        return value;
    }

    public long getLong(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return 0L;
        }
        long value = 0L;
        if (!(containsKey(name))) {
            return 0L;
        }
        Object obj = get(name);
        if (obj == null) {
            return 0L;
        }
        if (!(obj instanceof Long))
        {
            try
            {
                value = Long.parseLong(obj.toString());
            }
            catch (Exception ex)
            {
                value = 0L;
            }
        }
        else
        {
            value = ((Long)obj).longValue();
            obj = null;
        }

        return value;
    }

    public float getFloat(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return 0.0F;
        }
        float value = 0.0F;
        if (!(containsKey(name))) {
            return 0.0F;
        }
        Object obj = get(name);
        if (obj == null) {
            return 0.0F;
        }
        if (!(obj instanceof Float))
        {
            try
            {
                value = Float.parseFloat(obj.toString());
            }
            catch (Exception ex)
            {
                value = 0.0F;
            }
        }
        else
        {
            value = ((Float)obj).floatValue();
            obj = null;
        }

        return value;
    }

    public double getDouble(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return 0.0D;
        }
        double value = 0.0D;
        if (!(containsKey(name))) {
            return 0.0D;
        }
        Object obj = get(name);
        if (obj == null) {
            return 0.0D;
        }
        if (!(obj instanceof Double))
        {
            try
            {
                value = Double.parseDouble(obj.toString());
            }
            catch (Exception ex)
            {
                value = 0.0D;
            }
        }
        else
        {
            value = ((Double)obj).doubleValue();
            obj = null;
        }

        return value;
    }

    public boolean getBoolean(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return false;
        }
        boolean value = false;
        if (!(containsKey(name)))
            return false;
        Object obj = get(name);
        if (obj == null) {
            return false;
        }
        if (obj instanceof Boolean)
        {
            return ((Boolean)obj).booleanValue();
        }

        value = Boolean.valueOf(obj.toString()).booleanValue();
        obj = null;
        return value;
    }

    public Object getObject(String name)
    {
        if ((name == null) || (name.equals(""))) {
            return null;
        }
        if (!(containsKey(name)))
            return null;
        return get(name);
    }
}