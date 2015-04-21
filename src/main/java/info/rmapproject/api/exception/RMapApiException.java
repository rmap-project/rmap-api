package info.rmapproject.api.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
/**
 * 
 * @author khanson
 * Creates HTTP responses for DiSCO REST API requests
 * Note: Code derived from: https://northconcepts.com/downloads/file/blog/exceptions/NorthConcepts-Exceptions.zip 
 * Therefore it should be stated that: The source code is licensed under the terms of the Apache License, Version 2.0.
 */
public class RMapApiException extends Exception {

    private static final long serialVersionUID = 1L;

    public static RMapApiException wrap(Throwable exception, ErrorCode errorCode) {
        if (exception instanceof RMapApiException) {
            RMapApiException se = (RMapApiException)exception;
        	if (errorCode != null && errorCode != se.getErrorCode()) {
                return new RMapApiException(exception.getMessage(), exception, errorCode);
			}
			return se;
        } else {
            return new RMapApiException(exception.getMessage(), exception, errorCode);
        }
    }
    
    public static RMapApiException wrap(Throwable exception) {
    	return wrap(exception, null);
    }
    
    private ErrorCode errorCode;
    private final Map<String,Object> properties = new TreeMap<String,Object>();
    
    public RMapApiException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public RMapApiException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public RMapApiException(Throwable cause, ErrorCode errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public RMapApiException(String message, Throwable cause, ErrorCode errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
        return errorCode;
    }
	
	public RMapApiException setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T get(String name) {
        return (T)properties.get(name);
    }
	
    public RMapApiException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
    
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            printStackTrace(new PrintWriter(s));
        }
    }

    public void printStackTrace(PrintWriter s) { 
        synchronized (s) {
            s.println(this);
            s.println("\t-------------------------------");
            if (errorCode != null) {
	        	s.println("\t" + errorCode + ":" + errorCode.getClass().getName()); 
			}
            for (String key : properties.keySet()) {
            	s.println("\t" + key + "=[" + properties.get(key) + "]"); 
            }
            s.println("\t-------------------------------");
            StackTraceElement[] trace = getStackTrace();
            for (int i=0; i < trace.length; i++)
                s.println("\tat " + trace[i]);

            Throwable ourCause = getCause();
            if (ourCause != null) {
                ourCause.printStackTrace(s);
            }
            s.flush();
        }
    }
    
}
