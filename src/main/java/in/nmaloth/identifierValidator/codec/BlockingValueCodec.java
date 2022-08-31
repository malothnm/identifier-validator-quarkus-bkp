package in.nmaloth.identifierValidator.codec;

import com.mongodb.MongoClientSettings;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.identifierValidator.model.entity.product.BlockingValue;
import in.nmaloth.payments.constants.account.BalanceTypes;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BlockingValueCodec implements CollectibleCodec<BlockingValue> {


    private final Codec<Document> documentCodec;

    public BlockingValueCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }


    @Override
    public BlockingValue generateIdIfAbsentFromDocument(BlockingValue blockingValue) {

        if (!documentHasId(blockingValue)) {

        }
        return blockingValue;
    }

    @Override
    public boolean documentHasId(BlockingValue blockingValue) {
        return blockingValue.getValue() != null;    }

    @Override
    public BsonValue getDocumentId(BlockingValue blockingValue) {
        return  new BsonString(blockingValue.getValue());
    }

    @Override
    public BlockingValue decode(BsonReader bsonReader, DecoderContext decoderContext) {


        Document document = documentCodec.decode(bsonReader, decoderContext);
        BlockingValue blockingValue = new BlockingValue();
        String international = document.getString("international");
        if (international != null) {
            blockingValue.setInternationalApplied(international);
        }
        String value = document.getString("value");
        if(value != null){
            blockingValue.setValue(value);
        }
        return blockingValue;
    }

    @Override
    public void encode(BsonWriter bsonWriter, BlockingValue blockingValue, EncoderContext encoderContext) {

        Document doc = new Document();
        doc.put("international", blockingValue.getInternationalApplied());
        doc.put("value", blockingValue.getValue());

        documentCodec.encode(bsonWriter, doc, encoderContext);

    }

    @Override
    public Class<BlockingValue> getEncoderClass() {
        return BlockingValue.class;
    }
}
