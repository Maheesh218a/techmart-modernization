package com.techmart.service;

import com.techmart.entity.Warehouse;
import com.techmart.repository.WarehouseRepository;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class WarehouseService {

    @EJB
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.find(id);
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse.getName() == null || warehouse.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name is required");
        }
        warehouseRepository.create(warehouse);
        return warehouse;
    }

    public Warehouse updateWarehouse(Long id, Warehouse updatedData) {
        Warehouse warehouse = warehouseRepository.find(id);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found");
        }
        
        if (updatedData.getName() != null && !updatedData.getName().trim().isEmpty()) {
            warehouse.setName(updatedData.getName());
        }
        if (updatedData.getLocation() != null) {
            warehouse.setLocation(updatedData.getLocation());
        }
        if (updatedData.getCapacity() != null) {
            warehouse.setCapacity(updatedData.getCapacity());
        }
        
        warehouseRepository.edit(warehouse);
        return warehouse;
    }

    public void updateWarehouseStatus(Long id, boolean isActive) {
        Warehouse warehouse = warehouseRepository.find(id);
        if (warehouse != null) {
            warehouse.setActive(isActive);
            warehouseRepository.edit(warehouse);
        } else {
            throw new IllegalArgumentException("Warehouse not found");
        }
    }
}
