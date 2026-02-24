package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.domain.Item;
import org.example.repo.ItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ItemServices {
    private ItemRepo repo;

    public ItemServices(ItemRepo repo) {
        super();
        this.repo = repo;
    }

    public ResponseEntity<Item> createItem(Item newItem) {
        Item created = this.repo.save(newItem);
        return new ResponseEntity<Item>(created, HttpStatus.CREATED);
    }

    public List<Item> getItems() {
        return this.repo.findAll();
    }

    // Find items by ID
    public ResponseEntity<Item> getItem(int id) {
        Optional<Item> found = this.repo.findById(id);
        // If the given ID is empty then returns status not found
        if (found.isEmpty()) {
            return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
        }
        // If the given ID finds data then returns Item data
        Item body = found.get();
        return ResponseEntity.ok(body);
    }

    // Update items by ID
    public ResponseEntity<Item> updateItem(int id, Item itemDetails) {
        Optional<Item> found = this.repo.findById(id);
        // If the given ID is empty then returns status not found
        if (found.isEmpty()) {
            return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
        }
        // If the given ID finds data then returns Item data
        Item exists = found.get();
        // If a field is not Null or 0 then set field input to XYZ
        if (itemDetails.getItemName() != null) {
            exists.setItemName(itemDetails.getItemName());
        }
        if (itemDetails.getConsole() != null) {
            exists.setConsole(itemDetails.getConsole());
        }
        if (itemDetails.getGenre() != null) {
            exists.setGenre(itemDetails.getGenre());
        }
        if (itemDetails.getStockLevel() != 0) {
            exists.setStockLevel(itemDetails.getStockLevel());
        }
        if (itemDetails.getPrice() != 0) {
            exists.setPrice(itemDetails.getPrice());
        }
        if (itemDetails.getInStock() != null) {
            exists.setInStock(itemDetails.getInStock());
        }
        if (itemDetails.getOnSale() != null) {
            exists.setOnSale(itemDetails.getOnSale());
        }
        // saves new data inside the fields and returns new data
        Item updated = this.repo.save(exists);
        return ResponseEntity.ok(updated);
    }

    // Remove item by ID
    public boolean deleteItem(int id) {
        this.repo.deleteById(id);
        return !this.repo.existsById(id);
    }
}
