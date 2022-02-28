package in.nmaloth.identifierValidator.services.cards;

import in.nmaloth.identifierValidator.services.card.CardsService;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.card.CardStatus;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.testResource.GRPCWireResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(GRPCWireResource.class)
class CardsServiceImplTest {

    @Inject
    CardsService cardsService;

    @Test
    void validateCardStatus() {

        assertAll(
                ()-> assertTrue(cardsService.validateCardStatus(CardStatus.ACTIVE).isEmpty()),
                ()-> assertEquals(ServiceResponse.STATUS,cardsService.validateCardStatus(CardStatus.FRAUD).get())
        );
    }

    @Test
    void validateCardBlock() {

        assertAll(
                ()-> assertTrue(cardsService.validateCardBlock(BlockType.APPROVE).isEmpty()),
                ()-> assertTrue(cardsService.validateCardBlock(BlockType.VIP_ALWAYS_APPROVE).isEmpty()),
                ()-> assertEquals(ServiceResponse.TEMP_BLK,cardsService.validateCardBlock(BlockType.BLOCK_TEMP).get()),
                ()-> assertEquals(ServiceResponse.SUSPECT_FRAUD,cardsService.validateCardBlock(BlockType.BLOCK_SUSPECTED_FRAUD).get()),
                ()-> assertEquals(ServiceResponse.PICK_UP,cardsService.validateCardBlock(BlockType.BLOCK_PICKUP).get()),
                ()-> assertEquals(ServiceResponse.FRAUD,cardsService.validateCardBlock(BlockType.BLOCK_FRAUD).get()),
                ()-> assertEquals(ServiceResponse.BLK,cardsService.validateCardBlock(BlockType.BLOCK_DECLINE).get())


        );
    }

    @Test
    void checkForBlockTransactionType_both() {

       Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTransactionType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.BOTH.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);


