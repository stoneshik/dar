import { useEffect, useState } from "react";
import superagent from "superagent";
import { Header } from "../header/Header";
import { Footer } from "../footer/Footer";

export function Replenishes() {
    const [errorMessage, setErrorMessage] = useState('');
    const [replenishes, setReplenishes] = useState([]);
    useEffect(() => {
        superagent
            .get('/api/replenish/get_all')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const responseReplenishes = result.body;
                    if (responseReplenishes === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setReplenishes(responseReplenishes);
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
    const createRowTable = (replenish, num) => {
        const replenishId = replenish['replenishId'];
        const accountId = replenish['accountId'];
        const replenishAmount = replenish['replenishAmount'];
        const replenishDatetime = replenish['replenishDatetime'];
        if (replenishId === undefined ||
            accountId === undefined ||
            replenishAmount === undefined ||
            replenishDatetime === undefined) {
            return;
        }
        return (
            <tr>
                <td>{num + 1}</td>
                <td>{replenishAmount}</td>
                <td>{replenishDatetime}</td>
            </tr>
        );
    };
    return (
        <div>
            <Header/>
            <div id="wrapper" className="container">
                <div id="content">
                    <div id="orders">
                        <h3>Пополнения</h3>
                        <table>
                            <thead>
                            <tr>
                                <th>Номер</th>
                                <th>Сумма пополнения</th>
                                <th>Дата пополнения</th>
                            </tr>
                            </thead>
                            <tbody>
                            {replenishes.map((replenish, num) => (createRowTable(replenish, num)))}
                            </tbody>
                        </table>
                        <div className="error">{errorMessage}</div>
                    </div>
                </div>
            </div>
            <Footer/>
        </div>
    );
}