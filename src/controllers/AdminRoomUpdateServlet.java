package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import services.RoomService;

@WebServlet("/admin/room/update/*")
//записать в обсидиан
@MultipartConfig
public class AdminRoomUpdateServlet extends HttpServlet {
    
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        roomService = RoomService.getRoomService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer roomId = 0;
        if(postfixPath != null){
            String[] pathItems = postfixPath.split("/");
            if(pathItems.length == 2 && !pathItems[1].isEmpty() ){
                try{
                    roomId = Integer.parseInt(pathItems[1]);
                }catch (NumberFormatException e){
                    resp.sendError(404);
                    return;
                }
            }else{
                resp.sendError(404);
                return;
            }
        }else{
            resp.sendError(404);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Integer number = null;
        Integer guests = null;
        Integer floor = null;
        Integer buildingId = null;
        Float price = null;
        String description = partToString(req.getPart("description"));
        try {
            String numberStr = partToString(req.getPart("number"));
            if(numberStr != null){
                number = Integer.parseInt(numberStr);
            }
            String guestsStr = partToString(req.getPart("guests"));
            if(guestsStr != null){
                guests = Integer.parseInt(guestsStr);
            }
            String floorStr = partToString(req.getPart("floor"));
            if(floorStr != null){
                floor = Integer.parseInt(floorStr);
            }
            String buildingIdStr = partToString(req.getPart("building"));
            if(buildingIdStr != null){
                buildingId = Integer.parseInt(buildingIdStr);
            }
            String priceStr = partToString(req.getPart("price"));
            if(priceStr != null){
                price = Float.parseFloat(priceStr);
            }
        } catch (Exception e) {
            resp.sendError(406);
            e.printStackTrace();
            return;
        }   

        Part filePart = req.getPart("picture");
        String filename = null;
        String savedFilePath = null;
        if (filePart != null && filePart.getSize() > 0) {
            String uploadPath = getServletContext().getRealPath("") + File.separator + "images" + File.separator + "rooms";
            filename = roomId + "_" + System.currentTimeMillis() + getFileExtension(Paths.get(filePart.getSubmittedFileName()).getFileName().toString());
            savedFilePath = uploadPath + File.separator + filename;
        }

        roomService.updateRoom(roomId, buildingId, number, floor, guests, price, filename, description);

        if (savedFilePath != null) {
            InputStream fileContent = filePart.getInputStream();
            saveFile(fileContent, savedFilePath);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }
    
    private void saveFile(InputStream inputStream, String filePath) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            Integer bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private String partToString(Part part) throws IOException {
        if(part == null) return null;
        try (InputStream in = part.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}