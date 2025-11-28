package edu.univ.erp.util;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Utility to export data to CSV
 */
public class CsvExportUtil {

    public static void exportToCsv(String filePath, String[] headers, List<String[]> data) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(headers);
            writer.writeAll(data);
        }
    }
}
