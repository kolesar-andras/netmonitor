package hu.kolesar.netmonitor;

import java.util.Date;
import java.text.SimpleDateFormat;

class Interval<T extends Comparable<? super T>> {
    public T min;
    public T max;

    public void add(T value) {
        if (min == null || min.compareTo(value) > 0) min = value;
        if (max == null || max.compareTo(value) < 0) max = value;
    }
}

class DateInterval extends Interval<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String toString() {
        return String.format("%s - %s",
            dateFormat.format(min),
            dateFormat.format(max)
        );
    }
}
