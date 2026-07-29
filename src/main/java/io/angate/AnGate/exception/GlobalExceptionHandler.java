package io.angate.AnGate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLockFailure(ObjectOptimisticLockingFailureException e){
        ApiError apiError = ApiError.builder()
                .Message("Another user booked this ticket before you. Please try again.")
                .httpStatus(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.CONFLICT);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> ResourceNotFoundException(ResourceNotFoundException e){
        ApiError apiError = ApiError.builder()
                .Message(e.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(BookingExistsDeletionException.class)
    public ResponseEntity<ApiError> ExistingBookingsDeletion(BookingExistsDeletionException e){
        ApiError apiError = ApiError.builder()
                .Message(e.getMessage())
                .httpStatus(HttpStatus.CONFLICT)
                .build();
        return new ResponseEntity<>(apiError , HttpStatus.CONFLICT);
    }



}
