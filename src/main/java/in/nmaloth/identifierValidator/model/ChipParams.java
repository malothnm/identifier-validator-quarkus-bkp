package in.nmaloth.identifierValidator.model;

import java.util.BitSet;

public class ChipParams {

    private boolean decideBasedOnTvr;
    private BitSet bitSetTvr;
    private boolean decideBasedOnCvr;
    private BitSet bitSetCvr;

    public ChipParams(boolean decideBasedOnTvr, BitSet bitSetTvr, boolean decideBasedOnCvr, BitSet bitSetCvr) {
        this.decideBasedOnTvr = decideBasedOnTvr;
        this.bitSetTvr = bitSetTvr;
        this.decideBasedOnCvr = decideBasedOnCvr;
        this.bitSetCvr = bitSetCvr;
    }

    public ChipParams() {
    }

    public boolean isDecideBasedOnTvr() {
        return decideBasedOnTvr;
    }

    public void setDecideBasedOnTvr(boolean decideBasedOnTvr) {
        this.decideBasedOnTvr = decideBasedOnTvr;
    }

    public BitSet getBitSetTvr() {
        return bitSetTvr;
    }

    public void setBitSetTvr(BitSet bitSetTvr) {
        this.bitSetTvr = bitSetTvr;
    }

    public boolean isDecideBasedOnCvr() {
        return decideBasedOnCvr;
    }

    public void setDecideBasedOnCvr(boolean decideBasedOnCvr) {
        this.decideBasedOnCvr = decideBasedOnCvr;
    }

    public BitSet getBitSetCvr() {
        return bitSetCvr;
    }

    public void setBitSetCvr(BitSet bitSetCvr) {
        this.bitSetCvr = bitSetCvr;
    }

    public static ChipParamsBuilder builder(){
        return new ChipParamsBuilder();
    }

    public static class ChipParamsBuilder {

        private boolean decideBasedOnTvr;
        private BitSet bitSetTvr;
        private boolean decideBasedOnCvr;
        private BitSet bitSetCvr;


        public ChipParamsBuilder decideBasedOnTvr(boolean decideBasedOnTvr){
            this.decideBasedOnTvr = decideBasedOnTvr;
            return this;
        }
        public ChipParamsBuilder bitSetTvr(BitSet bitSetTvr){
            this.bitSetTvr = bitSetTvr;
            return this;
        }
        public ChipParamsBuilder decideBasedOnCvr(boolean decideBasedOnCvr){
            this.decideBasedOnCvr = decideBasedOnCvr;
            return this;
        }

        public ChipParamsBuilder bitSetCvr(BitSet bitSetCvr){
            this.bitSetCvr = bitSetCvr;
            return this;
        }

        public ChipParams build(){
            return new ChipParams(decideBasedOnTvr,bitSetTvr,decideBasedOnCvr,bitSetCvr);
        }
    }
}
