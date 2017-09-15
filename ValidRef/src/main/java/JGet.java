/*
 * A simple Java class to provide functionality similar to Wget.
 *
 * Note: Could also strip out all of the html w/ jtidy.
 */

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JGet {

    public String downloadBibTex(String url) {

        URL u;
        InputStream is = null;
        String bibtex = "";
        try {
            u = new URL(url);
            is = u.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8));
            String s;
            while ((s = in.readLine()) != null) {
                if (s.contains("url= {")) {
                } else if (s.contains("crossref = {")) {
                } else {
                    if (s.contains("abstract") || s.contains("keywords") || s.equals("},")) {
                        //do nothing
                        System.out.println();
                    } else if (!s.equals("}") && !s.contains("{") && !s.trim().isEmpty()) {
                        System.out.println();
                    } else {
                        if (s.contains("year = {")) {
                            String year = s.substring(8, 12);
                            s = "year = {" + year + "},";
                        }
                        String sReplaced = s;//.replace("%", "\\%");
                        //sReplaced = sReplaced.replace("&", "\\&");
                        if (sReplaced.contains("title")) {
                            sReplaced = sReplaced.replace("{", "{{");
                            sReplaced = sReplaced.replace("}", "}}");
                        }
                        //sReplaced = sReplaced.replace('\u2013', '\u002D');
                        //sReplaced = sReplaced.replace('’', '\'');
                        //sReplaced = sReplaced.replace('“', '\"');
                        //sReplaced = sReplaced.replace('”', '\"');

                        //sReplaced = sReplaced.replace("ä", "\\\"a");
                        //sReplaced = sReplaced.replace("ö", "\\\"o");
                        //sReplaced = sReplaced.replace("ü", "\\\"u");

                        bibtex += sReplaced + "\n";
                    }
                }
            }
        } catch (MalformedURLException mue) {
            System.err.println("Ouch - a MalformedURLException happened.");
            mue.printStackTrace();
            System.exit(2);
        } catch (IOException ioe) {
            System.err.println("Oops- an IOException happened.");
            ioe.printStackTrace();
            System.exit(3);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
            }
        }
        return bibtex;
    }
}