package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.identifierValidator.model.ChipParams;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.Strategy;

import java.util.BitSet;
import java.util.Optional;

public interface ChipService {

    boolean validateIad(String iad, String iadFormat);
    String extractCvr(String iad,String iadFormat);

    void loadChipParams();

    ChipParams fetchChipParams(String chipVersion, Strategy strategy);

    Optional<ServiceResponse> validateCvrAndTvr(String tvr, String iad, String iadFormat,String chipVersion, String strategy);

}
