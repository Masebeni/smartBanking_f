package org.apache.http.params;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

public class BasicHttpParams extends AbstractHttpParams implements HttpLinkedParams, Serializable {
    static final long serialVersionUID = 4571099216197814749L;
    protected HttpParams defaults;
    private HashMap parameters;

    public BasicHttpParams(HttpParams defaults) {
        setDefaults(defaults);
    }

    public BasicHttpParams() {
        this(null);
    }

    public HttpParams getDefaults() {
        return this.defaults;
    }

    public void setDefaults(HttpParams params) {
        this.defaults = params;
    }

    public Object getParameter(String name) {
        Object param = null;
        if (this.parameters != null) {
            param = this.parameters.get(name);
        }
        if (param != null) {
            return param;
        }
        if (this.defaults != null) {
            return this.defaults.getParameter(name);
        }
        return null;
    }

    public HttpParams setParameter(String name, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }
        this.parameters.put(name, value);
        return this;
    }

    public void setParameters(String[] names, Object value) {
        for (String parameter : names) {
            setParameter(parameter, value);
        }
    }

    public boolean isParameterSet(String name) {
        return getParameter(name) != null;
    }

    public boolean isParameterSetLocally(String name) {
        return (this.parameters == null || this.parameters.get(name) == null) ? false : true;
    }

    public void clear() {
        this.parameters = null;
    }

    public HttpParams copy() {
        BasicHttpParams bhp = new BasicHttpParams(this.defaults);
        copyParams(bhp);
        return bhp;
    }

    protected void copyParams(HttpParams target) {
        if (this.parameters != null) {
            for (Entry me : this.parameters.entrySet()) {
                if (me.getKey() instanceof String) {
                    target.setParameter((String) me.getKey(), me.getValue());
                }
            }
        }
    }
}
