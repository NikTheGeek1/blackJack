class ResponseOptions {
    static POSTResponse(bodyObj) {
        return {
            method: "POST",
            headers: {
                "Application": "application/json",
                "Content-Type": "application/json"
            },
            body: bodyObj && JSON.stringify(bodyObj)
        };
    }
}

export default ResponseOptions;