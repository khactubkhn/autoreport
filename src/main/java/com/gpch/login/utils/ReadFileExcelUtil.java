package com.gpch.login.utils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class ReadFileExcelUtil {
    public static String rootPath = System.getProperty("user.dir");
    public static String BASE_URL = rootPath + "/src/main/resources/upload/room_";


    public List<Vector<String>> readWhoFile(int roomId){

        return readFile(roomId, "who");
    }

    public List<Vector<String>> readWhatFile(int roomId){

        return readFile(roomId, "what");
    }

    public List<Vector<String>> readFile(int roomId, String type){
        List<Vector<String>> listVector = new ArrayList<>();
        List<String> listFilesName = new ArrayList<>();
        String pathFolderOfRoom = BASE_URL + roomId;
        File dir = new File(pathFolderOfRoom);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if(fileName.contains(type)){
                listFilesName.add(fileName);
            }
        }
        for(String fileName: listFilesName){
            File file = new File(pathFolderOfRoom + "/" + fileName);
            Sheet sheet = getSheet(file);
            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                Vector<String> vector = new Vector<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String result = "";
                    if(cell.getCellTypeEnum() == CellType.NUMERIC){
                        result = String.valueOf((int)cell.getNumericCellValue());
                    }else{
                        result = cell.getStringCellValue();
                    }
                    vector.add(result);
                }
                listVector.add(vector);
            }
        }
        Collections.sort(listVector, new TranscriptComapre());
        return listVector;
    }

    public Sheet getSheet(File excelFile){
        Sheet sheet = null;
        try {
            Workbook workbook = WorkbookFactory.create(excelFile);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return sheet;
    }

    class TranscriptComapre implements Comparator<Vector<String>>{

        @Override
        public int compare(Vector<String> o1, Vector<String> o2) {
            if(DateUtil.parseDate(o1.get(0)) > DateUtil.parseDate(o2.get(0))){
                return 1;
            }
            if(DateUtil.parseDate(o1.get(0)) < DateUtil.parseDate(o2.get(0))){
                return -1;
            }
            return 0;
        }
    }
}