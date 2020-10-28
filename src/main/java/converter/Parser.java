package converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Employee> staffXML;
    private List<Employee> staffCSV;

    public Parser() {
        staffXML = new ArrayList<>();
    }

    public String readString(String filename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String json;
        StringBuilder sb = new StringBuilder();
        while ((json = bufferedReader.readLine()) != null) {
            sb.append(json);
        }
        bufferedReader.close();
        return sb.toString();
    }

    public List<Employee> jsonToList(String json) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        List<Employee> staffJSON = new ArrayList<>();
        Gson gson = new Gson();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            Employee employee = gson.fromJson(jsonArray.get(i).toString(), Employee.class);
            staffJSON.add(employee);
        }
        return staffJSON;
    }

    public List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staffCSV = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staffCSV;
    }

    public String listToJson(List<Employee> list) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();

        String json = gson.toJson(list, listType);
        return json;
    }

    public void writeString(String json, String prefix) throws IOException {
        FileWriter fileWriter = new FileWriter("json" + prefix + ".json");
        fileWriter.write(json);
        fileWriter.flush();
        fileWriter.close();
    }

    public List<Employee> parseXML(String filename) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        Node root = doc.getDocumentElement();
        readXML(root);
        return staffXML;
    }

    private void readXML(Node root) {
        NodeList childNodes = root.getChildNodes();
        Employee employee = new Employee();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                if (!node.getNodeName().equals("employee")) {
                    Element element = (Element) node;
                    String content = element.getTextContent();
                    String nameInfo = element.getNodeName();
                    fillEmployee(content, employee, nameInfo);
                }
            }
            readXML(node);
        }
    }

    private void fillEmployee(String info, Employee employee, String nameInfo) {
        switch (nameInfo) {
            case "id":
                employee.setId(Long.decode(info));
                break;
            case "firstName":
                employee.setFirstName(info);
                break;
            case "lastName":
                employee.setLastName(info);
                break;
            case "country":
                employee.setCountry(info);
                break;
            case "age":
                employee.setAge(Integer.parseInt(info));
                staffXML.add(employee);
                break;
        }
    }
}
