package hu.kolesar.netmonitor;

public class Cell {

    public Integer CC;
    public Integer NC;
    public Integer LAC;
    public Integer CID;
    public Integer CH;
    public Integer BSIC;

    public String toString() {
        return String.format("%d %d %d %d %d %d",
            CC, NC, LAC, CID, CH, BSIC);
    }

    @Override
    public int hashCode() {
        int c = 1;
        if (CC   != null) c = c * 11 + CC.hashCode();
        if (NC   != null) c = c * 13 + NC.hashCode();
        if (LAC  != null) c = c * 17 + LAC.hashCode();
        if (CID  != null) c = c * 19 + CID.hashCode();
        if (CH   != null) c = c * 23 + CH.hashCode();
        if (BSIC != null) c = c * 29 + BSIC.hashCode();
        return c;
    }

    @Override
    public boolean equals(Object other) {
       if (!(other instanceof Cell))
            return false;
        if (other == this)
            return true;

        Cell rhs = (Cell) other;
        return
            (CC   == null || CC.equals(rhs.CC)) &&
            (NC   == null || NC.equals(rhs.NC)) &&
            (LAC  == null || LAC.equals(rhs.LAC)) &&
            (CID  == null || CID.equals(rhs.CID)) &&
            (CH   == null || CH.equals(rhs.CH)) &&
            (BSIC == null || BSIC.equals(rhs.BSIC));
    }
}
