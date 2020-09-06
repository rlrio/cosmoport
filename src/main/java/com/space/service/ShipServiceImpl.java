package com.space.service;


import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }


    @Override
    public List<Ship> getShipsList(String name,
                                   String planet,
                                   ShipType shipType,
                                   Long after, Long before,
                                   Boolean isUsed,
                                   Double minSpeed, Double maxSpeed,
                                   Integer minCrewSize, Integer maxCrewSize,
                                   Double minRating, Double maxRating,
                                   ShipOrder order,
                                   Integer pageNumber,
                                   Integer pageSize) {

        List<Ship> resultList = findAllWithoutOrderAndPaging(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        if (order != null) {
            resultList.sort((ship1, ship2) -> {
                switch (order) {
                    case ID: return ship1.getId().compareTo(ship2.getId());
                    case SPEED:return ship1.getSpeed().compareTo(ship2.getSpeed());
                    case DATE:return ship1.getProdDate().compareTo(ship2.getProdDate());
                    case RATING:return ship1.getRating().compareTo(ship2.getRating());
                    default: return 0;
                }
            });
        }
        return getPage(resultList, pageNumber, pageSize);
    }
    @Override
    public int getShipsCount(String name,
                             String planet,
                             ShipType shipType,
                             Long after, Long before,
                             Boolean isUsed,
                             Double minSpeed, Double maxSpeed,
                             Integer minCrewSize, Integer maxCrewSize,
                             Double minRating, Double maxRating) {
        return findAllWithoutOrderAndPaging(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @Override
    public Ship createShip(Ship ship) {
        checkNullParamsForCreation(ship);
        checkValidParams(ship);
        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        ship.setRating(ratingCalculator(ship));
        return shipRepository.save(ship);
    }

    @Override
    public Ship getShipById(Long id) {
        isIDValidAndExists(id);
        return shipRepository.findById(id).get();
    }

    @Override
    public Ship updateShip(Long id, Ship newShipData) {
        Ship shipToUpdate = getShipById(id);
        boolean isChangedForRating = false;
        if (shipToUpdate.equals(newShipData) || newShipData == null || checkEmptyBodyOfEntity(newShipData)) return shipToUpdate;
        if (newShipData.getName() != null && !shipToUpdate.getName().equals(newShipData.getName())) {
            shipToUpdate.setName(newShipData.getName());
        }
        if (newShipData.getPlanet() != null && !shipToUpdate.getPlanet().equals(newShipData.getPlanet())) {
            shipToUpdate.setPlanet(newShipData.getPlanet());
        }
        if (newShipData.getShipType() != null && !shipToUpdate.getShipType().equals(newShipData.getShipType())) {
            shipToUpdate.setShipType(newShipData.getShipType());
        }
        if (newShipData.getProdDate() != null && !shipToUpdate.getProdDate().equals(newShipData.getProdDate())) {
            shipToUpdate.setProdDate(newShipData.getProdDate());
            isChangedForRating = true;
        }
        if (newShipData.getUsed() != null && !shipToUpdate.getUsed().equals(newShipData.getUsed())) {
            shipToUpdate.setUsed(newShipData.getUsed());
            isChangedForRating = true;
        }
        if (newShipData.getSpeed() != null && !shipToUpdate.getSpeed().equals(newShipData.getSpeed())) {
            shipToUpdate.setSpeed(newShipData.getSpeed());
            isChangedForRating = true;
        }
        if (newShipData.getCrewSize() != null && !shipToUpdate.getCrewSize().equals(newShipData.getCrewSize())) {
            shipToUpdate.setCrewSize(newShipData.getCrewSize());
        }
        if (isChangedForRating) {
          shipToUpdate.setRating(ratingCalculator(shipToUpdate));
        }
        checkValidParams(shipToUpdate);
        return shipRepository.save(shipToUpdate);
    }

    @Override
    public boolean deleteShip(Long id) {
       isIDValidAndExists(id);
       shipRepository.deleteById(id);
       return true;
    }

    private void isIDValidAndExists(Long id) {
        if (id == null || id <= 0 || id != Math.floor(id)) throw new BadRequestException("Id is not valid.");
        else if (!shipRepository.existsById(id)) throw new NotFoundException(String.format("Ship by id = %d does not exist.", id));
        else {
            shipRepository.existsById(id);
        }
    }

    private boolean checkEmptyBodyOfEntity(Ship ship) {
        return  ship.getName() == null
                && ship.getPlanet() == null
                && ship.getShipType() == null
                && ship.getProdDate() == null
                && ship.getSpeed() == null
                && ship.getCrewSize() == null
                && ship.getUsed() == null
                && ship.getRating() == null;
    }

    private void checkNullParamsForCreation(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null
        ) throw new BadRequestException("One or more required fields is empty.");
    }

    private void checkValidParams(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException("Incorrect Ship name.");

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException("Incorrect Ship planet.");

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException("Incorrect Ship crewSize.");

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            throw new BadRequestException("Incorrect Ship speed.");

        if (ship.getProdDate() != null && (yearFromDate(ship.getProdDate()) < 2800 || yearFromDate(ship.getProdDate()) > 3019)) {
                throw new BadRequestException("Incorrect Ship production date.");
        }
    }

    private int yearFromDate(Date date) {
        Calendar year = Calendar.getInstance();
        year.setTime(date);
        return year.get(Calendar.YEAR);
    }

    private Double ratingCalculator(Ship ship) {
        int year = yearFromDate(ship.getProdDate());
        double rating = (80.00 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) /
                (3019 - year + 1);
        return ((double) Math.round(rating * 100) / 100);
    }


    private List<Ship> findAllWithoutOrderAndPaging(String name,
                                                    String planet,
                                                    ShipType shipType,
                                                    Long after, Long before,
                                                    Boolean isUsed,
                                                    Double minSpeed, Double maxSpeed,
                                                    Integer minCrewSize, Integer maxCrewSize,
                                                    Double minRating, Double maxRating) {
        return shipRepository.findAll().stream()
                .filter(o -> name == null || o.getName().contains(name))
                .filter(o -> planet == null || o.getPlanet().contains(planet))
                .filter(o -> shipType == null || o.getShipType().equals(shipType))
                .filter(o -> after == null || o.getProdDate().getTime() >= new Date(after).getTime())
                .filter(o -> before == null || o.getProdDate().getTime() <= new Date(before).getTime())
                .filter(o -> isUsed == null || o.getUsed().equals(isUsed))
                .filter(o -> minSpeed == null || o.getSpeed() >= minSpeed)
                .filter(o -> maxSpeed == null || o.getSpeed() <= maxSpeed)
                .filter(o -> minCrewSize == null || o.getCrewSize() >= minCrewSize)
                .filter(o -> maxCrewSize == null || o.getCrewSize() <= maxCrewSize)
                .filter(o -> minRating == null || o.getRating() >= minRating)
                .filter(o -> maxRating == null || o.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    private List<Ship> getPage(List<Ship> ship, Integer pageNumber, Integer pageSize) {
        int page = pageNumber == null ? 0 : pageNumber;
        int size = pageSize == null ? 3 : pageSize;
        int x = page * size;
        int y = x + size;
        if ( y > ship.size()) {
            y = ship.size();
        }
        return ship.subList(x, y);
    }



}
