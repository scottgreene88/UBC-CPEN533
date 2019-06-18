package data;

public class ProcessClock {

    private long localProcessClock;

    public ProcessClock()
    {
        //start process clock
        this.localProcessClock = 1;
    }

    public synchronized void incrementClock()
    {
        localProcessClock++;
    }

    public synchronized long getClock()
    {
        return localProcessClock;
    }

    public synchronized void setClock(long clock)
    {
        localProcessClock = clock;
    }
}
