package in.nmaloth.identifierValidator.config;

import in.nmaloth.identifierValidator.model.entity.temp.AccountTempBalance;
import in.nmaloth.identifierValidator.model.entity.temp.CardTempBalance;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder( schemaPackageName = "in.nmaloth.identifierValidator.model.entity.temp",includeClasses = { CardTempBalance.class, AccountTempBalance.class})
public interface ProtocSchemaMarshaller extends GeneratedSchema {
}
