package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    Ship getById(Long id) throws NotFoundException;

    List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                            Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                            Integer maxCrewSize, Double minRating, Double maxRating);

    Ship createShip(Ship ship);

    Ship updateShip(Ship ship, Long id);

    void deleteShip(Long id);

    List<Ship> displayListShips (List<Ship> allShips, ShipOrder order, Integer pageNumber, Integer pageSize);
}