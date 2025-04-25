package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.LieuDTO;
import tn.enicarthage.backendandroid.models.GeoPoint;
import tn.enicarthage.backendandroid.models.Lieu;
import tn.enicarthage.backendandroid.repositories.LieuRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LieuService {
    private final LieuRepository lieuRepository;

    public List<LieuDTO> getAllLieux() {
        return lieuRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LieuDTO> getLieuById(Long id) {
        return lieuRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<LieuDTO> searchLieuxByName(String name) {
        return lieuRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LieuDTO> getLieuxByMinCapacity(int capacity) {
        return lieuRepository.findByCapacityGreaterThanEqual(capacity).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LieuDTO createLieu(LieuDTO lieuDTO) {
        Lieu lieu = convertToEntity(lieuDTO);
        Lieu savedLieu = lieuRepository.save(lieu);
        return convertToDTO(savedLieu);
    }

    public Optional<LieuDTO> updateLieu(Long id, LieuDTO lieuDTO) {
        if (!lieuRepository.existsById(id)) {
            return Optional.empty();
        }

        Lieu lieu = convertToEntity(lieuDTO);
        lieu.setId(id);
        Lieu updatedLieu = lieuRepository.save(lieu);
        return Optional.of(convertToDTO(updatedLieu));
    }

    public boolean deleteLieu(Long id) {
        if (!lieuRepository.existsById(id)) {
            return false;
        }

        lieuRepository.deleteById(id);
        return true;
    }

    private LieuDTO convertToDTO(Lieu lieu) {
        return new LieuDTO(
                lieu.getId(),
                lieu.getName(),
                lieu.getMapPosition(),
                lieu.getCapacity()
        );
    }

    private Lieu convertToEntity(LieuDTO lieuDTO) {
        return new Lieu(
                lieuDTO.getId(),
                lieuDTO.getName(),
                lieuDTO.getMapPosition(),
                lieuDTO.getCapacity()
        );
    }
    public GeoPoint extractCoordinatesFromMapPosition(String mapPosition) {
        try {
            // Supposons que mapPosition est au format "latitude,longitude"
            String[] coordinates = mapPosition.split(",");
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);
            return new GeoPoint(latitude, longitude);
        } catch (Exception e) {
            // En cas d'erreur, retourner des coordonnées par défaut (Paris)
            return new GeoPoint(48.8566, 2.3522);
        }
    }
}
