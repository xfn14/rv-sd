import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Account {

    private final String username;
    private final String password;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public void serialize(DataOutputStream out) throws IOException{}

    public static Account deserialize(DataInputStream in){return null;}


}
