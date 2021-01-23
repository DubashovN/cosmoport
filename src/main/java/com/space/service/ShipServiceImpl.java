package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        if (!isValidId(id)) {
            throw new BadRequestException();
        }

        Ship ship = shipRepository.findById(id).orElse(null);

        checkShipOnNull(ship);

        return ship;
    }

    @Override
    public Ship createShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null) {
            throw new BadRequestException();
        }

        checkShipParams(ship);

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        ship.setRating(shipRating(ship));

        shipRepository.saveAndFlush(ship);
        return ship;
    }

    @Override
    public Ship updateShip(Ship ship, Long id) {
        if (!isValidId(id)) {
            throw new BadRequestException();
        }

        checkShipParams(ship);

        Ship shipUpdate = getById(id);
        checkShipOnNull(shipUpdate);

        if (ship.getName() != null) {
            shipUpdate.setName(ship.getName());
        }

        if (ship.getPlanet() != null) {
            shipUpdate.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            shipUpdate.setShipType(ship.getShipType());
        }
        if (ship.getProdDate() != null) {
            shipUpdate.setProdDate(ship.getProdDate());
        }
        if (ship.getUsed() != null) {
            shipUpdate.setUsed(ship.getUsed());
        }
        if (ship.getSpeed() != null) {
            shipUpdate.setSpeed(ship.getSpeed());
        }
        if (ship.getCrewSize() != null) {
            shipUpdate.setCrewSize(ship.getCrewSize());
        }


        shipUpdate.setRating(shipRating(shipUpdate));
        return shipRepository.save(shipUpdate);
    }

    @Override
    public void deleteShip(Long id) {
        if (!isValidId(id)) {
            throw new BadRequestException();
        }

        Ship ship = getById(id);
        checkShipOnNull(ship);

        shipRepository.delete(ship);
    }

    private Boolean isValidId(Long id) {
        return id != null && id == Math.floor(id) && id > 0;
    }

    private double shipRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        double rate = ship.getUsed() ? 0.5 : 1;

        BigDecimal rating = BigDecimal.valueOf((80 * ship.getSpeed() * rate) / (3019 - year + 1));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }

    private void checkShipParams(Ship ship) {
        checkShipOnNull(ship);

        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException();

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException();

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException();

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            throw new BadRequestException();

        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                throw new BadRequestException();
        }
    }

    @Override
    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ships = shipRepository.findAll();
        if (name != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (planet != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }
        if (shipType != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }
        if (after != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getProdDate().after(new Date(after)))
                    .collect(Collectors.toList());
        }
        if (before != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getProdDate().before(new Date(before)))
                    .collect(Collectors.toList());
        }
        if (isUsed != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }
        if (minSpeed != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }
        if (minRating != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }
        if (maxRating != null) {
            ships = ships.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }
        return ships;
    }


    private void checkShipOnNull(Ship ship) {
        if (ship == null) {
            throw new NotFoundException();
        }
    }

    @Override
    public List<Ship> displayListShips (List<Ship> allShips, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ?  0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;
        return allShips.stream()
                .sorted(getComparator(order))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private Comparator<? super Ship> getComparator(ShipOrder order) {
        if (order == null) {
            return Comparator.comparing(Ship::getId);
        }
        Comparator<Ship> comparator = null;
        switch (order.getFieldName()) {
            case "id":
                comparator = Comparator.comparing(Ship::getId);
                break;
            case "speed":
                comparator = Comparator.comparing(Ship::getSpeed);
                break;
            case "prodDate":
                comparator = Comparator.comparing(Ship::getProdDate);
                break;
            case "rating":
                comparator = Comparator.comparing(Ship::getRating);
                break;
        }
        return comparator;
    }
}
