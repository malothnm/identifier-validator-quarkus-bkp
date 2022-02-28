package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ProductCriteria;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import in.nmaloth.identifierValidator.model.entity.product.ProductAuthCriteria;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.account.BalanceTypes;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.constants.products.*;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class ParameterServiceImplTest {

    @Inject
    ParameterService parameterService;

    @BeforeEach
    void cleanUp() {

        Uni<List<ProductAuthCriteria>> productListUni = ProductAuthCriteria.listAll();

        productListUni.await().indefinitely()
                .forEach(productAuthCriteria -> {
                    productAuthCriteria.delete();

                });
    }



    @Test
    void blockingCountryCode_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"IND");

        assertEquals(ServiceResponse.BLOCKED_COUNTRY,serviceResponseOptional.get());


    }

    @Test
    void blockingCountryCode_Exclude_Pass() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"USD");

        assertTrue(serviceResponseOptional.isEmpty());


    }


    @Test
    void blockingCountryCode_Include() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"IND");


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingCountryCode_Include_fail() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"USD");

        assertEquals(ServiceResponse.BLOCKED_COUNTRY,serviceResponseOptional.get());



    }

    @Test
    void blockingCountryCode_NA() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"IND");


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingCountryCode_NA_pass() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCountryCode(productCriteria,"USD");

        assertTrue(serviceResponseOptional.isEmpty());



    }

    @Test
    void blockingCurrencyCode_Exclude() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"INR");

        assertEquals(ServiceResponse.BLOCKED_CURRENCY, serviceResponseOptional.get());


    }

    @Test
    void blockingCurrencyCode_Exclude_Pass() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"USD");

        assertTrue(serviceResponseOptional.isEmpty());


    }


    @Test
    void blockingCurrencyCode_Include() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"INR");


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingCurrencyCode_Include_fail() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"USD");

        assertEquals(ServiceResponse.BLOCKED_CURRENCY,serviceResponseOptional.get());



    }

    @Test
    void blockingCurrency_NA() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"INR");


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingCcurrencyCode_NA_pass() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingCurrencyCode(productCriteria,"USD");

        assertTrue(serviceResponseOptional.isEmpty());



    }

    @Test
    void blockingMCC_Exclude_InRangeMid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5043);


        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Exclude_Not_InRange() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5074);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Upper() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5045);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Lower() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5042);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Exclude_InRangeMid_MId() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4043);


        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Exclude_Not_InRange_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4074);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Upper_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4045);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Lower_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4042);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }


    @Test
    void blockingMCC_Exclude_InRangeMid_Outside() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3041);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_Not_InRange_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3074);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Upper_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3045);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Exclude_RangeLimit_Lower_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3042);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }


    @Test
    void blockingMCC_Include_InRangeMid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5043);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Include_Not_InRange() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5074);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Upper() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5045);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Lower() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,5042);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Include_InRangeMid_MId() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4043);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_Include_Not_InRange_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4074);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Upper_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4045);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Lower_Mid() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,4042);

        assertTrue(serviceResponseOptional.isEmpty());


    }


    @Test
    void blockingMCC_Include_InRangeMid_Outside() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3041);


        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_Not_InRange_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3074);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Upper_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3045);

        assertEquals(ServiceResponse.BLOCKED_MCC,serviceResponseOptional.get());


    }

    @Test
    void blockingMCC_Include_RangeLimit_Lower_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3042);

        assertTrue(serviceResponseOptional.isEmpty());


    }


    @Test
    void blockingMCC_NA_RangeLimit_Upper_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3045);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingMCC_NA_RangeLimit_Lower_First() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingMCC(productCriteria,3042);

        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.GAMBLING,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.GAMBLING,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingPurchaseTypes_Exclude_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.AIRLINE,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.LODGING,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.LODGING,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingPurchaseTypes_Exclude_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTRICTED_ADULTS,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.DOMESTIC);


        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());


    }

    @Test
    void blockingPurchaseTypes_Exclude_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingPurchaseTypes_Exclude_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTRICTED_ADULTS,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingPurchaseTypes_Include_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.GAMBLING,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingPurchaseTypes_Include_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.GAMBLING,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingPurchaseTypes_Include_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.AIRLINE,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingPurchaseTypes_Include_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.LODGING,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingPurchaseTypes_Include_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.LODGING,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingPurchaseTypes_Include_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTRICTED_ADULTS,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingPurchaseTypes_Include_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingPurchaseTypes_Include_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingPurchaseTypes_Include_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTRICTED_ADULTS,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_PURCHASE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingPurchaseTypes_NA_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingPurchaseTypes_NA_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTAURANT,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingPurchaseTypes_NA_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingPurchaseTypes(productCriteria,PurchaseTypes.RESTRICTED_ADULTS,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingLimitTypes_Exclude_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.OTC},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingLimitTypes_Exclude_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.OTC},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingLimitTypes_Exclude_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.RETAIL},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingLimitType_Exclude_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.QUASI_CASH},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingLimitTypes_Exclude_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new  LimitType[]{LimitType.QUASI_CASH},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingLimitTypes_Exclude_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.RETAIL},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingLimitTypes_Exclude_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[] {LimitType.ATM},International.DOMESTIC);


        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());


    }

    @Test
    void blockingLimitTypes_Exclude_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.ATM},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingLimitTypes_Exclude_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria, new LimitType[]{LimitType.RETAIL},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }



    @Test
    void blockingLimitTypes_Include_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.OTC},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingLimitTypes_Include_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.OTC},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingLimitTypes_Include_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.RETAIL},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingLimitType_Include_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.QUASI_CASH},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingLimitTypes_Include_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new  LimitType[]{LimitType.QUASI_CASH},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingLimitTypes_Include_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.RETAIL},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingLimitTypes_Include_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[] {LimitType.ATM},International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingLimitTypes_Include_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.ATM},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingLimitTypes_Include_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria, new LimitType[]{LimitType.RETAIL},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_LIMIT_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingLimitTypes_NA_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria,new LimitType[]{LimitType.ATM},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingLimitTypes_NA_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingLimitTypes(productCriteria, new LimitType[]{LimitType.RETAIL},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }



    @Test
    void blockingBalanceTypes_Exclude_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_BALANCE},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingBalanceTypes_Exclude_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_BALANCE},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingBalanceTypes_Exclude_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingBalanceType_Exclude_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CASH_BALANCE},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingBalanceTypes_Exclude_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new  BalanceTypes[]{BalanceTypes.CASH_BALANCE},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingBalanceTypes_Exclude_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingBalanceTypes_Exclude_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[] {BalanceTypes.INSTALLMENT_CASH},International.DOMESTIC);


        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());


    }

    @Test
    void blockingBalanceTypes_Exclude_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_CASH},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingBalanceTypeTypes_Exclude_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria, new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }



    @Test
    void blockingBalanceTypes_Include_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_BALANCE},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingBalanceTypes_Include_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_BALANCE},International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingBalanceTypes_Include_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingBalanceType_Include_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CASH_BALANCE},International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingBalanceTypes_Include_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new  BalanceTypes[]{BalanceTypes.CASH_BALANCE},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingBalanceTypes_Include_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingBalanceTypes_Include_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[] {BalanceTypes.INSTALLMENT_CASH},International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingBalanceTypes_Include_ValuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_CASH},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingBalanceTypeTypes_Include_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria, new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_BALANCE_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingBalanceTypes_NA_ValuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[]{BalanceTypes.INSTALLMENT_CASH},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingBalanceTypeTypes_NA_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria, new BalanceTypes[]{BalanceTypes.CURRENT_BALANCE},International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingBalanceTypes_NA_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingBalanceTypes(productCriteria,new BalanceTypes[] {BalanceTypes.INSTALLMENT_CASH},International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }



    @Test
    void blockingTransactionTypes_Exclude_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.CASH,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTransactionType_Exclude_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.CASH,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTransactionType_Exclude_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.TOKEN_ACTIVATION_REQUEST,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTransactionType_Exclude_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.BILL_PAYMENT,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTransactionTypes_Exclude_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.BILL_PAYMENT,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTransactionTypes_Exclude_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.ACCOUNT_FUND_TRANSACTION,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTransactionTypes_Exclude_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.DOMESTIC);


        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());


    }

    @Test
    void blockingTransactionTypes_Exclude_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTransactionTypes_Exclude_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria, TransactionType.PREPAID_ACTIVATION,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }



    @Test
    void blockingTransactionTypes_Include_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.CASH,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTransactionType_Include_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.CASH,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTransactionType_Include_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.TOKEN_ACTIVATION_REQUEST,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTransactionType_Include_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.BILL_PAYMENT,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTransactionTypes_Include_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.BILL_PAYMENT,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTransactionTypes_Include_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.ACCOUNT_FUND_TRANSACTION,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTransactionTypes_Include_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingTransactionTypes_IncludePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTransactionTypes_Include_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria, TransactionType.PREPAID_ACTIVATION,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TRANSACTION_TYPE,serviceResponseOptional.get());

    }



    @Test
    void blockingTransactionTypes_NA_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.ACCOUNT_FUND_TRANSACTION,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTransactionTypes_NA_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingTransactionTypes_NA_Present_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria,TransactionType.OCT,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTransactionTypes_NA_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTransactionTypes(productCriteria, TransactionType.PREPAID_ACTIVATION,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }





    @Test
    void blockingTerminalTypes_Exclude_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.MOTO,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTerminalType_Exclude_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.MOTO,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTerminalType_Exclude_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ATM,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTerminalType_Exclude_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.HOME_TERMINALS,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTerminalTypes_Exclude_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.HOME_TERMINALS,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTerminalTypes_Exclude_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ATM,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTerminalTypes_Exclude_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.DOMESTIC);


        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());


    }

    @Test
    void blockingTerminalTypes_Exclude_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTerminalTypes_Exclude_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.EXCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria, TerminalType.ATM,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTerminalTypes_Include_valuePresent_International_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.MOTO,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTerminalType_Include_valuePresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.MOTO,International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockingTerminalType_Include_valueNotPresent_Domestic_both() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ATM,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTerminalType_Include_valuePresent_Domestic_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.HOME_TERMINALS,International.DOMESTIC);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTerminalTypes_Include_valuePresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.HOME_TERMINALS,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTerminalTypes_Include_valueNotPresent_International_InternationalApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ATM,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }

    @Test
    void blockingTerminalTypes_Include_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingTerminalTypes_Include_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTerminalTypes_Include_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria, TerminalType.ATM,International.INTERNATIONAL);

        assertEquals(ServiceResponse.BLOCKED_TERMINAL_TYPE,serviceResponseOptional.get());

    }


    @Test
    void blockingTerminalTypes_NA_valuePresent_Domestic_IDomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.DOMESTIC);


        assertTrue(serviceResponseOptional.isEmpty());


    }

    @Test
    void blockingTerminalTypes_NA_valuePresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria,TerminalType.ELECTRONIC_CASH_REGISTER,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockingTerminalTypes_NA_valueNotPresent_International_DomesticApplied() {


        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.NOT_APPLICABLE);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockingTerminalTypes(productCriteria, TerminalType.ATM,International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockInternational_domestic() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInternational(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInternational(productCriteria, International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockInternational_international() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInternational(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInternational(productCriteria, International.INTERNATIONAL);

        assertEquals(ServiceResponse.INTERNATIONAL,serviceResponseOptional.get());

    }

    @Test
    void blockInternational_domestic_notSet() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInternational(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInternational(productCriteria, International.DOMESTIC);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockInternational_international_not_set() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInternational(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInternational(productCriteria, International.INTERNATIONAL);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockInstalment_noInstallment() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInternational(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInstallment(productCriteria, InstallmentType.NO_INSTALLMENT_TYPE);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockInstallment_installment() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInstallments(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInstallment(productCriteria, InstallmentType.INSTALLMENT_TYPE);

        assertEquals(ServiceResponse.INSTALLMENT,serviceResponseOptional.get());

    }

    @Test
    void blockInstallment_no_installment_notSet() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInstallments(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInstallment(productCriteria, InstallmentType.NO_INSTALLMENT_TYPE);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockInstallment_installment_not_set() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockInstallments(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockInstallment(productCriteria, InstallmentType.INSTALLMENT_TYPE);

        assertTrue(serviceResponseOptional.isEmpty());

    }


    @Test
    void blockCashBack_no_cashBack() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockCashBack(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockCashBack(productCriteria, CashBack.NO_CASH_BACK);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockCashBack_cashback() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockCashBack(true);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockCashBack(productCriteria, CashBack.CASH_BACK_PRESENT);

        assertEquals(ServiceResponse.CASH_BACK,serviceResponseOptional.get());

    }

    @Test
    void blockCashBack_no_cashback_notSet() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockCashBack(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockCashBack(productCriteria, CashBack.NO_CASH_BACK);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    @Test
    void blockCashBack_cashBack_not_set() {

        ProductAuthCriteria productAuthCriteria = createProductAuthCriteria(1, 201, 100, IncludeExclude.INCLUDE);
        productAuthCriteria.setBlockCashBack(false);
        productAuthCriteria.persist().await().indefinitely();
        parameterService.loadAllAuthProductCriteria();

        ProductCriteria productCriteria = parameterService.findCriteriaRecord(1,201,100).get();

        Optional<ServiceResponse> serviceResponseOptional = parameterService.blockCashBack(productCriteria, CashBack.CASH_BACK_PRESENT);

        assertTrue(serviceResponseOptional.isEmpty());

    }

    private ProductAuthCriteria createProductAuthCriteria(Integer org, Integer product,
                                                          Integer criteria, IncludeExclude includeExclude) {

        List<String> countriesList = new ArrayList<>();
        countriesList.add("IND");
        countriesList.add("INA");
        countriesList.add("PAK");
        List<String> currenciesList = new ArrayList<>();
        currenciesList.add("INR");
        currenciesList.add("IDR");
        currenciesList.add("PKR");
        List<MCCRange> mccRangeList = new ArrayList<>();
        mccRangeList.add(MCCRange.builder().mccStart(5042).mccEnd(5045).build());
        mccRangeList.add(MCCRange.builder().mccStart(4042).mccEnd(4045).build());
        mccRangeList.add(MCCRange.builder().mccStart(3042).mccEnd(3045).build());


        List<BlockingLimitType> limitTypeList = new ArrayList<>();
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.OTC).internationalApplied(InternationalApplied.BOTH).build());
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.QUASI_CASH).internationalApplied(InternationalApplied.INTERNATIONAL).build());
        limitTypeList.add(BlockingLimitType.builder().limitType(LimitType.ATM).internationalApplied(InternationalApplied.DOMESTIC).build());

        List<BlockingBalanceType> blockingBalanceTypeList = new ArrayList<>();
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .BOTH).balanceTypes(BalanceTypes.INSTALLMENT_BALANCE).build());
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .INTERNATIONAL).balanceTypes(BalanceTypes.CASH_BALANCE).build());
        blockingBalanceTypeList.add(BlockingBalanceType.builder().internationalApplied(InternationalApplied
                .DOMESTIC).balanceTypes(BalanceTypes.INSTALLMENT_CASH).build());

        List<BlockingTransactionType> blockingTransactionTypeList = new ArrayList<>();
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.BOTH)
                .transactionType(TransactionType.CASH).build());
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.INTERNATIONAL)
                .transactionType(TransactionType.BILL_PAYMENT).build());
        blockingTransactionTypeList.add(BlockingTransactionType.builder().internationalApplied(InternationalApplied.DOMESTIC)
                .transactionType(TransactionType.OCT).build());


        List<BlockingTerminalType> terminalTypeList = new ArrayList<>();
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.MOTO).internationalApplied(InternationalApplied.BOTH).build());
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.HOME_TERMINALS).internationalApplied(InternationalApplied.INTERNATIONAL).build());
        terminalTypeList.add(BlockingTerminalType.builder().terminalType(TerminalType.ELECTRONIC_CASH_REGISTER).internationalApplied(InternationalApplied.DOMESTIC).build());

        List<BlockingPurchaseType> purchaseTypesList = new ArrayList<>();
        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.GAMBLING).internationalApplied(InternationalApplied.BOTH)
                .build());

        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.LODGING).internationalApplied(InternationalApplied.INTERNATIONAL)
                .build());

        purchaseTypesList.add(BlockingPurchaseType.builder()
                .purchaseTypes(PurchaseTypes.RESTAURANT).internationalApplied(InternationalApplied.DOMESTIC)
                .build());


        ProductAuthCriteria.ProductAuthCriteriaBuilder builder =
                ProductAuthCriteria.builder()
                        .org(org)
                        .product(product)
                        .criteria(criteria)
                        .strategy(Strategy.CHAMPION);

        if (includeExclude.equals(IncludeExclude.EXCLUDE)) {

            builder
                    .blockingCountries(IncludeExclude.EXCLUDE)
                    .countryCodesBlocked(countriesList)
                    .blockingCurrency(IncludeExclude.EXCLUDE)
                    .currencyCodesBlocked(currenciesList)
                    .blockingStates(IncludeExclude.EXCLUDE)
                    .stateCodesBlocked(new ArrayList<>())
                    .blockingMCC(IncludeExclude.EXCLUDE)
                    .mccBlocked(mccRangeList)
                    .blockingLimitTypes(IncludeExclude.EXCLUDE)
                    .limitTypesBlocked(limitTypeList)
                    .blockingPurchaseTypes(IncludeExclude.EXCLUDE)
                    .purchaseTypesBlocked(purchaseTypesList)
                    .blockingBalanceTypes(IncludeExclude.EXCLUDE)
                    .balanceTypesBlocked(blockingBalanceTypeList)
                    .blockingTransactionTypes(IncludeExclude.EXCLUDE)
                    .transactionTypesBlocked(blockingTransactionTypeList)
                    .blockTerminalTypes(IncludeExclude.EXCLUDE)
                    .terminalTypesBlocked(terminalTypeList)
            ;
        } else if (includeExclude.equals(IncludeExclude.INCLUDE)) {

            builder
                    .blockingCountries(IncludeExclude.INCLUDE)
                    .countryCodesBlocked(countriesList)
                    .blockingCurrency(IncludeExclude.INCLUDE)
                    .currencyCodesBlocked(currenciesList)
                    .blockingStates(IncludeExclude.INCLUDE)
                    .stateCodesBlocked(new ArrayList<>())
                    .blockingMCC(IncludeExclude.INCLUDE)
                    .mccBlocked(mccRangeList)
                    .blockingLimitTypes(IncludeExclude.INCLUDE)
                    .limitTypesBlocked(limitTypeList)
                    .blockingPurchaseTypes(IncludeExclude.INCLUDE)
                    .purchaseTypesBlocked(purchaseTypesList)
                    .blockingBalanceTypes(IncludeExclude.INCLUDE)
                    .balanceTypesBlocked(blockingBalanceTypeList)
                    .blockingTransactionTypes(IncludeExclude.INCLUDE)
                    .transactionTypesBlocked(blockingTransactionTypeList)
                    .blockTerminalTypes(IncludeExclude.INCLUDE)
                    .terminalTypesBlocked(terminalTypeList)
            ;
        } else {

            builder
                    .blockingCountries(IncludeExclude.NOT_APPLICABLE)
                    .blockingCurrency(IncludeExclude.NOT_APPLICABLE)
                    .blockingStates(IncludeExclude.NOT_APPLICABLE)
                    .blockingMCC(IncludeExclude.NOT_APPLICABLE)
                    .blockingLimitTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingPurchaseTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingBalanceTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockingTransactionTypes(IncludeExclude.NOT_APPLICABLE)
                    .blockTerminalTypes(IncludeExclude.NOT_APPLICABLE)
            ;
        }

        return builder.build();


    }
}