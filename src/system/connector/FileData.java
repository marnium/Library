package system.connector;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileData {
    private static final String file_name = "ServerConf.m2a";

    public static void write(DataConnector data) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file_name));
        out.writeObject(data);
        out.close();
    }

    public static DataConnector read() throws IOException {
        DataConnector data = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file_name));
            data = (DataConnector) in.readObject();
            in.close();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
}