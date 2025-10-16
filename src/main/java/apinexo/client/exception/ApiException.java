package apinexo.client.exception;

public class ApiException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -2926695552251487943L;

    public ApiException(String message) {
        super(message);
    }
}