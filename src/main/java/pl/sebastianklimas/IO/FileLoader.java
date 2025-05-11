package pl.sebastianklimas.IO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileLoader {
    public static <T> List<T> loadFile(String filename, TypeReference<List<T>> typeReference) {
        try {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(
                    new File(filename),
                    typeReference
            );
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return List.of();
    }
}
