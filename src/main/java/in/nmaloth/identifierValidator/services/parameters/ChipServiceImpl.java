package in.nmaloth.identifierValidator.services.parameters;

import com.google.common.io.BaseEncoding;
import in.nmaloth.identifierValidator.model.ChipKey;
import in.nmaloth.identifierValidator.model.ChipParams;
import in.nmaloth.identifierValidator.model.entity.global.ChipParameters;
import in.nmaloth.payments.constants.ServiceResponse;
import in.nmaloth.payments.constants.Strategy;
import io.smallrye.mutiny.Uni;

import java.util.*;

public abstract class ChipServiceImpl implements ChipService{


    private final Map<ChipKey, ChipParams> chipParamsMap = new HashMap<>();


    @Override
    public void loadChipParams(){
        Uni<List<ChipParameters>> uniChip =  ChipParameters.listAll();
        uniChip.await().indefinitely()
                .forEach(chipParameters -> chipParamsMap.put(ChipKey.builder().chipVersion(chipParameters
                                .getChipVersion()).strategy(Strategy.valueOf(chipParameters.getStrategy())).build(),
                        buildChipParams(chipParameters)))
                ;

    }

    private ChipParams buildChipParams(ChipParameters chipParameters) {

        ChipParams.ChipParamsBuilder builder = ChipParams.builder()
                .decideBasedOnCvr(chipParameters.isDecideOnCvr())
                .decideBasedOnTvr(chipParameters.isDecideOnTvr())
                ;

        if(chipParameters.getCvrApproveDecline() != null){
            builder.bitSetCvr(createBitSet(chipParameters.getCvrApproveDecline()));
        }

        if(chipParameters.getTvrApproveDecline() != null){
            builder.bitSetTvr(createBitSet(chipParameters.getTvrApproveDecline()));
        }
        return builder.build();

    }

    public BitSet createBitSet(byte[] byteArray) {

        byte[] updatedByteArray = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            updatedByteArray[i] = reverseByte(byteArray[i]);
        }
        return BitSet.valueOf(updatedByteArray);

    }

    private byte reverseByte(byte x) {
        int intSize = 8;
        byte y = 0;
        for (int position = intSize - 1; position >= 0; position--) {
            y += ((x & 1) << position);
            x >>= 1;
        }
        return y;
    }


    public ChipParams fetchChipParams(String chipVersion, Strategy strategy){

        return chipParamsMap.get(ChipKey.builder().strategy(strategy).chipVersion(chipVersion).build());

    };


    public Optional<ServiceResponse> validateCvrAndTvr(BitSet cvr, String tvrString, String chipVersion, Strategy strategy){

        ChipParams chipParams = chipParamsMap.get(ChipKey.builder().chipVersion(chipVersion).strategy(strategy).build());

        Optional<ServiceResponse> serviceResponseOptional = validateCVR(cvr,chipParams);

        if(serviceResponseOptional.isPresent()){
            return serviceResponseOptional;
        }

        byte[] tvrBytes = stringToHex(tvrString,tvrString.length(),"0");
        BitSet tvr = createBitSet(tvrBytes);

        return validateTVR(tvr,chipParams);

    }

    public Optional<ServiceResponse> validateCVR(BitSet cvr, ChipParams chipParams){


        if(!chipParams.isDecideBasedOnCvr()){
            return Optional.empty();
        }
        int nexBitSet = 0;
        while ((nexBitSet = cvr.nextSetBit(nexBitSet)) != -1){
            if(chipParams.getBitSetCvr().get(nexBitSet)){
                return Optional.of(ServiceResponse.BLOCKED_CVR);
            }
            nexBitSet = nexBitSet + 1;
        }


        return Optional.empty();


    };
    public Optional<ServiceResponse> validateTVR(BitSet tvr, ChipParams chipParams){


        if(!chipParams.isDecideBasedOnTvr()){
            return Optional.empty();
        }


        int nexBitSet = 0;
        while ((nexBitSet = tvr.nextSetBit(nexBitSet)) != -1){
            if(chipParams.getBitSetTvr().get(nexBitSet)){
                return Optional.of(ServiceResponse.BLOCKED_TVR);
            }
            nexBitSet = nexBitSet + 1;
        }
        return Optional.empty();
    }

    public byte[] stringToHex(String input, int length, String padValue) {
        String stringValue = input;
        if ((length % 2) == 1) {
            length = length + 1;
        }

        if (stringValue.length() < length) {
            String padString = padValue.repeat(length - stringValue.length());
            StringBuilder sb = new StringBuilder();
            sb.append(padString).append(stringValue);
            stringValue = sb.toString();

        }
        return BaseEncoding.base16().decode(stringValue);

    }

}
