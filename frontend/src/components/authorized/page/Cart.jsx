import superagent from "superagent";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";
import { responseMessageHandlerForFormError, responseMessageHandlerForFormResult } from "../../../responseHandlers";


export function Cart() {
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [orders, setOrders] = useState([]);
    const [checkedOrders] = useState({});
    const [amount, setAmount] = useState(0.0);
    const [updateVar, setUpdate] = useState(0);
    useEffect(() => {
        superagent
            .get('/api/order/get_not_paid')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const responseOrders = result.body;
                    if (responseOrders === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setOrders(responseOrders);
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
    const updateChecked = (orderNum, orderAmount) => {
        checkedOrders[orderNum] = !checkedOrders[orderNum];
        if (checkedOrders[orderNum]) {
            setAmount(amount + orderAmount);
            return;
        }
        setAmount(amount - orderAmount);
    };
    const removeRow = (orderNum, orderAmount) => {
        for (let i = 0; i < orders.length; i++) {
            const order = orders[i];
            if (order['orderNum'] === orderNum) {
                if (checkedOrders[orderNum]) {
                    setAmount(amount - orderAmount);
                }
                orders.splice(i, 1);
                delete checkedOrders[orderNum];
                setUpdate(updateVar + 1);
                return;
            }
        }
    };
    const handlingRemoveRow = async (orderNum, orderAmount, orderId) => {
        const result = window.confirm("Вы уверены что хотите удалить заказ?");
        if (!result) {
            return;
        }
        removeRow(orderNum, orderAmount);
        let isValid = false;
        await superagent
            .delete('/api/order/remove/' + orderId)
            .set('Content-Type', 'application/json')
            .then((result) => {
                    isValid = responseMessageHandlerForFormResult(result, setErrorMessage, setSuccessMessage);
                }
            )
            .catch((err) => {
                    isValid = responseMessageHandlerForFormError(err, setErrorMessage, setSuccessMessage);
                }
            );
        if (!isValid) {
            return;
        }
        setTimeout(() => window.location.reload(), 1000);
    };
    const getCheckedOrders = () => {
        const handlingOrderNums = [];
        for (const [orderNum, isChecked] of Object.entries(checkedOrders)) {
            if (isChecked) {
                handlingOrderNums.push(orderNum);
            }
        }
        const handlingOrders = []
        for (let i = 0; i < orders.length; i++) {
            const order = orders[i];
            const orderNum = order['orderNum'];
            if (orderNum === undefined) {
                return [];
            }
            for (let j = 0; j < handlingOrderNums.length; j++) {
                const handlingOrderNum = handlingOrderNums[j];
                if (String(orderNum) === String(handlingOrderNum)) {
                    handlingOrders.push(order);
                    break;
                }
            }
        }
        return handlingOrders;
    };
    const handlingPay = async () => {
        const handlingOrders = getCheckedOrders();
        if (handlingOrders.length < 1) {
            return;
        }
        let isValid = false;
        await superagent
            .post('/api/order/pay')
            .send(handlingOrders)
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
        if (!isValid) {
            return;
        }
        for (let i = 0; i < handlingOrders.length; i++) {
            const handlingOrder = handlingOrders[i];
            const orderNum = handlingOrder['orderNum'];
            const orderAmount = handlingOrder['orderAmount'];
            if (orderNum === undefined || orderAmount === undefined) {
                continue;
            }
            removeRow(orderNum, orderAmount);
        }
        setTimeout(() => window.location.reload(), 1000);
    };
    const createRowTable = (order) => {
        const orderId = order['orderId'];
        const orderNum = order['orderNum'];
        const orderType = order['orderType'];
        const orderDate = order['orderDatetime'];
        const orderAmount = order['orderAmount'];
        const orderAddress = order['orderAddress'];
        if (orderId === undefined ||
            orderNum === undefined ||
            orderType === undefined ||
            orderDate === undefined ||
            orderAmount === undefined ||
            orderAddress === undefined) {
            return;
        }
        const orderTypeString = (orderType === 'PRINT')? 'печать': 'сканирование';
        const orderLinkString = (orderType === 'PRINT')? '/order_print/': '/order_scan/';
        if (checkedOrders[orderNum] === undefined) {
            checkedOrders[orderNum] = false;
        }
        return (
            <tr>
                <td><input type="checkbox" autoComplete="off"
                           onChange={() => {updateChecked(orderNum, orderAmount)}}/></td>
                <td className="num">
                    <Link to={orderLinkString + orderId} title="Подробнее о заказе">{orderNum}</Link>
                </td>
                <td>{orderTypeString}</td>
                <td>{orderDate}</td>
                <td>{orderAmount} руб.</td>
                <td className="address">{orderAddress}</td>
                <td>
                    <img src={"/img/cross.png"} alt="cross" style={{width: "24px"}}
                         onClick={() => handlingRemoveRow(orderNum, orderAmount, orderId)}/>
                </td>
            </tr>
        );
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div id="content">
                    <div id="orders">
                        <h3>Неоплаченные заказы</h3>
                        <table>
                            <thead>
                            <tr>
                                <th></th>
                                <th>Номер заказа</th>
                                <th>Тип заказа</th>
                                <th>Дата заказа</th>
                                <th>Сумма заказа</th>
                                <th>Адрес</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            {orders.map(order => (createRowTable(order)))}
                            </tbody>
                        </table>
                    </div>
                    <div id="total" className="container">
                        <h3>Итого: <span id="amount">{amount}</span> руб.</h3>
                        <div className="button" onClick={handlingPay}>Оплатить</div>
                    </div>
                    <div className="error">{errorMessage}</div>
                    <div className="success-text">{successMessage}</div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
