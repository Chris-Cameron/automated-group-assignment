import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JFileChooser;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Choose the file containing the preference information");
            File inputFile = getSelectedFile();
            System.out.println("Choose the .txt file to print to");
            File outputFile = getSelectedFile();
            XSSFWorkbook wb = new XSSFWorkbook(inputFile);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp;

            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) cols = tmp;
                }
            }

            ArrayList<Integer> sizes = new ArrayList<Integer>();
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<int[]> data = new ArrayList<int[]>();
            ArrayList<Group> groups = new ArrayList<Group>();

            int startIndex = -1;
            int numSlots = 0;

            row = sheet.getRow(0);
            for (int c = 0; c < cols; c++) {
                cell = row.getCell((short) c);
                if (cell != null) {
                    sizes.add((int) cell.getNumericCellValue());
                    numSlots += (int) cell.getNumericCellValue();
                }
            }

            row = sheet.getRow(1);
            for (int c = 0; c < cols; c++) {
                cell = row.getCell((short) c);
                if (cell != null) {
                    if (startIndex >= 0) {
                        String cellName = cell.getStringCellValue();
                        int bracketPos = cellName.indexOf('[');
                        int finalPos = cellName.indexOf(']');
                        Group temp = new Group(cellName.substring(bracketPos, finalPos + 1));
                        groups.add(temp);
                    }
                    if (cell.getStringCellValue().equalsIgnoreCase("name")) {
                        startIndex = c;
                    }
                }
            }

            int[] qualities = new int[cols - startIndex - 1];

            for (int r = 2; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    cell = row.getCell((short) startIndex);
                    if (cell != null && !cell.getStringCellValue().equals("")) {
                        names.add(cell.getStringCellValue());
                        int[] rankings = new int[cols - startIndex - 1];
                        for (int c = startIndex + 1; c < cols; c++) {
                            cell = row.getCell((short) c);
                            if (cell != null) {
                                rankings[c - startIndex - 1] = (int) cell.getNumericCellValue();
                                qualities[c - startIndex - 1] += (int) cell.getNumericCellValue();
                            }
                        }
                        data.add(rankings);
                    }
                }
            }

            while(numSlots > names.size()){
                int[] rankings = new int[cols - startIndex - 1];
                for(int i = 1; i < rankings.length; i++){
                    rankings[i] = groups.size()/2;
                }
                data.add(rankings);
                names.add("");
            }


            while(numSlots > 0){
                int category = getMaxIndex(qualities);
                qualities[category] = 0;
                for(int i = 0; i < sizes.get(category); i++){
                    int minrank = Integer.MAX_VALUE;
                    int minpos = 0;
                    for(int j = 0; j < data.size(); j++){
                        if(data.get(j)[category] < minrank){
                            minrank = data.get(j)[category];
                            minpos = j;
                            if(minrank == 1) break;
                        }
                    }
                    if(!names.get(minpos).equals("")){
                        groups.get(category).addMember(names.get(minpos));
                    }
                    numSlots --;
                    data.remove(minpos);
                    names.remove(minpos);
                }
            }

            for(Group g: groups){
                g.print(outputFile);
            }

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

    }

    private static int getMaxIndex(int[] list) {
        int index = 0;
        int max = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] > max) {
                max = list[i];
                index = i;
            }
        }
        return index;
    }

    private static File getSelectedFile(){
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.showOpenDialog(null);
        File file;
        file = fileChooser.getSelectedFile();
        return file;
    }

}