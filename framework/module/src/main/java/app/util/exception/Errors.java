package app.util.exception;

import org.slf4j.helpers.MessageFormatter;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author chi
 */
public interface Errors {
    static RuntimeException notFound(String message, Object... args) {
        NotFoundException e = new NotFoundException(MessageFormatter.arrayFormat(message, args).getMessage());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.initCause((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static RuntimeException badRequest(String message, Object... args) {
        BadRequestException e = new BadRequestException(MessageFormatter.arrayFormat(message, args).getMessage());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.initCause((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static RuntimeException badRequest(String field, String message, Object... args) {
        String formattedMessage = MessageFormatter.arrayFormat(message, args).getMessage();
        BadRequestExceptionResponse badRequestExceptionResponse = new BadRequestExceptionResponse();
        badRequestExceptionResponse.field = field;
        badRequestExceptionResponse.message = formattedMessage;
        BadRequestException e = new BadRequestException(formattedMessage, Response.status(400).entity(badRequestExceptionResponse).build());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.initCause((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static RuntimeException internalError(String message, Object... args) {
        WebApplicationException e = new WebApplicationException(MessageFormatter.arrayFormat(message, args).getMessage());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.addSuppressed((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static RuntimeException internalError(Throwable throwable) {
        return new WebApplicationException(throwable);
    }

    static RuntimeException forbidden(String message, Object... args) {
        ForbiddenException e = new ForbiddenException(MessageFormatter.arrayFormat(message, args).getMessage());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.initCause((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static RuntimeException notAuthorized(String message, Object... args) {
        NotAuthorizedException e = new NotAuthorizedException(MessageFormatter.arrayFormat(message, args).getMessage());
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            e.initCause((Throwable) args[args.length - 1]);
        }
        return e;
    }

    static String stackTrace(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    static BadRequestException badRequestException(String field, String message) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse();
        response.field = field;
        response.message = message;
        return new BadRequestException(message, Response.status(400).entity(response).build());
    }

    static ExceptionResponse response(Throwable e) {
        ExceptionResponse response = new ExceptionResponse();
        response.message = e.getMessage();
        return response;
    }

    static BadRequestExceptionResponse response(BadRequestException e) {
        Response response = e.getResponse();
        if (response != null) {
            Object entity = response.getEntity();
            if (entity instanceof BadRequestExceptionResponse) {
                return (BadRequestExceptionResponse) entity;
            }
        }
        BadRequestExceptionResponse badRequestExceptionResponse = new BadRequestExceptionResponse();
        badRequestExceptionResponse.message = e.getMessage();
        return badRequestExceptionResponse;
    }
}
