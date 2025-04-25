package tn.enicarthage.backendandroid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.dto.request.LieuDTO;
import tn.enicarthage.backendandroid.services.LieuService;

import java.util.List;

@RestController
@RequestMapping("/api/lieux")
@RequiredArgsConstructor
public class LieuController {
    private final LieuService lieuService;

    @GetMapping
    public ResponseEntity<List<LieuDTO>> getAllLieux() {
        return ResponseEntity.ok(lieuService.getAllLieux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LieuDTO> getLieuById(@PathVariable Long id) {
        return lieuService.getLieuById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<LieuDTO>> searchLieuxByName(@RequestParam String name) {
        return ResponseEntity.ok(lieuService.searchLieuxByName(name));
    }

    @GetMapping("/capacity")
    public ResponseEntity<List<LieuDTO>> getLieuxByMinCapacity(@RequestParam int capacity) {
        return ResponseEntity.ok(lieuService.getLieuxByMinCapacity(capacity));
    }

    @PostMapping
    public ResponseEntity<LieuDTO> createLieu(@RequestBody LieuDTO lieuDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lieuService.createLieu(lieuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LieuDTO> updateLieu(@PathVariable Long id, @RequestBody LieuDTO lieuDTO) {
        return lieuService.updateLieu(id, lieuDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLieu(@PathVariable Long id) {
        if (lieuService.deleteLieu(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