       assertAll(
               ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional1.get()),
               ()-> assertTrue(serviceResponseOptional2.isEmpty()),
               ()-> assertTrue(serviceResponseOptional3.isEmpty()),
               ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional4.get()),
               ()-> assertTrue(serviceResponseOptional5.isEmpty()),
               ()-> assertTrue(serviceResponseOptional6.isEmpty()),
               ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional7.get())

               );
    }


    @Test
    void checkForBlockTransactionType_Domestic() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTransactionType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);


        assertAll(
                ()-> assertTrue(serviceResponseOptional1.isEmpty()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional2.get()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockTransactionType_International() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTransactionType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.CASH.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTransactionType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTransactionType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TransactionType.CASH.getTransactionType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TransactionType.BILL_PAYMENT.getTransactionType(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertTrue(serviceResponseOptional4.isEmpty()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional5.get()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.TRANSACTION_TYPE, serviceResponseOptional7.get())

        );
    }

    @Test
    void checkForBlockTerminalType_both() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTerminalType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.UNATTENDED_CARDHOLDER_ACTIVATED_NO_AUTH.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.BOTH.getBlockInternational()),
                TerminalType.UNATTENDED_CARDHOLDER_ACTIVATED_NO_AUTH.getTerminalType(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockTerminalType_Domestic() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTerminalType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.UNATTENDED_CARDHOLDER_ACTIVATED_NO_AUTH.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                TerminalType.UNATTENDED_CARDHOLDER_ACTIVATED_NO_AUTH.getTerminalType(), International.DOMESTIC);


        assertAll(
                ()-> assertTrue(serviceResponseOptional1.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional2.get()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockTerminalType_International() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockTerminalType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.HOME_TERMINALS.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockTerminalType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.ELECTRONIC_CASH_REGISTER.getTerminalType(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockTerminalType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(TerminalType.HOME_TERMINALS.getTerminalType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                TerminalType.ELECTRONIC_CASH_REGISTER.getTerminalType(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertTrue(serviceResponseOptional4.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional5.get()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.TERMINAL_TYPE, serviceResponseOptional7.get())

        );
    }

    @Test
    void checkForBlockPurchaseType_both() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockPurchaseType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.BOTH.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockPurchaseType_Domestic() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockPurchaseType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.DOMESTIC.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);


        assertAll(
                ()-> assertTrue(serviceResponseOptional1.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional2.get()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockPurchaseType_International() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockPurchaseType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.RESTAURANT.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockPurchaseType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockPurchaseType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(PurchaseTypes.RESTAURANT.getPurchaseTypes(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                PurchaseTypes.LODGING.getPurchaseTypes(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertTrue(serviceResponseOptional4.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional5.get()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.PURCHASE_TYPE, serviceResponseOptional7.get())

        );
    }

    @Test
    void checkForBlockEntryMode_both() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockEntryMode(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.MAG.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.BOTH.getBlockInternational()),
                EntryMode.MAG.getEntryMode(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockEntryMode_Domestic() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockEntryMode(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.MAG.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.DOMESTIC.getBlockInternational()),
                EntryMode.MAG.getEntryMode(), International.DOMESTIC);


        assertAll(
                ()-> assertTrue(serviceResponseOptional1.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional2.get()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockEntryMode_International() {

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockEntryMode(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
        EntryMode.ICC.getEntryMode(), International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.ICC.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockEntryMode(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.MOTO.getEntryMode(), International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockEntryMode(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(EntryMode.ICC.getEntryMode(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                EntryMode.MOTO.getEntryMode(), International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertTrue(serviceResponseOptional4.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional5.get()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.ENTRY_MODE, serviceResponseOptional7.get())

        );
    }



    @Test
    void checkForBlockLimit_both() {

        List<String> limitList1 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.RETAIL.getLimitType(),LimitType.OTC.getLimitType());

        List<String> limitList2 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.OTC.getLimitType());

        List<String> limitList3 = List.of(LimitType.RETAIL.getLimitType());

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList3, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockLimitType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList1, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList3, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList2, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.BOTH.getBlockInternational()),
                limitList2, International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockLimit_Domestic() {


        List<String> limitList1 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.RETAIL.getLimitType(),LimitType.OTC.getLimitType());

        List<String> limitList2 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.OTC.getLimitType());
        List<String> limitList3 = List.of(LimitType.RETAIL.getLimitType());




        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockLimitType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList1, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList3, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList2, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.DOMESTIC.getBlockInternational()),
                limitList2, International.DOMESTIC);


        assertAll(
                ()-> assertTrue(serviceResponseOptional1.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional2.get()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional4.get()),
                ()-> assertTrue(serviceResponseOptional5.isEmpty()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional7.get())

        );
    }


    @Test
    void checkForBlockLimit_International() {


        List<String> limitList1 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.RETAIL.getLimitType(),LimitType.OTC.getLimitType());

        List<String> limitList2 = List.of(LimitType.NO_SPECIFIC.getLimitType(),LimitType.OTC.getLimitType());

        List<String> limitList3 = List.of(LimitType.RETAIL.getLimitType());

        Optional<ServiceResponse> serviceResponseOptional1 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional2 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList3, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional3 =  cardsService.checkForBlockLimitType(IncludeExclude.NOT_APPLICABLE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList1, International.INTERNATIONAL);

        Optional<ServiceResponse> serviceResponseOptional4 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList1, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional5 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList3, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional6 =  cardsService.checkForBlockLimitType(IncludeExclude.EXCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList2, International.DOMESTIC);

        Optional<ServiceResponse> serviceResponseOptional7 =  cardsService.checkForBlockLimitType(IncludeExclude.INCLUDE.getIncludeExclude(),
                Map.of(LimitType.RETAIL.getLimitType(),InternationalApplied.INTERNATIONAL.getBlockInternational()),
                limitList2, International.DOMESTIC);


        assertAll(
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional1.get()),
                ()-> assertTrue(serviceResponseOptional2.isEmpty()),
                ()-> assertTrue(serviceResponseOptional3.isEmpty()),
                ()-> assertTrue(serviceResponseOptional4.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional5.get()),
                ()-> assertTrue(serviceResponseOptional6.isEmpty()),
                ()-> assertEquals(ServiceResponse.CARD_LIMIT, serviceResponseOptional7.get())

        );
    }

    @Test
    void checkForBlockCashBack() {

        assertAll(
                ()-> assertTrue(cardsService.checkForBlockCashBack(false, CashBack.NO_CASH_BACK.getCashBack()).isEmpty()),
                ()-> assertTrue(cardsService.checkForBlockCashBack(false, CashBack.CASH_BACK_PRESENT.getCashBack()).isEmpty()),
                ()-> assertTrue(cardsService.checkForBlockCashBack(true, CashBack.NO_CASH_BACK.getCashBack()).isEmpty()),
                ()-> assertEquals(ServiceResponse.CASH_BACK,
                        cardsService.checkForBlockCashBack(true, CashBack.CASH_BACK_PRESENT.getCashBack()).get())
                );

    }

    @Test
    void checkForInstallment() {
        assertAll(
                ()-> assertTrue(cardsService.checkForInstallment(false, InstallmentType.NO_INSTALLMENT_TYPE.getInstallmentType()).isEmpty()),
                ()-> assertTrue(cardsService.checkForInstallment(false, InstallmentType.INSTALLMENT_TYPE.getInstallmentType()).isEmpty()),
                ()-> assertTrue(cardsService.checkForInstallment(true, InstallmentType.NO_INSTALLMENT_TYPE.getInstallmentType()).isEmpty()),
                ()-> assertEquals(ServiceResponse.INSTALLMENT,
                        cardsService.checkForInstallment(true, InstallmentType.INSTALLMENT_TYPE.getInstallmentType()).get())
        );

    }

    @Test
    void checkForInternational() {

        assertAll(
                ()-> assertTrue(cardsService.checkForInternational(false, International.DOMESTIC).isEmpty()),
                ()-> assertTrue(cardsService.checkForInternational(false, International.INTERNATIONAL).isEmpty()),
                ()-> assertTrue(cardsService.checkForInternational(true, International.DOMESTIC).isEmpty()),
                ()-> assertEquals(ServiceResponse.INTERNATIONAL,
                        cardsService.checkForInternational(true, International.INTERNATIONAL).get())
        );

    }

    @Test
    void validateCardLimits() {
    }
}