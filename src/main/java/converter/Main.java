package converter;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException {
        Parser parser = new Parser();
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> parseCSV = parser.parseCSV(columnMapping, fileName);
        String jsonCSV = parser.listToJson(parseCSV);
        parser.writeString(jsonCSV, "CSV");

        List<Employee> parseXML = parser.parseXML("data.xml");
        String jsonXML = parser.listToJson(parseXML);
        parser.writeString(jsonXML, "XML");

        String json = parser.readString("jsonXML.json");
        List<Employee> list = parser.jsonToList(json);
        System.out.println(list);
    }
}
