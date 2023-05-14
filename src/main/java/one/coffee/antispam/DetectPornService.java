package one.coffee.antispam;

import java.io.IOException;


public interface DetectPornService {

    boolean hasPornOnImage(String url) throws IOException, InterruptedException;

}
