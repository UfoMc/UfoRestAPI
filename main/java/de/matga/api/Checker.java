package de.matga.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Checker {

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<String> extractKeys(String jsonString) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        List<String> keys = new ArrayList<>();
        extractKeys(jsonNode, keys);

        return keys;
    }

    private void extractKeys(JsonNode jsonNode, List<String> keys) {
        Iterator<String> fieldNames = jsonNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            keys.add(fieldName);
            JsonNode nestedNode = jsonNode.get(fieldName);
            if (nestedNode.isObject()) {
                extractKeys(nestedNode, keys);
            }
        }
    }

    private String jsonFileToString(String filePath) throws Exception {
        return objectMapper.writeValueAsString(objectMapper.readValue(new File(filePath), Object.class));
    }

    public boolean isValid() {
        try {
            List<String> deployed = extractKeys(jsonFileToString(FileSystems.getDefault().getPath("")
                    .toAbsolutePath() + "/config/config.json"));
            List<String> local = extractKeys(jsonFileToString("src/main/resources/config.json"));

            for (String s : local){
                if (!deployed.contains(s)){
                    return false;
                }
            }

            return true;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
