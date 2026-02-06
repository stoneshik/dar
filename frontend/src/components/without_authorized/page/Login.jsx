import superagent from "superagent";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";
import { responseMessageHandlerForFormError, responseMessageHandlerForFormResult } from "../../../responseHandlers";


export function Login() {
    const navigate = useNavigate();
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const changeHandling = (event, functionForSetValue) => {
        functionForSetValue(event.target.value);
        if (event.target.value) {
            event.target.classList.add('inputting_field');
        } else {
            event.target.classList.remove('inputting_field');
        }
    };
    const validateForm = async () => {
        if (!login) {
            setErrorMessage('Введите логин');
            return false;
        }
        if (!password) {
            setErrorMessage('Введите пароль');
            return false;
        }
        if (login.length > 15) {
            setErrorMessage('Длина логина не должна превышать 15 символов');
            return false;
        }
        if (login.length < 2) {
            setErrorMessage('Длина логина должна быть минимум 2 символа');
            return false;
        }
        if (password.length < 5) {
            setErrorMessage('Длина пароля должна быть минимум 5 символов');
            return false;
        }
        const regex = new RegExp('[a-zA-Z0-9]+');
        if (!regex.test(login)) {
            setErrorMessage('Логин должен быть написан латинскими буквами, могут использоваться цифры');
            return false;
        }
        if (!regex.test(password)) {
            setErrorMessage('Пароль должен быть написан латинскими буквами,могут использоваться цифры');
            return false;
        }
        let isValid = false;
        await superagent
            .post('/api/open/auth')
            .send({"login": login, "password": password})
            .set('Content-Type', 'application/json')
            .then(
                (result) => {
                    isValid = responseMessageHandlerForFormResult(result, setErrorMessage, setSuccessMessage);
                }
            )
            .catch(
                (err) => {
                    isValid = responseMessageHandlerForFormError(err, setErrorMessage, setSuccessMessage);
                }
            );
        return isValid;
    };
    const formHandling = async (event) => {
        event.preventDefault();
        if (!await validateForm()) {
            return false;
        }
        setTimeout(() => navigate('/main'), 1000);
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div>
                    <form className="ui-form main-form" id="login_form" onSubmit={formHandling}>
                        <div className="form-row">
                            <input type="text" id="login-field" required autoComplete="off"
                                   name="login"
                                   onChange={(e) => changeHandling(e, setLogin)}/>
                            <label htmlFor="login-field" className="text-input-label">Логин:</label>
                        </div>
                        <div className="form-row">
                            <input type="password" id="password-field" required autoComplete="off"
                                   name="password"
                                   onChange={(e) => changeHandling(e, setPassword)}/>
                            <label htmlFor="password-field" className="text-input-label">Пароль:</label>
                        </div>
                        <input type="submit" value="Войти"/>
                        <div className="error">{errorMessage}</div>
                        <div className="success-text">{successMessage}</div>
                    </form>
                    <form className="ui-form second-form" onSubmit={() => navigate('/register')}>
                        <input type="submit" value="Зарегистрироваться"/>
                    </form>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
