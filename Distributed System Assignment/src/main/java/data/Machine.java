package data;

public class Machine {

    public String ipAddress;
    public int timestamp;
    public boolean status;
    public boolean gateway;

    public Machine(String ipAddress, int timestamp, boolean status, boolean gateway) {

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
