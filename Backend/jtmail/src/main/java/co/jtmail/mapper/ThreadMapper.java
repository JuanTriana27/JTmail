package co.jtmail.mapper;

import co.jtmail.dto.response.ThreadResponse;
import co.jtmail.model.MailThread;

public class ThreadMapper {

    public static ThreadResponse toResponse(MailThread mailThread) {
        return ThreadResponse.builder()
                .idThread(mailThread.getIdThread())
                .createdAt(mailThread.getCreatedAt())
                .lastEmailAt(mailThread.getLastEmailAt())
                .build();
    }

    // Thread no tiene CreateRequest porque se crea internamente
    // cuando se envía un email, no desde un endpoint propio
    public static MailThread toEntity() {
        return MailThread.builder().build();
    }
}