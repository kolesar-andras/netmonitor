package hu.kolesar.netmonitor;

public class Measurement {

    public Integer CC;
    public Integer NC;
    public Integer LAC;
    public Integer CID;
    public Integer CH;
    public Integer signal;
    public Integer TA;

    public String toString() {
        return String.format("%d %d %d %d %d %d %d",
            CC, NC, LAC, CID, CH, signal, TA);
    }
}
