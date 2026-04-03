package co.jtmail.service.impl;

import co.jtmail.dto.request.CreateLabelRequest;
import co.jtmail.dto.response.LabelResponse;
import co.jtmail.exception.ConflictException;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.mapper.LabelMapper;
import co.jtmail.model.Label;
import co.jtmail.model.User;
import co.jtmail.repository.LabelRepository;
import co.jtmail.repository.UserRepository;
import co.jtmail.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final UserRepository userRepository;

    @Override
    public List<LabelResponse> getLabelsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return labelRepository.findByUser(user)
                .stream()
                .map(LabelMapper::toResponse)
                .toList();
    }

    @Override
    public LabelResponse getLabelById(UUID id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", id));
        return LabelMapper.toResponse(label);
    }

    @Override
    public LabelResponse createLabel(UUID userId, CreateLabelRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Un usuario no puede tener dos labels con el mismo nombre
        if (labelRepository.existsByUserAndName(user, request.getName())) {
            throw new ConflictException("Ya existe un label con ese nombre");
        }

        Label label = LabelMapper.toEntity(request, user);
        return LabelMapper.toResponse(labelRepository.save(label));
    }

    @Override
    public LabelResponse updateLabel(UUID id, CreateLabelRequest request) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", id));

        // Los labels de sistema no se pueden modificar
        if (label.getIsSystem()) {
            throw new ConflictException("Los labels del sistema no se pueden modificar");
        }

        label.setName(request.getName());
        label.setColor(request.getColor());

        return LabelMapper.toResponse(labelRepository.save(label));
    }

    @Override
    public void deleteLabel(UUID id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label", id));

        // Los labels de sistema no se pueden eliminar
        if (label.getIsSystem()) {
            throw new ConflictException("Los labels del sistema no se pueden eliminar");
        }

        labelRepository.deleteById(id);
    }
}