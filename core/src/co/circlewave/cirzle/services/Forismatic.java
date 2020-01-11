package co.circlewave.cirzle.services;

import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Forismatic {

    private final static String BASE_URL = "http://api.forismatic.com/api/1.0/";
    private final static String API_METHOD_TITLE = "method";
    private final static String API_FORMAT_TITLE = "format";
    private final static String API_KEY_TITLE = "key";
    private final static String API_LANG_TITLE = "lang";
    private final static String API_METHOD = "getQuote";
    private final static String API_XML = "xml";
    private final static String XML_QUOTE_TEXT_PATH = "forismatic/quote/quoteText";
    private final static String XML_QUOTE_TEXT_AUTHOR_PATH = "/forismatic/quote/quoteAuthor";
    private final static int MIN = 1;
    private final static int MAX = 999999;

    private String language;

    public Forismatic() {
        this.language = "en";
    }

    public Forismatic.Quote getQuote() {
        return parseXML(getXML());
    }

    private Forismatic.Quote parseXML(String xmlString) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        InputSource source1 = new InputSource(new StringReader(xmlString)), source2 = new InputSource(new StringReader(xmlString));
        String text = null, author = null;
        try {
            text = xPath.evaluate(XML_QUOTE_TEXT_PATH, source1);
            author = xPath.evaluate(XML_QUOTE_TEXT_AUTHOR_PATH, source2);
        } catch (XPathException e) {
            e.printStackTrace();
        }
        return new Forismatic.Quote(text, author);
    }

    private String getXML() {
        String xmlString = "";
        String urlParametersString = API_METHOD_TITLE + "=" + API_METHOD + "&" +
                API_FORMAT_TITLE + "=" + API_XML + "&" + API_KEY_TITLE + "=" + getRandom().toString() + "&" +
                API_LANG_TITLE + "=" + language;
        try {
            URLConnection connection = new URL(BASE_URL).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("charset", "utf-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(urlParametersString);
            writer.flush();
            xmlString = convertInputStreamToString(connection);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlString;
    }

    private Integer getRandom() {
        return new Random().nextInt((MAX - MIN) + 1) + MIN;
    }


    private String convertInputStreamToString(URLConnection connection) {
        StringBuilder xmlString = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            while ((line = reader.readLine()) != null) {
                xmlString.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlString.toString();
    }

    public class Quote {

        private String quoteText;
        private String quoteAuthor;

        public Quote() {

        }

        public Quote(String quoteText, String quoteAuthor) {
            this.quoteText = quoteText;
            this.quoteAuthor = quoteAuthor;
        }

        public String getQuoteText() {
            return quoteText;
        }

        public void setQuoteText(String quoteText) {
            this.quoteText = quoteText;
        }

        public String getQuoteAuthor() {
            return quoteAuthor;
        }

        public void setQuoteAuthor(String quoteAuthor) {
            this.quoteAuthor = quoteAuthor;
        }
    }

}
