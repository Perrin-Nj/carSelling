package com.projet_bd.carselling.controller;


import com.projet_bd.carselling.model.Car;
import com.projet_bd.carselling.model.Photo;
import com.projet_bd.carselling.model.ResponseMessage;
import com.projet_bd.carselling.model.ResponsePhoto;
import com.projet_bd.carselling.service.CarService;
import com.projet_bd.carselling.service.PhotoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin("*")


public class PhotoController {

    @Autowired
    private PhotoStorageService storageService;

    @Autowired
    private CarService carService;


    @PostMapping("car/{id}/photo")
    public ResponseEntity<ResponseMessage> uploadFile(
            @PathVariable("id") Long id,
            @RequestParam("photo") MultipartFile[] photos,
            @RequestParam("name") String name,
            @RequestParam("marque") String marque,
            @RequestParam("type") String type,
            @RequestParam("prix") Double prix,
            @RequestParam("titre") String title,
            @RequestParam("description") String description

            ) {
            String message = "";
            Car car = carService.findById(id);
            
            try {
                
                for (MultipartFile photo: photos){
                    storageService.store(photo, car);
                }
                message = "Uploaded file(s) successfully: ";
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
                message = "Could not upload the file: 'File size should exceed 200MB, make sure the file is not corrupted'!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
    }
    
    
    @GetMapping("/files")
    public ResponseEntity<List<ResponsePhoto>> getListFiles() {
        List<ResponsePhoto> files = storageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponsePhoto(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        Photo fileDB = storageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }


}
