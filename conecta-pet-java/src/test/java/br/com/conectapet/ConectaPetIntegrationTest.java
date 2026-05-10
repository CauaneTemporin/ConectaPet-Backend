//package br.com.conectapet;
//
//import br.com.conectapet.dto.*;
//import br.com.conectapet.model.Animal;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.*;
//import java.math.BigDecimal;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class ConectaPetIntegrationTest {
//
//    @Autowired MockMvc mvc;
//    @Autowired ObjectMapper mapper;
//
//    static String adminToken;
//    static String userToken;
//
//    // ── AUTH ────────────────────────────────────────────────
//
//    @Test @Order(1)
//    void loginAdmin() throws Exception {
//        var req = new AuthDTOs.LoginRequest("admin@conectapet.org", "admin123");
//        var result = mvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.token").isNotEmpty())
//            .andExpect(jsonPath("$.user.role").value("ADMIN"))
//            .andReturn();
//
//        var body = mapper.readTree(result.getResponse().getContentAsString());
//        adminToken = body.get("token").asText();
//    }
//
//    @Test @Order(2)
//    void loginDemo() throws Exception {
//        var req = new AuthDTOs.LoginRequest("demo@conectapet.org", "demo123");
//        var result = mvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.user.role").value("USER"))
//            .andReturn();
//
//        var body = mapper.readTree(result.getResponse().getContentAsString());
//        userToken = body.get("token").asText();
//    }
//
//    @Test @Order(3)
//    void loginInvalidCredentials() throws Exception {
//        var req = new AuthDTOs.LoginRequest("nobody@example.com", "wrongpass");
//        mvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isUnauthorized());
//    }
//
//    @Test @Order(4)
//    void registerNewUser() throws Exception {
//        var req = new AuthDTOs.RegisterRequest(
//            "Test User", "testuser@example.com", "senha123", "Campinas");
//        mvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.token").isNotEmpty());
//    }
//
//    @Test @Order(5)
//    void registerDuplicateEmail() throws Exception {
//        var req = new AuthDTOs.RegisterRequest(
//            "Another User", "admin@conectapet.org", "senha123", "SP");
//        mvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isConflict());
//    }
//
//    @Test @Order(6)
//    void getMyProfile() throws Exception {
//        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + userToken))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.email").value("demo@conectapet.org"));
//    }
//
//    // ── ANIMALS ─────────────────────────────────────────────
//
//    @Test @Order(10)
//    void listAnimalsPublic() throws Exception {
//        mvc.perform(get("/api/animals"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$").isArray())
//            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
//    }
//
//    @Test @Order(11)
//    void filterAnimalsBySpecies() throws Exception {
//        mvc.perform(get("/api/animals?species=CACHORRO&status=DISPONIVEL"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$[*].species").value(
//                org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is("cachorro"))));
//    }
//
//    @Test @Order(12)
//    void searchAnimalsByName() throws Exception {
//        mvc.perform(get("/api/animals?q=luna"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
//    }
//
//    @Test @Order(13)
//    void createAnimalAsAdmin() throws Exception {
//        var req = new AnimalDTOs.AnimalRequest(
//            "Bolinha", Animal.Species.CACHORRO, "SRD", Animal.Gender.MACHO,
//            1.5, Animal.Size.PEQUENO, "Muito dócil e brincalhão.", "Dócil",
//            true, true, Animal.AnimalStatus.DISPONIVEL, "🐕", null);
//
//        mvc.perform(post("/api/animals")
//                .header("Authorization", "Bearer " + adminToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.name").value("Bolinha"));
//    }
//
//    @Test @Order(14)
//    void createAnimalForbiddenForUser() throws Exception {
//        var req = new AnimalDTOs.AnimalRequest(
//            "Teste", Animal.Species.GATO, null, null, null,
//            null, null, null, false, false, null, null, null);
//
//        mvc.perform(post("/api/animals")
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isForbidden());
//    }
//
//    // ── ADOPTIONS ────────────────────────────────────────────
//
//    @Test @Order(20)
//    void requestAdoption() throws Exception {
//        var req = new AdoptionDTOs.AdoptionRequest(1L, "Adoro cães labrador!");
//        mvc.perform(post("/api/adoptions")
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.status").value("pendente"));
//    }
//
//    @Test @Order(21)
//    void requestAdoptionDuplicate() throws Exception {
//        var req = new AdoptionDTOs.AdoptionRequest(1L, "Segunda tentativa");
//        mvc.perform(post("/api/adoptions")
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isConflict());
//    }
//
//    @Test @Order(22)
//    void listMyAdoptions() throws Exception {
//        mvc.perform(get("/api/adoptions/mine")
//                .header("Authorization", "Bearer " + userToken))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
//    }
//
//    @Test @Order(23)
//    void listAllAdoptionsAsAdmin() throws Exception {
//        mvc.perform(get("/api/adoptions")
//                .header("Authorization", "Bearer " + adminToken))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$").isArray());
//    }
//
//    }
//
//    @Test @Order(31)
//    void donationStats() throws Exception {
//        mvc.perform(get("/api/donations/stats"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.total").isNumber())
//            .andExpect(jsonPath("$.count").isNumber());
//    }
//
//    // ── VOLUNTEER ────────────────────────────────────────────
//
//    @Test @Order(40)
//    void registerVolunteer() throws Exception {
//        var req = new VolunteerDTOs.VolunteerRequest(
//            java.util.List.of("fotografia", "transporte"), "Fins de semana");
//        mvc.perform(post("/api/volunteers")
//                .header("Authorization", "Bearer " + ConectaPetIntegrationTest.userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isCreated());
//    }
//
//    @Test @Order(41)
//    void registerVolunteerDuplicate() throws Exception {
//        var req = new VolunteerDTOs.VolunteerRequest(java.util.List.of("fotografia"), "Manhãs");
//        mvc.perform(post("/api/volunteers")
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isConflict());
//    }
//
//    // ── CONTACT ──────────────────────────────────────────────
//
//    @Test @Order(50)
//    void sendContactMessage() throws Exception {
//        var req = new ContactDTOs.ContactRequest(
//            "João Silva", "joao@example.com", "(11) 99999-0000",
//            "Dúvida sobre adoção", "Gostaria de saber mais sobre o processo.");
//        mvc.perform(post("/api/contact")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(req)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.name").value("João Silva"));
//    }
//
//    // ── ADMIN ────────────────────────────────────────────────
//
//    @Test @Order(60)
//    void adminStats() throws Exception {
//        mvc.perform(get("/api/admin/stats")
//                .header("Authorization", "Bearer " + adminToken))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.totalAnimals").isNumber())
//            .andExpect(jsonPath("$.totalDonations").isNumber());
//    }
//
//    @Test @Order(61)
//    void adminStatsForbiddenForUser() throws Exception {
//        mvc.perform(get("/api/admin/stats")
//                .header("Authorization", "Bearer " + userToken))
//            .andExpect(status().isForbidden());
//    }
//
//    @Test @Order(62)
//    void adminEndpointUnauthorizedWithoutToken() throws Exception {
//        mvc.perform(get("/api/admin/stats"))
//            .andExpect(status().isForbidden());
//    }
//}
