package com.service.boot.utils.io;

import java.io.Closeable;
import java.io.IOException;

public class IOClose {

    public static void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

}
