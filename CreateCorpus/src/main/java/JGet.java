/*
 * A simple Java class to provide functionality similar to Wget.
 *
 * Note: Could also strip out all of the html w/ jtidy.
 */

import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.Key;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JGet {

    String path;

    public void downloadBibTex(String url, long number, String path) {
        this.path = path;
        URL u;
        InputStream is = null;
        String bibtex = "";

        String type = findType(number);
        System.out.println(type);
        try
        {
            u = new URL(url);
            is = u.openStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8));
            String s;
            while ((s = in.readLine()) != null) {
                if (s.contains("url= {")) {
                } else if (s.contains("crossref = {")) {
                } else {
                    if(s.contains("abstract") || s.contains("keywords") || s.equals("},") || s.equals("type")) {
                        //do nothing
                        System.out.println();
                    } else if(!s.equals("}") && !s.contains("{") && !s.trim().isEmpty()) {
                        System.out.println();
                    } else {
                        if (s.contains("year = {")) {
                            String year = "";
                            Pattern regex = Pattern.compile("\\d\\d\\d\\d");
                            Matcher regexMatcher = regex.matcher(s);
                            if (regexMatcher.find()) {
                                year = regexMatcher.group();
                            }
                            s = "year = {" + year + "},";
                        }
                        String sReplaced = s; //.replace("%", "\\%");
                        //sReplaced = sReplaced.replace("&", "\\&");
                        /*if (sReplaced.contains("title")) {
                            sReplaced = sReplaced.replace("{", "{{");
                            sReplaced = sReplaced.replace("}", "}}");
                        }*/
                        //sReplaced = sReplaced.replace('\u2013', '\u002D');
                        sReplaced = sReplaced.replace('’', '\'');
                        sReplaced = sReplaced.replace('“', '\"');
                        sReplaced = sReplaced.replace('”', '\"');

                        //sReplaced = sReplaced.replace("ä", "\\\"a");
                        //sReplaced = sReplaced.replace("ö", "\\\"o");
                        //sReplaced = sReplaced.replace("ü", "\\\"u");

                        if (!type.equals("other") && !type.isEmpty() && !type.equals("event")) {
                            sReplaced = sReplaced.replace("@misc", "@" + type);
                        }

                        if (!type.equals("article") && !type.equals("book") && !type.equals("incollection")) {
                            System.out.println("TEST TYPE: " + type);
                        }

                        bibtex += sReplaced + "\n";
                    }
                }
            }
            System.out.println(bibtex);
            addToFile(bibtex);
        }
        catch (MalformedURLException mue)
        {
            System.err.println("Ouch - a MalformedURLException happened.");
            mue.printStackTrace();
            System.exit(2);
        }
        catch (IOException ioe)
        {
            System.err.println("Oops- an IOException happened.");
            ioe.printStackTrace();
            System.exit(3);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException ioe)
            {
            }
        }
    }

    private static String findType(long number) {
        String type = "";
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;

        String urlType = "https://api.econbiz.de/v1/record/" + number;
        try
        {
            u = new URL(urlType);
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                String pattern = "(?<=\"type\":\")(.*?)(?=\")";

                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);

                // Now create matcher object.
                Matcher m = r.matcher(s);
                if (m.find( )) {
                    System.out.println("Found value: " + m.group(0) );
                    type = m.group(0);

                    if (type.equals("article")) {
                        if (s.contains("Aufsatz im Buch") || s.contains("Article in collection")) {
                            type = "incollection";
                        }
                    } else if (type.equals("journal")) {
                        type = "article";
                    }
                } else {
                    System.out.println("NO MATCH");
                }
            }
        }
        catch (MalformedURLException mue)
        {
            System.err.println("Ouch - a MalformedURLException happened.");
            mue.printStackTrace();
        }
        catch (IOException ioe)
        {
            System.err.println("Oops- an IOException happened.");
            ioe.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException ioe)
            {
            }
        }
        return type;
    }

    private void addToFile(String s) {

        try {
            Files.write(Paths.get(path + "\\bibliography.txt"), s.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(path + "\\bibliography.bib"), s.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
            //System.out.println(entry.getFields());

    }

}