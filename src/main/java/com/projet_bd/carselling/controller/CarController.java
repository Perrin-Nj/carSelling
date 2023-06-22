package com.projet_bd.carselling.controller;


import com.projet_bd.carselling.model.Car;
import com.projet_bd.carselling.model.Photo;
import com.projet_bd.carselling.model.ResponsePhoto;
import com.projet_bd.carselling.repository.CarRepository;
import com.projet_bd.carselling.repository.PhotoRepository;
import com.projet_bd.carselling.service.CarService;
import com.projet_bd.carselling.service.PhotoStorageService;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//@RequestMapping("api/v1/car")

@Controller
@CrossOrigin("*")
public class CarController {
    @Autowired
    private CarService carService;
    @Autowired
    private PhotoStorageService storageService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private CarRepository carRepository;


    @PostMapping(value = "/upload")
    public ResponseEntity<Car> uploadFile(
            @RequestParam("photo") MultipartFile[] photos,
            @RequestParam("name") String name,
            @RequestParam("marque") String marque,
            @RequestParam("type") String type,
            @RequestParam("prix") Double prix,
            @RequestParam("numChassi") String numChassi
    ) throws IOException {
        List<Photo> postedCarPhotos = new ArrayList<>();
        Car postedCar = new Car();
        postedCar.setName(name);
        postedCar.setPrice(prix);
        postedCar.setMarque(marque);
        postedCar.setType(type);
        postedCar.setNumChassi(numChassi);
        if(photos != null){
            for (MultipartFile file: photos){
                postedCarPhotos.add(storageService.store(file, postedCar));
            }
            postedCar.setListImages(postedCarPhotos);
        }


        Car car = carService.saveCar(postedCar);

        return ResponseEntity.ok(car);
    }

