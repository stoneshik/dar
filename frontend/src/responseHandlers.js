export const responseMessageHandlerForFormResult = (result, setErrorMessage, setSuccessMessage) => {
    setErrorMessage('');
    const responseMessage = result.body['responseMessage'];
    if (responseMessage === undefined) {
        return false;
    }
    setSuccessMessage(responseMessage);
    return true;
};
export const responseMessageHandlerForFormError = (err, setErrorMessage, setSuccessMessage) => {
    setSuccessMessage('');
    const responseMessage = err.response.body['responseMessage'];
    if (responseMessage === undefined) {
        return false;
    }
    setErrorMessage(responseMessage);
    return false;
};