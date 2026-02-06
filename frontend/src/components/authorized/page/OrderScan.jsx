import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import superagent from "superagent";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";


export function OrderScan() {
    const { id } = useParams();
    const [errorMessage, setErrorMessage] = useState('');
    const [orderInfo, setOrderInfo] = useState({});
    const [numberPages, setNumberPages] = useState(0);
    useEffect(() => {
        superagent
            .get('/api/order/get_scan/' + id)
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const response = result.body;
                    if (response === undefined) {
                        return false;
                    }
                    const order = response['orderInfo'];
                    const rawNumberPages = response['numberPages'];
                    if (order === undefined || rawNumberPages === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setOrderInfo(order);
                    setNumberPages(rawNumberPages);
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
    const createOrderRow = (order) => {
        const orderId = order['orderId'];
        const accountId = order['accountId'];
        const orderAddress = order['orderAddress'];
        const orderAmount = order['orderAmount'];
        const orderDatetime = order['orderDatetime'];
        const orderType = order['orderType'];
        const orderStatus = order['orderStatus'];
        const orderNum = order['orderNum'];
        if (orderId === undefined ||
            accountId === undefined ||
            orderAddress === undefined ||
            orderAmount === undefined ||
            orderDatetime === undefined ||
            orderType === undefined ||
            orderStatus === undefined ||
            orderNum === undefined) {
            return;
        }
        const orderTypeString = (orderType === 'PRINT')? 'печать': 'сканирование';
        let orderStatusString = '';
        if (orderStatus === 'NOT_PAID') {
            orderStatusString = 'Не оплачен';
        } else if (orderStatus === 'PAID') {
            orderStatusString = 'Оплачен';
        } else if (orderStatus === 'COMPLETED') {
            orderStatusString = 'Завершен';
        } else {
            return;
        }
        return (
            <tr>
                <td className="num"><Link to={"/order_scan/" + orderId} title="Подробнее о заказе">{orderNum}</Link>
                </td>
                <td>{orderTypeString}</td>
                <td>{orderStatusString}</td>
                <td>{orderDatetime}</td>
                <td>{orderAmount} руб.</td>
                <td className="address">{orderAddress}</td>
            </tr>
        );
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div id="content">
                    <div id="orders">
                        <h3>Общая информация о заказе</h3>
                        <table>
                            <tbody>
                            <tr>
                                <th>Номер заказа</th>
                                <th>Тип заказа</th>
                                <th>Статус</th>
                                <th>Дата заказа</th>
                                <th>Сумма заказа</th>
                                <th>Адрес</th>
                            </tr>
                            {createOrderRow(orderInfo)}
                            </tbody>
                        </table>
                    </div>
                    <div className="container column">
                        <h3>Было заказано {numberPages} страниц</h3>
                    </div>
                    <div className="error">{errorMessage}</div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
