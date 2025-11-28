package edu.univ.erp.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to import data from CSV
 */
public class CsvImportUtil {

    public static List<String[]> importFromCsv(String filePath) throws IOException, CsvValidationException {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip header
            reader.readNext();

            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
            }
        }
        return data;
    }
}
