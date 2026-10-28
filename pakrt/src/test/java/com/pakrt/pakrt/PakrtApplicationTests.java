package com.pakrt.pakrt;

import com.pakrt.pakrt.repositories.kartukeluarga.KartuKeluargaRepository;
import com.pakrt.pakrt.repositories.warga.WargaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PakrtApplicationTests {

    @Autowired
    private WargaRepository wargaRepository;

    @Autowired
    private KartuKeluargaRepository kartuKeluargaRepository;

    @Autowired
    private com.pakrt.pakrt.services.LogService logService;

    @Test
    void contextLoads() {
        assertNotNull(wargaRepository);
        assertNotNull(kartuKeluargaRepository);
        assertNotNull(logService);

        // Test logging an activity
        logService.log("Test_Action", java.util.Map.of("testKey", "testValue"), "/test/endpoint");
        com.pakrt.pakrt.utils.Logging.LogInfo("Testing LogInfo output!");

        var logs = logService.getLogs(null, "Test_Action", 0, 10);
        assertNotNull(logs);

        // Test inserting Warga with BaseAuditEntity
        com.pakrt.pakrt.models.warga.Warga testWarga = new com.pakrt.pakrt.models.warga.Warga();
        testWarga.setNik("3201010101010099");
        testWarga.setNamaLengkap("Budi Santoso Audit Test");
        testWarga.setNoWhatsapp("081234567890");
        testWarga.setStatusDomisili("WARGA_TETAP");
        com.pakrt.pakrt.models.warga.Warga savedWarga = wargaRepository.save(testWarga);

        assertNotNull(savedWarga.getCreatedAt());
        assertNotNull(savedWarga.getCreatedBy());
        assertNotNull(savedWarga.getUpdatedAt());
        assertNotNull(savedWarga.getUpdatedBy());

        // Test update
        savedWarga.setNamaLengkap("Budi Santoso Audit Test Updated");
        com.pakrt.pakrt.models.warga.Warga updatedWarga = wargaRepository.save(savedWarga);
        assertEquals("Budi Santoso Audit Test Updated", updatedWarga.getNamaLengkap());

        // Cleanup test data
        wargaRepository.delete(updatedWarga);

        System.out.println("Jumlah Warga di DB: " + wargaRepository.count());
        System.out.println("Jumlah Kartu Keluarga di DB: " + kartuKeluargaRepository.count());
    }
}
