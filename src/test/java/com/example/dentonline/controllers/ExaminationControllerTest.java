package com.example.dentonline.controllers;


import com.example.dentonline.DentOnlineApplication;
import com.example.dentonline.models.Examination;
import com.example.dentonline.services.ExaminationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Testcontainers
@SpringBootTest(classes = DentOnlineApplication.class)
public class ExaminationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExaminationService examinationService;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.3"));

    @BeforeAll
    static void initAll() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    static void tearDownAll() {
        mongoDBContainer.stop();
    }

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    void shouldAddExaminationSuccessfully() throws Exception {
        Examination examination = new Examination();
        examination.setDoctorId("doctor1");
        examination.setPatientId("patient1");
        examination.setDate(LocalDate.now());
        examination.setDescription("Test Description");


        Examination examinationOutput = new Examination();
        examinationOutput.setId("exam1");
        examinationOutput.setDoctorId(examination.getDoctorId());
        examinationOutput.setPatientId(examination.getPatientId());
        examinationOutput.setDate(examination.getDate());
        examinationOutput.setDescription(examination.getDescription());

        when(examinationService.createExamination(any(Examination.class))).thenReturn(examinationOutput);

        mockMvc.perform(post("/api/examinations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examination)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(examinationOutput.getId()))
                .andExpect(jsonPath("$.doctorId").value(examinationOutput.getDoctorId()))
                .andExpect(jsonPath("$.patientId").value(examinationOutput.getPatientId()))
                .andExpect(jsonPath("$.date").value(examinationOutput.getDate().toString()))
                .andExpect(jsonPath("$.description").value(examinationOutput.getDescription()));
    }

    @Test
    void shouldGetExaminationsForPatientSuccessfully() throws Exception {
        String patientId = "patient1";
        List<Examination> mockExaminations = new ArrayList<>();

        Examination exam1 = new Examination();
        exam1.setId("exam1");
        exam1.setDoctorId("doctor1");
        exam1.setPatientId(patientId);
        exam1.setDescription("Description1");
        exam1.setDate(LocalDate.now());
        mockExaminations.add(exam1);

        Examination exam2 = new Examination();
        exam2.setId("exam2");
        exam2.setDoctorId("doctor2");
        exam2.setPatientId(patientId);
        exam2.setDescription("Description2");
        exam2.setDate(LocalDate.now().plusDays(1));
        mockExaminations.add(exam2);

        when(examinationService.getExaminationsByPatient(patientId)).thenReturn(mockExaminations);

        mockMvc.perform(get("/api/examinations/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("exam1"))
                .andExpect(jsonPath("$[0].doctorId").value("doctor1"))
                .andExpect(jsonPath("$[0].patientId").value(patientId))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$[1].id").value("exam2"))
                .andExpect(jsonPath("$[1].doctorId").value("doctor2"))
                .andExpect(jsonPath("$[1].patientId").value(patientId))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].date").value(LocalDate.now().plusDays(1).toString()));
    }

}
