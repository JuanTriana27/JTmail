package co.jtmail.service.impl;

import co.jtmail.dto.response.ThreadResponse;
import co.jtmail.exception.ResourceNotFoundException;
import co.jtmail.model.MailThread;
import co.jtmail.repository.ThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceImplTest {

    @Mock
    private ThreadRepository threadRepository;

    @InjectMocks
    private ThreadServiceImpl threadService;

    private MailThread mockThread;
    private UUID threadId;

    @BeforeEach
    void setUp() {
        threadId = UUID.randomUUID();
        mockThread = MailThread.builder()
                .idThread(threadId)
                .createdAt(Instant.now())
                .lastEmailAt(Instant.now())
                .build();
    }

    // ─── getAllThreads ─────────────────────────────────────────

    @Test
    void getAllThreads_retornaLista() {
        when(threadRepository.findAll()).thenReturn(List.of(mockThread));

        List<ThreadResponse> result = threadService.getAllThreads();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIdThread()).isEqualTo(threadId);
    }

    @Test
    void getAllThreads_retornaListaVacia() {
        when(threadRepository.findAll()).thenReturn(List.of());

        assertThat(threadService.getAllThreads()).isEmpty();
    }

    // ─── getThreadById ─────────────────────────────────────────

    @Test
    void getThreadById_retornaThreadCuandoExiste() {
        when(threadRepository.findById(threadId)).thenReturn(Optional.of(mockThread));

        ThreadResponse result = threadService.getThreadById(threadId);

        assertThat(result.getIdThread()).isEqualTo(threadId);
    }

    @Test
    void getThreadById_lanzaExcepcionCuandoNoExiste() {
        when(threadRepository.findById(threadId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> threadService.getThreadById(threadId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createThread ──────────────────────────────────────────

    @Test
    void createThread_creaYRetornaThread() {
        when(threadRepository.save(any(MailThread.class))).thenReturn(mockThread);

        ThreadResponse result = threadService.createThread();

        assertThat(result).isNotNull();
        assertThat(result.getIdThread()).isEqualTo(threadId);
        verify(threadRepository).save(any(MailThread.class));
    }

    // ─── deleteThread ──────────────────────────────────────────

    @Test
    void deleteThread_eliminaCuandoExiste() {
        when(threadRepository.existsById(threadId)).thenReturn(true);

        threadService.deleteThread(threadId);

        verify(threadRepository).deleteById(threadId);
    }

    @Test
    void deleteThread_lanzaExcepcionCuandoNoExiste() {
        when(threadRepository.existsById(threadId)).thenReturn(false);

        assertThatThrownBy(() -> threadService.deleteThread(threadId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(threadRepository, never()).deleteById(any());
    }
}