package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String planet,
                                                   @RequestParam(required = false) ShipType shipType,
                                                   @RequestParam(required = false) Long after,
                                                   @RequestParam(required = false) Long before,
                                                   @RequestParam(required = false) Boolean isUsed,
                                                   @RequestParam(required = false) Double minSpeed,
                                                   @RequestParam(required = false) Double maxSpeed,
                                                   @RequestParam(required = false) Integer minCrewSize,
                                                   @RequestParam(required = false) Integer maxCrewSize,
                                                   @RequestParam(required = false) Double minRating,
                                                   @RequestParam(required = false) Double maxRating,
                                                   @RequestParam(required = false) ShipOrder order,
                                                   @RequestParam(required = false) Integer pageNumber,
                                                   @RequestParam(required = false) Integer pageSize) {

        List<Ship> resultList = shipService.getShipsList(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
        if (resultList == null || resultList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count")
    public ResponseEntity<Integer> getShipsCount(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String planet,
                                              @RequestParam(required = false) ShipType shipType,
                                              @RequestParam(required = false) Long after,
                                              @RequestParam(required = false) Long before,
                                              @RequestParam(required = false) Boolean isUsed,
                                              @RequestParam(required = false) Double minSpeed,
                                              @RequestParam(required = false) Double maxSpeed,
                                              @RequestParam(required = false) Integer minCrewSize,
                                              @RequestParam(required = false) Integer maxCrewSize,
                                              @RequestParam(required = false) Double minRating,
                                              @RequestParam(required = false) Double maxRating) {
        int result = shipService.getShipsCount(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        if (result <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/ships")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return new ResponseEntity<>(shipService.createShip(ship), HttpStatus.OK);
    }


    @GetMapping("/ships/{id}")
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
       return new ResponseEntity<>(shipService.getShipById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable Long id, @RequestBody Ship ship) {
        return new ResponseEntity<>(shipService.updateShip(id, ship), HttpStatus.OK);
    }


    @DeleteMapping("/ships/{id}")
    public ResponseEntity<?> deleteShip(@PathVariable Long id) {
        return new ResponseEntity<>(shipService.deleteShip(id), HttpStatus.OK);
    }

}
