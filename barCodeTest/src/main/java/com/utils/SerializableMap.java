package com.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by fuyuan on 2015/9/17.
 */
public class SerializableMap implements Serializable {
    private Map<String,Object> map;
    public Map<String,Object> getMap()
    {
        return map;
    }
    public void setMap(Map<String,Object> map)
    {
        this.map=map;
    }

}
