class CustomError {
    constructor(errorMessage) {
        this.errorMessage = errorMessage;
    }
}

class ErrorHandling {

    static simpleErrorHandler(response) {
        if (response.errorMessage) throw new CustomError(response.errorMessage);
    }
}

export default ErrorHandling;