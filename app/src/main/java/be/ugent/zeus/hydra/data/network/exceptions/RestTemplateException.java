package be.ugent.zeus.hydra.data.network.exceptions;

/**
 * Exception thrown when the rest template could not be made.
 *
 * @author Niko Strijbol
 */
public class RestTemplateException extends RequestFailureException {

    public RestTemplateException(Throwable cause) {
        super(cause);
    }
}