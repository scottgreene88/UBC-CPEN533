package data;

public class machine {

    public String ipAddress;
    public int timestamp;
    public boolean status;
    public boolean gateway;

    public machine (String ipAddress, int timestamp, boolean status, boolean gateway) {

        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.status = status;
        this.gateway = gateway;

    }

    public String toString() {
        return "[" + ipAddress + " " + timestamp +
                " " + status + " " + gateway + "]";
    }
}
