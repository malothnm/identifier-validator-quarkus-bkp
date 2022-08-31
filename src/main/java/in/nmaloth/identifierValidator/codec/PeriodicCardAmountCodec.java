package in.nmaloth.identifierValidator.codec;

import com.mongodb.MongoClientSettings;
import in.nmaloth.payments.constants.card.LimitType;
import in.nmaloth.payments.schemas.PeriodicCardAmount;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class PeriodicCardAmountCodec implements CollectibleCodec<PeriodicCardAmount> {

    private final Codec<Document> documentCodec;

    public PeriodicCardAmountCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public PeriodicCardAmount generateIdIfAbsentFromDocument(PeriodicCardAmount periodicCardAmount) {
        if (!documentHasId(periodicCardAmount)) {
            periodicCardAmount.setLimitType(LimitType.NO_SPECIFIC.getLimitType());
        }
        return periodicCardAmount;    }

    @Override
    public boolean documentHasId(PeriodicCardAmount periodicCardAmount) {
        return periodicCardAmount.getLimitType() != null;
    }

    @Override
    public BsonValue getDocumentId(PeriodicCardAmount periodicCardAmount) {
        return new BsonString(periodicCardAmount.getLimitType());
    }

    @Override
    public PeriodicCardAmount decode(BsonReader bsonReader, DecoderContext decoderContext) {

        Document document = documentCodec.decode(bsonReader, decoderContext);
        PeriodicCardAmount periodicCardAmount = new PeriodicCardAmount();
        if (document.getString("limitType") != null) {
            periodicCardAmount.setLimitType(document.getString("limitType"));
        }
        periodicCardAmount.setTransactionAmount(document.getLong("transactionAmount"));
        periodicCardAmount.setTransactionNumber(document.getInteger("transactionNumber"));
        return periodicCardAmount;
    }

    @Override
    public void encode(BsonWriter bsonWriter, PeriodicCardAmount periodicCardAmount, EncoderContext encoderContext) {

        Document doc = new Document();
        doc.put("limitType", periodicCardAmount.getLimitType());
        doc.put("transactionAmount", periodicCardAmount.getTransactionAmount().longValue());
        doc.put("transactionNumber", periodicCardAmount.getTransactionNumber().intValue());

        documentCodec.encode(bsonWriter, doc, encoderContext);

    }

    @Override
    public Class<PeriodicCardAmount> getEncoderClass() {
        return PeriodicCardAmount.class;
    }
}
