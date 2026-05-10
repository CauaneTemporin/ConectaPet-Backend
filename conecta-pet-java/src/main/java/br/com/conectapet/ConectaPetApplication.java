package br.com.conectapet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ConectaPetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConectaPetApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("");
        log.info("╔══════════════════════════════════════╗");
        log.info("║   🐾  Conecta PET  — API rodando!   ║");
        log.info("║   http://localhost:8080/api          ║");
        log.info("║   Docs: /api/actuator/health         ║");
        log.info("╚══════════════════════════════════════╝");
        log.info("");
        log.info("Admin: admin@conectapet.org / admin123");
        log.info("Demo:  demo@conectapet.org  / demo123");
    }
}
