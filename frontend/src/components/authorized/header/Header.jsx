import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import superagent from "superagent";
import Cookies from "js-cookie";

export function Header() {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');
    const [login, setLogin] = useState('');
    const [balance, setBalance] = useState(0.0);
    useEffect(() => {
        superagent
            .get('/api/account/get_balance')
            .set('Content-Type', 'application/json')
            .then((result) => {
                    const userLogin = result.body['userLogin'];
                    const accountBalance = result.body['accountBalance'];
                    if (userLogin === undefined || accountBalance === undefined) {
                        return false;
                    }
                    setErrorMessage('');
                    setLogin(userLogin);
                    setBalance(accountBalance);
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
    const logout = async () => {
        await superagent
            .post('/api/logout')
            .set('Content-Type', 'application/json')
            .then((result) => {return true;})
            .catch((err) => {
                    const responseMessage = err.response.body['responseMessage'];
                    if (responseMessage === undefined) {
                        return false;
                    }
                    setErrorMessage(responseMessage);
                    return false;
                }
            );
        const jsessionId = 'JSESSIONID';
        if (Cookies.get(jsessionId)) {
            Cookies.remove(jsessionId);
        }
        navigate('/');
    };
    return (
        <div id="header" className="container">
            <div className="container"><Link to="/main" className="none-underline"><h2>TypoFast</h2></Link></div>
            <div className="container">
                <ul className="menu row">
                    <li><NavLink to="#">помощь</NavLink></li>
                    <li><NavLink to="/map">мы на карте</NavLink></li>
                    <li><NavLink to="/files">файлы</NavLink></li>
                    <li><NavLink to="/cart">корзина</NavLink></li>
                    <li id="hamburger">
                        <div className="description">аккаунт</div>
                        <div className="hamburger-wrapper">
                            <ul>
                                <li><Link id="login" to="#">{errorMessage}{login}</Link></li>
                                <li><Link to="#">{errorMessage}{balance} руб.</Link></li>
                                <li><Link to="/replenishes">Пополнения</Link></li>
                                <li><Link to="/new_replenish">Пополнить счет</Link></li>
                                <li><Link to="#" onClick={logout}>Выйти</Link></li>
                            </ul>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    );
}