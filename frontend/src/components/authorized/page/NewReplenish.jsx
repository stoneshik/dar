import superagent from "superagent";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";
import { responseMessageHandlerForFormError, responseMessageHandlerForFormResult } from "../../../responseHandlers";


export function NewReplenish() {
    const navigate = useNavigate();
    const [replenishAmount, setReplenishAmount] = useState('');
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
        if (replenishAmount === '') {
            setErrorMessage('Введите сумму пополнения');
            return false;
        }
        const amount = Number(replenishAmount);
        if (isNaN(amount)) {
            setErrorMessage('Должно быть введено число');
            return false;
        }
        if (amount < 0) {
            setErrorMessage('Число должно быть положительным');
            return false;
        }
        let isValid = false;
        await superagent
            .post('/api/replenish/new')
            .send({"replenishAmount": amount})
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
                    <form className="ui-form main-form" id="replenish_form" onSubmit={formHandling}>
                        <div className="form-row">
                            <input type="text" id="replenish" required autoComplete="off"
                                   name="replenish"
                                   onChange={(e) => changeHandling(e, setReplenishAmount)}/>
                            <label htmlFor="replenish" className="text-input-label">Сумма пополнения:</label>
                        </div>
                        <input type="submit" value="Пополнить счет"/>
                        <div className="error">{errorMessage}</div>
                        <div className="success-text">{successMessage}</div>
                    </form>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
