package org.example.rest;

import java.util.List;

import org.example.domain.Item;
import org.example.service.ItemServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/item")
public class ItemController {

    private ItemServices service;

    public ItemController(ItemServices service) {
        super();
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Item> createItem(@RequestBody Item newItem) {
        return this.service.createItem(newItem);
    }

    @GetMapping("/get")
    public List<Item> getItems() {
        return this.service.getItems();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Item> getItem(@PathVariable int id) {
        return this.service.getItem(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item itemDetails) {
        return this.service.updateItem(id, itemDetails);
    }

    // Delete items by ID
    @DeleteMapping("/delete/{id}")
    public boolean deleteItem(@PathVariable int id) {
        return this.service.deleteItem(id);
    }
}
