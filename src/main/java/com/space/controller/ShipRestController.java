package com.space.controller;

import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipRestController {

    private final ShipService shipService;

    @Autowired
    public ShipRestController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShipById(@PathVariable("id") Long shipId) {
        Ship ship = this.shipService.getById(shipId);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @GetMapping(value = "/ships", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShip(@RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "planet", required = false) String planet,
                                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                 @RequestParam(value = "after", required = false) Long after,
                                                 @RequestParam(value = "before", required = false) Long before,
                                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                                 @RequestParam(value = "maxRating", required = false) Double maxRating,
                                                 @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                                 @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        List<Ship> ships = this.shipService.getShipsList(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating);

        if (ships.isEmpty()) {
            throw new NotFoundException();
        }

        List<Ship> displayList = this.shipService.displayListShips(ships, order, pageNumber, pageSize);

        return new ResponseEntity<>(displayList, HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getCountShip(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "planet", required = false) String planet,
                                @RequestParam(value = "shipType", required = false) ShipType shipType,
                                @RequestParam(value = "after", required = false) Long after,
                                @RequestParam(value = "before", required = false) Long before,
                                @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                @RequestParam(value = "minRating", required = false) Double minRating,
                                @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return shipService.getShipsList(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed,
                maxSpeed,
                minCrewSize,
                maxCrewSize,
                minRating,
                maxRating).size();
    }

    @PostMapping(value = "/ships", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {

        Ship shipCreate = this.shipService.createShip(ship);

        return new ResponseEntity<>(shipCreate, HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable("id") Long shipId) {
        Ship updateShip = this.shipService.updateShip(ship, shipId);

        return new ResponseEntity<>(updateShip, HttpStatus.OK);
    }

    @DeleteMapping(value = "/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void deleteShip(@PathVariable("id") Long shipId) {
        shipService.deleteShip(shipId);
    }
}