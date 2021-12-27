package Utils;

public class UserNaoExisteException extends Exception{
    public UserNaoExisteException(){
        super("User nao existe !");
    }
}
