package Utils;

import java.net.Socket;

public class DataPackage {
    public String originalStringValue;
    public Socket socket;

    public DataPackage(String data, Socket socket) {
        this.originalStringValue = data;
        this.socket = socket;
    }

    public int GetInt() {
        return Integer.parseInt(originalStringValue);
    }

    public float GetFloat() {
        return Float.parseFloat(originalStringValue);
    }

    public double GetDouble() {
        return Double.parseDouble(originalStringValue);
    }

    public boolean GetBool() {
        return Boolean.parseBoolean(originalStringValue);
    }
}
