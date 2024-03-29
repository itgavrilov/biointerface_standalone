//package ru.gsa.biointerface.services;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import ru.gsa.biointerface.domain.entity.Examination;
//import ru.gsa.biointerface.domain.entity.Icd;
//import ru.gsa.biointerface.domain.entity.PatientRecord;
//import ru.gsa.biointerface.repository.PatientRecordRepository;
//import ru.gsa.biointerface.repository.database.DatabaseHandler;
//import ru.gsa.biointerface.repository.impl.IcdRepositoryImpl;
//import ru.gsa.biointerface.repository.impl.PatientRecordRepositoryImpl;
//
//import javax.persistence.EntityNotFoundException;
//import java.util.*;
//
///**
// * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
// */
//class PatientRecordServiceTest {
//    private static final long id = 1;
//    private static final String secondName = "testSecondName";
//    private static final String firstName = "testFirstName";
//    private static final String middleName = "testMiddleName";
//    private static final Calendar birthday = new GregorianCalendar(2021, Calendar.NOVEMBER, 27);
//    private static final String comment = "testComment";
//    private static final Icd icd = new Icd(
//            "testName",
//            10,
//            comment);
//    private static final Set<Examination> examinations = new TreeSet<>();
//    private static PatientRecordService service;
//    private static PatientRecordRepository repository;
//
//    @BeforeAll
//    static void setUp() throws Exception {
//        DatabaseHandler.constructInstanceForTest();
//        service = PatientRecordService.getInstance();
//        repository = PatientRecordRepositoryImpl.getInstance();
//        IcdRepositoryImpl.getInstance().insert(icd);
//    }
//
//    @AfterAll
//    static void tearDown() throws Exception {
//        IcdRepositoryImpl.getInstance().delete(icd);
//    }
//
//    @Test
//    void getInstance() throws Exception {
//        Assertions.assertSame(service, PatientRecordService.getInstance());
//    }
//
//    @Test
//    void getAll() throws Exception {
//        PatientRecord entity = new PatientRecord(
//                id,
//                secondName,
//                firstName,
//                middleName,
//                birthday,
//                icd,
//                comment);
//        repository.insert(entity);
//        List<PatientRecord> entities = service.findAll();
//        Assertions.assertTrue(entities.contains(entity));
//        repository.delete(entity);
//    }
//
//    @Test
//    void getById() throws Exception {
//        PatientRecord entity = new PatientRecord(
//                id,
//                secondName,
//                firstName,
//                middleName,
//                birthday,
//                icd,
//                comment);
//        repository.insert(entity);
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.findById(-1));
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.findById(0));
//        Assertions.assertThrows(
//                EntityNotFoundException.class,
//                () -> service.findById(entity.getId() + 1));
//
//        PatientRecord entityTest = service.findById(entity.getId());
//        Assertions.assertEquals(entity, entityTest);
//        repository.delete(entity);
//    }
//
//    @Test
//    void save() throws Exception {
//        PatientRecord entity = new PatientRecord(
//                -1,
//                secondName,
//                firstName,
//                middleName,
//                birthday,
//                icd,
//                comment);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(null));
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        entity.setId(0);
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        entity.setId(id);
//        entity.setSecondName(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(entity));
//        entity.setSecondName("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        entity.setSecondName(secondName);
//        entity.setFirstName(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(entity));
//        entity.setFirstName("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        entity.setFirstName(firstName);
//        entity.setPatronymic(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(entity));
//        entity.setPatronymic("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        entity.setPatronymic(middleName);
//        entity.setBirthday(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(entity));
//        entity.setBirthday(birthday);
//        entity.setExaminations(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.save(entity));
//        entity.setExaminations(examinations);
//
//        entity.setIcd(null);
//        Assertions.assertDoesNotThrow(() -> service.save(entity));
//        repository.delete(entity);
//        entity.setIcd(icd);
//        entity.setComment(null);
//        Assertions.assertDoesNotThrow(() -> service.save(entity));
//        repository.delete(entity);
//        entity.setComment("");
//        Assertions.assertDoesNotThrow(() -> service.save(entity));
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.save(entity));
//        PatientRecord entityTest = repository.findById(id);
//        Assertions.assertEquals(entity, entityTest);
//        repository.delete(entity);
//    }
//
//    @Test
//    void delete() throws Exception {
//        PatientRecord entity = new PatientRecord(
//                id,
//                secondName,
//                firstName,
//                middleName,
//                birthday,
//                icd,
//                comment);
//        repository.insert(entity);
//
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.delete(null));
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> {
//                    entity.setId(-1);
//                    service.delete(entity);
//                });
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> {
//                    entity.setId(0);
//                    service.delete(entity);
//                });
//        Assertions.assertThrows(
//                EntityNotFoundException.class,
//                () -> {
//                    entity.setId(id + 1);
//                    service.delete(entity);
//                });
//
//        entity.setId(id);
//        Assertions.assertEquals(entity, repository.findById(id));
//        service.delete(entity);
//        Assertions.assertNull(repository.findById(id));
//    }
//
//    @Test
//    void update() throws Exception {
//        PatientRecord entity = new PatientRecord(
//                id,
//                secondName,
//                firstName,
//                middleName,
//                birthday,
//                icd,
//                comment);
//        repository.insert(entity);
//
//        String secondNameTest = secondName + "Update";
//        String firstNameTest = firstName + "Update";
//        String middleNameTest = middleName + "Update";
//        Calendar birthdayTest = new GregorianCalendar(2020, Calendar.NOVEMBER, 27);
//        String commentTest = comment + "Update";
//        entity.setSecondName(secondNameTest);
//        entity.setFirstName(firstNameTest);
//        entity.setPatronymic(middleNameTest);
//        entity.setBirthday(birthdayTest);
//        entity.setComment(commentTest);
//
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(null));
//        entity.setId(-1);
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.update(entity));
//        entity.setId(0);
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.update(entity));
//        entity.setId(id);
//        entity.setSecondName(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(entity));
//        entity.setSecondName("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.update(entity));
//        entity.setSecondName(secondNameTest);
//        entity.setFirstName(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(entity));
//        entity.setFirstName("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.update(entity));
//        entity.setFirstName(firstNameTest);
//        entity.setPatronymic(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(entity));
//        entity.setPatronymic("");
//        Assertions.assertThrows(
//                IllegalArgumentException.class,
//                () -> service.update(entity));
//        entity.setPatronymic(middleNameTest);
//        entity.setBirthday(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(entity));
//        entity.setBirthday(birthdayTest);
//        entity.setExaminations(null);
//        Assertions.assertThrows(
//                NullPointerException.class,
//                () -> service.update(entity));
//        entity.setExaminations(examinations);
//        entity.setIcd(null);
//        Assertions.assertDoesNotThrow(() -> service.update(entity));
//        entity.setIcd(icd);
//        entity.setComment(null);
//        Assertions.assertDoesNotThrow(() -> service.update(entity));
//        entity.setComment("");
//        Assertions.assertDoesNotThrow(() -> service.update(entity));
//        entity.setComment(commentTest);
//        repository.update(entity);
//
//        PatientRecord entityTest = repository.findById(id);
//
//        Assertions.assertEquals(secondNameTest, entityTest.getSecondName());
//        Assertions.assertEquals(firstNameTest, entityTest.getFirstName());
//        Assertions.assertEquals(middleNameTest, entityTest.getPatronymic());
//        Assertions.assertEquals(birthdayTest, entityTest.getBirthday());
//        Assertions.assertEquals(icd, entityTest.getIcd());
//        Assertions.assertEquals(commentTest, entityTest.getComment());
//
//        repository.delete(entity);
//    }
//}