import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import superagent from "superagent";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";


export function OrderPrint() {
    const { id } = useParams();
    const [errorMessage, setErrorMessage] = useState('');
    const [orderInfo, setOrderInfo] = useState({});
    const [filesInfo, setFilesInfo] = useState([]);
    useEffect(() => {
        superagent
            .get('/api/order/get_print/' + id)
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const response = result.body;
                    if (response === undefined) {
                        return false;
                    }
                    const order = response['orderInfo'];
                    const files = response['filesInfo'];
                    if (order === undefined || files === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setOrderInfo(order);
                    setFilesInfo(files);
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
                <td className="num"><Link to={"/order_print/" + orderId} title="Подробнее о заказе">{orderNum}</Link>
                </td>
                <td>{orderTypeString}</td>
                <td>{orderStatusString}</td>
                <td>{orderDatetime}</td>
                <td>{orderAmount} руб.</td>
                <td className="address">{orderAddress}</td>
            </tr>
        );
    };
    const createFileElement = (fileInfo) => {
        const fileId = fileInfo['fileId'];
        const userId = fileInfo['userId'];
        const fileName = fileInfo['fileName'];
        const fileLoadDatetime = fileInfo['fileLoadDatetime'];
        if (fileId === undefined ||
            userId === undefined ||
            fileName === undefined ||
            fileLoadDatetime === undefined) {
            return;
        }
        return (
            <div className="file">
                <img src={"/img/file.png"} alt="printer"/>
                <Link to={"/file/" + fileId}>{fileName}</Link>
                <div>{fileLoadDatetime}</div>
            </div>
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
                            <thead>
                            <tr>
                                <th>Номер заказа</th>
                                <th>Тип заказа</th>
                                <th>Статус</th>
                                <th>Дата заказа</th>
                                <th>Сумма заказа</th>
                                <th>Адрес</th>
                            </tr>
                            </thead>
                            <tbody>
                            {createOrderRow(orderInfo)}
                            </tbody>
                        </table>
                    </div>
                    <div id="files" className="container column">
                        <h3>Приложенные файлы</h3>
                        <div className="container files">
                            {filesInfo.map(fileInfo => createFileElement(fileInfo))}
                        </div>
                    </div>
                    <div className="error">{errorMessage}</div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}
