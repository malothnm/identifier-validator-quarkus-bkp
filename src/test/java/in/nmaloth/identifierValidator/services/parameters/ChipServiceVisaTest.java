package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ChipParams;
import in.nmaloth.identifierValidator.model.entity.global.ChipParameters;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.Strategy;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class ChipServiceVisaTest {

    private Logger logger = LoggerFactory.getLogger(ChipServiceVisaTest.class);
    @Inject
    ChipService chipService;


    @BeforeEach
    void setup(){


        Uni<List<ChipParameters>> chipParametersListUni = ChipParameters.listAll();

                chipParametersListUni.await().indefinitely()
                        .forEach(chipParameters -> chipParameters.delete().await().indefinitely());

        byte[] cvr = new byte[]{0x00,(byte) 0x0A,0x23,0x25,0x12};
        byte[] tvr = new byte[]{0x12,0x23,0x34,0x45,0x56};
        ChipParameters chipParameters = createChipParameter("TestChip1",cvr,tvr,Strategy.CHAMPION);
        chipParameters.persist().await().indefinitely();

        byte[] cvr1 = new byte[]{0x00,(byte) 0x0A,0x23,0x25};
        chipParameters = createChipParameter("TestChip2",cvr1,tvr,Strategy.CHAMPION);
        chipParameters.persist().await().indefinitely();

        chipService.loadChipParams();
                ;
//        chipParameterRepository.findAll().list()
//                .await().indefinitely()
//                .forEach(chipParameters -> chipParameterRepository.deleteByChipVersionAndStrategy(chipParameters.getChipVersion(), Strategy.valueOf(chipParameters.getStrategy())).await().indefinitely());
    }


    @Test
    void loadChipParams(){


        byte[] cvr = new byte[]{0x00,(byte) 0xF5,0x23,0x25};
        byte[] tvr = new byte[]{0x12,0x23,0x34,0x45,0x56};
        ChipParameters chipParameters = createChipParameter("TestChip",cvr,tvr,Strategy.CHAMPION);
        chipParameters.persist().await().indefinitely();

        chipService.loadChipParams();

        ChipParams chipParams = chipService.fetchChipParams("TestChip",Strategy.CHAMPION);

        assertAll(
                ()-> assertNotNull(chipParams),
                ()-> assertTrue(chipParams.getBitSetCvr().get(8)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(9)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(10)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(11)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(13)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(15)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(18)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(22)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(23)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(26)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(29)),
                ()-> assertTrue(chipParams.getBitSetCvr().get(31)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(3)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(6)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(10)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(14)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(15)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(18)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(19)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(21)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(25)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(29)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(31)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(33)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(35)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(37)),
                ()-> assertTrue(chipParams.getBitSetTvr().get(38)),
                ()-> assertTrue(chipParams.isDecideBasedOnCvr()),
                ()-> assertTrue(chipParams.isDecideBasedOnTvr())


        );

    }

    @Test
    void validateCvrAndTvr() {
        Optional<ServiceResponse> serviceResponseOptional = chipService.validateCvrAndTvr("000000000000", "06010AF0000000", "1","TestChip2",Strategy.CHAMPION.getStrategy());

        assertTrue(serviceResponseOptional.isEmpty());
    }


    @Test
    void validateCvrAndTvrInvalid() {
        Optional<ServiceResponse> serviceResponseOptional = chipService.validateCvrAndTvr("100000000000", "06010AF0000000", "1","TestChip2",Strategy.CHAMPION.getStrategy());


        assertEquals(ServiceResponse.BLOCKED_TVR,serviceResponseOptional.get());
    }

    @Test
    void validateInvalidCvrAndTvr() {
        Optional<ServiceResponse> serviceResponseOptional = chipService.validateCvrAndTvr("000000000000", "06010AF0080000", "1","TestChip2",Strategy.CHAMPION.getStrategy());


        assertEquals(ServiceResponse.BLOCKED_CVR,serviceResponseOptional.get());
    }

    @Test
    void validateInvalidIAD() {
        Optional<ServiceResponse> serviceResponseOptional = chipService.validateCvrAndTvr("000000000000", "06010AF0080000", "2","TestChip2",Strategy.CHAMPION.getStrategy());


        assertEquals(ServiceResponse.INVALID_IAD,serviceResponseOptional.get());
    }

    private ChipParameters createChipParameter(String chipVersion, byte[] cvr, byte[] tvr, Strategy strategy){


        return ChipParameters.builder()
                .chipVersion(chipVersion)
                .strategy(strategy)
                .decideOnCvr(true)
                .cvrApproveDecline(cvr)
                .decideOnTvr(true)
                .tvrApproveDecline(tvr)
                .build();
    }
}