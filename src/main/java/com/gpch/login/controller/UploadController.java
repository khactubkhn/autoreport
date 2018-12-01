package com.gpch.login.controller;

import com.gpch.login.model.FileSave;
import com.gpch.login.model.User;
import com.gpch.login.service.FileService;
import com.gpch.login.service.RoomService;
import com.gpch.login.utils.MergeFileExcelsUtil;

import com.gpch.login.utils.ReadFileExcelUtil;
import com.itextpdf.text.DocumentException;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import reactor.ipc.netty.http.server.HttpServerResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/room")
public class UploadController {


    @Autowired
    MergeFileExcelsUtil mergeFileExcelsUtil;
   
    
    @Autowired
    RoomService roomService;
    
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    FileService fileService;
    
    @Autowired
    ReadFileExcelUtil readFileExcelUtil;
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "";

    @GetMapping("/upload")
    public String index() {
        mergeFileExcelsUtil.merge(1);
        return "upload";
    }

    

    @PostMapping("/upload/{roomId}") // //new annotation since 4.3
    public @ResponseBody Map<String, ? extends Object> singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, @PathVariable int roomId, HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
        if (file.isEmpty()) {
        	result.put("code", 1);
    		result.put("message", "No file");
    		return result;
        }
        String rootPath = System.getProperty("user.dir");
        UPLOADED_FOLDER = rootPath + "/src/main/resources/upload/room_"+roomId+"/";
        File dir = new File(UPLOADED_FOLDER);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            FileSave newFile = fileService.saveFileSaveById(user.getId(), roomId, path.toString(), file.getOriginalFilename());
            
            result.put("code", 0);
            result.put("data", newFile);
    		result.put("message", "OK");
            
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.put("code", 1);
		result.put("message", "ERROR");
		return result;
    }

    @RequestMapping(value = "/report_rom_1.pdf", method = RequestMethod.GET, produces="application/vnd.xls")
    public ResponseEntity<ByteArrayResource> genPDF(HttpServletRequest request, HttpServerResponse reponse){
        try {
            //pdfGenerator.genPDF2(1);
            
            String rootPath = System.getProperty("user.dir");
            String path1 = rootPath + "/src/main/resources/reports/report_rom_1.pdf";
            String filenameDB = "report_rom_1.pdf";
            File f = new File(path1 + "");
			FileInputStream fi = new FileInputStream(f);
			byte[] bytes = new byte[(int) f.length()];
			fi.read(bytes);

//			reponse.addHeader("content-disposition", "attachment;filename="
//					+ filenameDB);
//			
			
			
			
			
			String mineType = servletContext.getMimeType(filenameDB);
			
				MediaType mediaType = MediaType.parseMediaType(mineType);
				
				Path path = Paths.get(path1);
					byte[] data = Files.readAllBytes(path);
					ByteArrayResource resource = new ByteArrayResource(data);

						return ResponseEntity.ok()
				// Content-Disposition
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + path.getFileName().toString())
					// Content-Type
							.contentType(mediaType) //
								// Content-Lengh
							.contentLength(data.length) //
								.body(resource);
		    //return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/doc/{roomId}", method = RequestMethod.GET)
    public ModelAndView genDoc(@PathVariable int roomId){
        String rootPath = System.getProperty("user.dir");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report");
        String path_export = rootPath + "/src/main/resources/reports/";
        //List<Vector<String>> datas = mergeFileExcelsUtil.merge(roomId);
        List<Map<String, Object>> datas = roomService.getRoomTranscript(roomId);
        List<Vector<String>> datasHtml = new ArrayList<>();
        List<String> speakers = new ArrayList<>();
        //Blank Document
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph p = document.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.createRun().setText("BIÊN BẢN CUỘC HỌP");
        
        //roomService.updateRoomFileContent(roomId, 14);
        
        if (datas.size() > 1) {
        	document.createParagraph()
        	.createRun()
        	.setText("Thời gian bắt đầu: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Timestamp)datas.get(0).get("start")).getTime()));
        	
        	document.createParagraph()
        	.createRun()
        	.setText("Thời gian kết thúc: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Timestamp)datas.get(datas.size()-1).get("end")).getTime()));
        	
        } else {
        	document.createParagraph()
        	.createRun()
        	.setText("Thời gian bắt đầu: ");
        	
        	document.createParagraph()
        	.createRun()
        	.setText("Thời gian kết thúc: ");

        }
        
        File f = new File(path_export + "report_" + roomId +".docx");
        if(f.exists() && !f.isDirectory()) { 
            f.delete();
        }

        //Write the Document in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(path_export + "report_" + roomId +".docx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //create table
        XWPFTable table = document.createTable();
        //create first row
        XWPFTableRow tableRowOne = table.getRow(0);
        tableRowOne.getCell(0).setText("Bắt đầu");
        tableRowOne.addNewTableCell().setText("Kết thúc");
        tableRowOne.addNewTableCell().setText("Người nói");
        tableRowOne.addNewTableCell().setText("Nội dung");

        for(Map<String, Object> data: datas){
        	Map<String, Object> speaker = (Map<String, Object>) data.get("speaker");
            XWPFTableRow tableRow = table.createRow();

            String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Timestamp)data.get("start")).getTime());
            String end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((Timestamp)data.get("end")).getTime());
            String spk = (String)speaker.get("firstName") + " " + (String)speaker.get("lastName");
            String ctn = (String)data.get("content");
            tableRow.getCell(0).setText(start);
            tableRow.getCell(1).setText(end);
            tableRow.getCell(2).setText(spk);
            tableRow.getCell(3).setText(ctn);

            if(checkSpeaker(speakers, spk)){
                speakers.add(spk);
            }

            Vector<String> v = new Vector<>();
            v.add(start);
            v.add(end);
            v.add(spk);
            v.add(ctn);
            datasHtml.add(v);
        }

        try {
            document.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("create_table.docx written successully");
        String spkStr = "";
        for(int i = 0; i< speakers.size(); i++){
            spkStr += speakers.get(i);
            if(i != speakers.size()-1){
                spkStr += ", ";
            }
        }
        modelAndView.addObject("datas", datasHtml);
        modelAndView.addObject("spkStr", spkStr);
        return modelAndView;
    }

    public boolean checkSpeaker(List<String> speakers, String name){
        for(String s: speakers){
            if(s.equals(name)){
                return false;
            }
        }
        return true;
    }

}