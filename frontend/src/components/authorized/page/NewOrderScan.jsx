import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import superagent from "superagent";
import { YMaps, Map, Placemark } from "@pbe/react-yandex-maps";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";
import {responseMessageHandlerForFormError, responseMessageHandlerForFormResult} from "../../../responseHandlers";



export function NewOrderScan() {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [vendingPoints, setVendingPoints] = useState([]);
    const [selectedVendingPoint, setSelectedVendingPoint] = useState({});
    const [numField, setNumField] = useState('');
    const [amount, setAmount] = useState(0);
    useEffect(() => {
        superagent
            .get('/api/vending_point/get_scan')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const responseVendingPoints = result.body;
                    if (responseVendingPoints === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setVendingPoints(responseVendingPoints);
                    setSelectedVendingPoint(responseVendingPoints[0]);
                    return true;
                }
            )
            .catch((err) => {
                    const responseMessage = err.response.body['responseMessage'];
                    if (responseMessage === undefined) {
                        return false;
                    }
                    setErrorMessage(responseMessage);
                    return false;
                }
            );
    }, []);
    const createPlacemark = (vendingPoint) => {
        const vendingPointCords = vendingPoint['vendingPointCords'];
        const vendingPointAddress = vendingPoint['vendingPointAddress'];
        if (vendingPointCords === undefined || vendingPointAddress === undefined) {
            return;
        }
        return <Placemark modules={["geoObject.addon.balloon"]} defaultGeometry={vendingPointCords}
                          properties={{balloonContent: "Адрес: " + vendingPointAddress,}}/>;
    };
    const createOptionAddress = (vendingPoint, index) => {
        const vendingPointAddress = vendingPoint['vendingPointAddress'];
        if (vendingPointAddress === undefined) {
            return;
        }
        if (index === 0) {
            return <option selected>{vendingPointAddress}</option>;
        }
        return <option>{vendingPointAddress}</option>;
    };
    const changeHandlingInputText = (event, functionForSetValue) => {
        functionForSetValue(event.target.value);
        if (event.target.value) {
            event.target.classList.add('inputting_field');
        } else {
            event.target.classList.remove('inputting_field');
        }
    };
    const changeHandlingNumField = (event) => {
        changeHandlingInputText(event, setNumField);
        const priceOnePage = 0.5;
        const newAmount = Number(event.target.value) * priceOnePage;
        setAmount(newAmount);
    };
    const changeHandlingSelect = (event) => {
        const address = event.target.value;
        for (let i = 0; i < vendingPoints.length; i++) {
            const vendingPoint = vendingPoints[i];
            const addressVendingPoint = vendingPoint['vendingPointAddress'];
            if (addressVendingPoint === address) {
                setSelectedVendingPoint(vendingPoint);
                return;
            }
        }
    };
    const validateForm = async () => {
        if (!selectedVendingPoint || selectedVendingPoint === {}) {
            setErrorMessage('Выберите адрес');
            return false;
        }
        if (!numField) {
            setErrorMessage('Введите количество страниц');
            return false;
        }
        const scanTaskNumberPages = Number(numField);
        if (isNaN(scanTaskNumberPages)) {
            setErrorMessage('Должно быть введено число');
            return false;
        }
        if (scanTaskNumberPages < 0) {
            setErrorMessage('Число должно быть положительным');
            return false;
        }
        if (!Number.isInteger(scanTaskNumberPages)) {
            setErrorMessage('Введенное число должно быть целочисленным');
            return false;
        }
        let isValid = false;
        const vendingPointId = selectedVendingPoint['vendingPointId'];
        if (vendingPointId === undefined) {
            setErrorMessage('Что-то пошло не так перезагрузите страницу...');
            return false;
        }
        await superagent
            .post('/api/order/create/scan_order')
            .send({"vendingPointId": vendingPointId, "scanTaskNumberPages": scanTaskNumberPages})
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
                <div id="new_order_wrapper" className="container">
                    <form className="ui-form main-form" id="new_order_form" onSubmit={formHandling}>
                        <h3>Заказ на сканирование</h3>
                        <div className="form-row">
                            <label htmlFor="address">Выбор места:</label>
                            <select id="address" name="address" size="3" className="select"
                                    onChange={(e) => changeHandlingSelect(e)}>
                                {vendingPoints.map((vendingPoint, index) => createOptionAddress(vendingPoint, index))}
                            </select>
                        </div>
                        <div className="form-row">
                            <input type="text" id="num_field" required autoComplete="off"
                                   name="num_field"
                                   onChange={(e) => changeHandlingNumField(e)}/>
                            <label htmlFor="num_field" className="text-input-label">Число страниц:</label>
                        </div>
                        <div className="form-row" id="pay_button">
                            <p>Сумма заказа: <strong id="amount">{amount} руб.</strong></p>
                            <input type="submit" value="Добавить в корзину"/>
                        </div>
                        <div className="error">{errorMessage}</div>
                        <div className="success-text">{successMessage}</div>
                    </form>
                    <YMaps>
                        <div>
                            <Map defaultState={{
                                center: [59.938678, 30.314474], zoom: 10,
                                controls: ["zoomControl", "fullscreenControl"],}} width={800} height={500}
                                 modules={["control.ZoomControl", "control.FullscreenControl"]}>
                                <div>
                                    {vendingPoints.map(vendingPoint => (createPlacemark(vendingPoint)))}
                                </div>
                            </Map>
                        </div>
                    </YMaps>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
