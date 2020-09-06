package com.space.service;


import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {

    List<Ship> getShipsList(String name,
                      String planet,
                      ShipType shipType,
                      Long after,
                      Long before,
                      Boolean isUsed,
                      Double minSpeed,
                      Double maxSpeed,
                      Integer minCrewSize,
                      Integer maxCrewSize,
                      Double minRating,
                      Double maxRating,
                      ShipOrder order,
                      Integer pageNumber,
                      Integer pageSize);

    int getShipsCount(String name,
                 String planet,
                 ShipType shipType,
                 Long after,
                 Long before,
                 Boolean isUsed,
                 Double minSpeed,
                 Double maxSpeed,
                 Integer minCrewSize,
                 Integer maxCrewSize,
                 Double minRating,
                 Double maxRating);

    Ship createShip(Ship ship);

    Ship getShipById(Long id);

    Ship updateShip(Long id, Ship ship);

    boolean deleteShip(Long id);


}
