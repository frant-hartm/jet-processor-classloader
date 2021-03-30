package org.example.jet.hz3context;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.nio.IOUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public final class Utils {

    public static ClientConfig xmlToConfig(String xmlConfig) {
        Path tempFile = null;
        ClientConfig var3;
        try {
            tempFile = Files.createTempFile("client-config", ".xml");
            Files.write(tempFile, xmlConfig.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
            XmlClientConfigBuilder configBuilder = new XmlClientConfigBuilder(tempFile.toFile());
            var3 = configBuilder.build();
        } catch (IOException var7) {
            throw new RuntimeException(var7);
        } finally {
            deleteQuietly(tempFile);
        }
        return var3;
    }

    private static void deleteQuietly(Path path) {
        if (path != null) {
            IOUtil.deleteQuietly(path.toFile());
        }
    }
}