    @GetMapping("/cars")
    public ResponseEntity<?> getListCars() {
        try{
        List<Car> cars = carService.findAll();
        List<ResponsePhoto> responsePhotos = storageService.getAllFiles().map(dbFile -> {
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

        List<Photo> photos = photoRepository.findAll();
        List<Car> carListFinal = new ArrayList<>();

        //init links
        for(Car car: cars){
            car.setImageLinks(new ArrayList<>());
        }

        for(ResponsePhoto responsePhoto: responsePhotos){
            for(Photo photo: photos){
                for(Car car: cars){

                    if(responsePhoto.getName().equals(photo.getName())){
                        if(photo.getCar().getId().equals(car.getId())){
                            car.getImageLinks().add(responsePhoto.getUrl());
                        }
                    }
                    carListFinal.add(car);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(cars);}catch ( Exception e){
            return ResponseEntity.badRequest().body("An error occured, an image name should be unique");
        }
    }

    @GetMapping("/car/id/{id}")
    public ResponseEntity<?> findCarById(@PathVariable("id") Long id){
        Car car = carService.findById(id);
        if(car == null) return ResponseEntity.badRequest().body("No car with the specified id");
        car.setImageLinks(new ArrayList<>());
        List<ResponsePhoto> photoResponses = storageService.getAllFiles().map(dbFile -> {
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
        List<Photo> photos = photoRepository.findAll();

        for(ResponsePhoto responsePhoto: photoResponses){
            for(Photo photo: photos){
                    if(responsePhoto.getName().equals(photo.getName())){
                        if(photo.getCar().getId().equals(car.getId())){
                            car.getImageLinks().add(responsePhoto.getUrl());
                        }
                    }
            }
        }
        return ResponseEntity.ok(car);
    }
    @GetMapping("/car/name/{name}/")
    public ResponseEntity<?> findCarByName(@PathVariable("name") String name){
        Car car = carService.findCarByName(name);

        if(car == null) return ResponseEntity.badRequest().body("No car with the specified name");
        car.setImageLinks(new ArrayList<>());
        List<ResponsePhoto> photoResponses = storageService.getAllFiles().map(dbFile -> {
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
        List<Photo> photos = photoRepository.findAll();

        for(ResponsePhoto responsePhoto: photoResponses){
            for(Photo photo: photos){
                if(responsePhoto.getName().equals(photo.getName())){
                    if(photo.getCar().getId().equals(car.getId())){
                        car.getImageLinks().add(responsePhoto.getUrl());
                    }
                }
            }
        }
        return ResponseEntity.ok(car);
    }

    @GetMapping("/car/chassi/{chassi}/")
    public ResponseEntity<?> findCarByNumChassi(@PathVariable("chassi") String chassi){
        Car car = carService.findByCarChassi(chassi);
        if(car == null) return ResponseEntity.badRequest().body("No car with the specified chassi");
        car.setImageLinks(new ArrayList<>());
        List<ResponsePhoto> photoResponses = storageService.getAllFiles().map(dbFile -> {
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
        List<Photo> photos = photoRepository.findAll();

        for(ResponsePhoto responsePhoto: photoResponses){
            for(Photo photo: photos){
                if(responsePhoto.getName().equals(photo.getName())){
                    if(photo.getCar().getId().equals(car.getId())){
                        car.getImageLinks().add(responsePhoto.getUrl());
                    }
                }
            }
        }
        return ResponseEntity.ok(car);
    }

    @GetMapping("/cars/marque/{marque}")
    public ResponseEntity<?> getCarsByMarque(@PathVariable("marque") String  marque) {
        try{
            List<Car> cars = carService.findByCarMarque(marque);
            List<ResponsePhoto> responsePhotos = storageService.getAllFiles().map(dbFile -> {
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

            List<Photo> photos = photoRepository.findAll();
            List<Car> carListFinal = new ArrayList<>();

            //init links
            for(Car car: cars){
                car.setImageLinks(new ArrayList<>());
            }

            for(ResponsePhoto responsePhoto: responsePhotos){
                for(Photo photo: photos){
                    for(Car car: cars){

                        if(responsePhoto.getName().equals(photo.getName())){
                            if(photo.getCar().getId().equals(car.getId())){
                                car.getImageLinks().add(responsePhoto.getUrl());
                            }
                        }
                        carListFinal.add(car);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(cars);}catch ( Exception e){
            return ResponseEntity.badRequest().body("An error occured, an image name should be unique");
        }
    }

    @GetMapping("/cars/type/{type}")
    public ResponseEntity<?> getCarsByType(@PathVariable("type") String  type) {
        try{
            List<Car> cars = carService.findByCarsType(type);
            List<ResponsePhoto> responsePhotos = storageService.getAllFiles().map(dbFile -> {
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

            List<Photo> photos = photoRepository.findAll();
            List<Car> carListFinal = new ArrayList<>();

            //init links
            for(Car car: cars){
                car.setImageLinks(new ArrayList<>());
            }

            for(ResponsePhoto responsePhoto: responsePhotos){
                for(Photo photo: photos){
                    for(Car car: cars){

                        if(responsePhoto.getName().equals(photo.getName())){
                            if(photo.getCar().getId().equals(car.getId())){
                                car.getImageLinks().add(responsePhoto.getUrl());
                            }
                        }
                        carListFinal.add(car);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(cars);}catch ( Exception e){
            return ResponseEntity.badRequest().body("An error occured, an image name should be unique");
        }
    }

    @GetMapping("/cars/price/{price}")
    public ResponseEntity<?> getCarsByPrice(@PathVariable("price") Double  price) {
        try{
            List<Car> cars = carService.findByCarPrice(price);
            List<ResponsePhoto> responsePhotos = storageService.getAllFiles().map(dbFile -> {
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

            List<Photo> photos = photoRepository.findAll();
            List<Car> carListFinal = new ArrayList<>();

            //init links
            for(Car car: cars){
                car.setImageLinks(new ArrayList<>());
            }

            for(ResponsePhoto responsePhoto: responsePhotos){
                for(Photo photo: photos){
                    for(Car car: cars){
                        if(responsePhoto.getName().equals(photo.getName())){
                            if(photo.getCar().getId().equals(car.getId())){
                                car.getImageLinks().add(responsePhoto.getUrl());
                            }
                        }
                        carListFinal.add(car);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(cars);}catch ( Exception e){
            return ResponseEntity.badRequest().body("An error occured, an image name should be unique");
        }
    }

}
