package in.nmaloth.identifierValidator.codec;

import com.mongodb.MongoClientSettings;
import in.nmaloth.identifierValidator.model.entity.product.MCCRange;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class MCCRangeCodec implements CollectibleCodec<MCCRange> {

    private final Codec<Document> documentCodec;

    public MCCRangeCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public MCCRange generateIdIfAbsentFromDocument(MCCRange mccRange) {
        if (!documentHasId(mccRange)) {
        }
        return mccRange;
    }

    @Override
    public boolean documentHasId(MCCRange mccRange) {
        return true;
    }

    @Override
    public BsonValue getDocumentId(MCCRange mccRange) {
        return new BsonInt32(mccRange.getMccStart());
    }

    @Override
    public MCCRange decode(BsonReader bsonReader, DecoderContext decoderContext) {

        Document document = documentCodec.decode(bsonReader, decoderContext);
        MCCRange mccRange = new MCCRange();

        Integer start = document.getInteger("start");
        if(start != null){
            mccRange.setMccStart(start);
        }

        Integer end = document.getInteger("end");
        if(end != null){
            mccRange.setMccEnd(end);
        }
        return mccRange;

    }

    @Override
    public void encode(BsonWriter bsonWriter, MCCRange mccRange, EncoderContext encoderContext) {


        Document doc = new Document();
        doc.put("start", mccRange.getMccStart());
        doc.put("end", mccRange.getMccEnd());

        documentCodec.encode(bsonWriter, doc, encoderContext);

    }

    @Override
    public Class<MCCRange> getEncoderClass() {
        return MCCRange.class;
    }
}
