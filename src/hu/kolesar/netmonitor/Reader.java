package hu.kolesar.netmonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class Reader {

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        Parser parser = new Parser();
        String line;
        while ((line = in.readLine()) != null) {
            parser.parseLine(line);
        }
        System.out.printf("lines: %d\n", parser.getLineCount());
    }
}
