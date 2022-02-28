package in.nmaloth.identifierValidator.services.parameters;

import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.Strategy;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.BitSet;
import java.util.Optional;

@ApplicationScoped
public class ChipServiceVisa extends ChipServiceImpl{



    public void startup(@Observes StartupEvent startupEvent){
        loadChipParams();
    }



    @Override
    public boolean validateIad(String iad, String iadFormat) {
        if(iadFormat.equals("2")) {
            return validateIadFormat2(iad);
        }
        return validateIad(iad);
    }

    @Override
    public String extractCvr(String iad, String iadFormat) {
        if(iadFormat.equals("2")){
            return populateCvrIAD2(iad);
        }
        return populateCvrIAD(iad);    }

    @Override
    public Optional<ServiceResponse> validateCvrAndTvr(String tvr, String iad, String iadFormat, String chipVersion, String strategy) {

        if(validateIad(iad,iadFormat)){

            String cvrString = extractCvr(iad,iadFormat);
            byte[] cvrBytes = stringToHex(cvrString,cvrString.length(),"0");

            BitSet cvr  = createBitSet(cvrBytes);

            return validateCvrAndTvr(cvr,tvr,chipVersion, Strategy.valueOf(strategy));

        } else {
            return Optional.of(ServiceResponse.INVALID_IAD);

        }
    }


    private boolean validateIadFormat2(String iad) {
        if(iad.length() < 16) {
            return false;
        }
        return true;
    }

    private boolean validateIad(String iad) {
        if(iad.length() < 14) {
            return false;
        }
        return true;
    }

    private String populateCvrIAD(String iad) {

        return iad.substring(6,14);
    }

    private String populateCvrIAD2(String iad) {
        return iad.substring(6,16);
    }




}
