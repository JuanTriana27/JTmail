package co.jtmail.service.impl;

import co.jtmail.dto.response.ThreadResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.mapper.ThreadMapper;
import co.jtmail.model.MailThread;
import co.jtmail.repository.ThreadRepository;
import co.jtmail.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThreadServiceImpl implements ThreadService {

    private final ThreadRepository threadRepository;

    @Override
    public List<ThreadResponse> getAllThreads() {
        return threadRepository.findAll()
                .stream()
                .map(ThreadMapper::toResponse)
                .toList();
    }

    @Override
    public ThreadResponse getThreadById(UUID id) {
        MailThread mailThread = threadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thread", id));
        return ThreadMapper.toResponse(mailThread);
    }

    @Override
    public ThreadResponse createThread() {
        // Thread se crea vacío — los emails lo van llenando
        MailThread mailThread = ThreadMapper.toEntity();
        return ThreadMapper.toResponse(threadRepository.save(mailThread));
    }

    @Override
    public void deleteThread(UUID id) {
        if (!threadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Thread", id);
        }
        threadRepository.deleteById(id);
    }
}