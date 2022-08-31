package in.nmaloth.identifierValidator.model.entity.product;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class BlockingValue {


    @BsonProperty("international")
    private String internationalApplied;
    @BsonProperty("value")
    private String value;

    public String getInternationalApplied() {
        return internationalApplied;
    }

    public void setInternationalApplied(String internationalApplied) {
        this.internationalApplied = internationalApplied;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
